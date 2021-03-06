/*
 * Copyright 2020 eBlocker Open Source UG (haftungsbeschraenkt)
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be
 * approved by the European Commission - subsequent versions of the EUPL
 * (the "License"); You may not use this work except in compliance with
 * the License. You may obtain a copy of the License at:
 *
 *   https://joinup.ec.europa.eu/page/eupl-text-11-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.eblocker.server.icap.filter.url;

import org.eblocker.server.common.util.UrlUtils;
import org.eblocker.server.icap.filter.Filter;
import org.eblocker.server.icap.filter.FilterLineParser;
import org.eblocker.server.icap.filter.FilterPriority;
import org.eblocker.server.icap.filter.FilterType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlLineParser implements FilterLineParser {

    private static final Pattern URL_PATTERN = Pattern.compile("^(?:https?://)?([^/]+)(/.*)?$");

    @Override
    public Filter parseLine(String line) {
        if (line == null || line.isEmpty() || line.startsWith("#")) {
            return null;
        }

        Matcher matcher = URL_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return null;
        }

        String hostname = matcher.group(1);
        String domain = UrlUtils.getDomain(hostname);

        String matchString = "https?://(?:[a-zA-Z0-9\\-]+\\.)*" + hostname;
        if (matcher.group(2) != null) {
            matchString += matcher.group(2);
        } else {
            matchString += "(:?/|$)";
        }

        return UrlFilterFactory.getInstance()
                .setDefinition(line)
                .setType(FilterType.BLOCK)
                .setPriority(FilterPriority.DEFAULT)
                .setDomain(domain)
                .setStringMatchType(StringMatchType.REGEX)
                .setMatchString(matchString)
                .build();
    }
}

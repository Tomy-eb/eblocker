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
package org.eblocker.server.common.network;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class DhcpUtilsTest {

    @Test
    public void parseDnsServersOption() {
        Assert.assertEquals(Collections.emptyList(), DhcpUtils.parseDnsServersOption(""));
        Assert.assertEquals(Collections.singletonList("192.168.3.3"), DhcpUtils.parseDnsServersOption("192.168.3.3"));
        Assert.assertEquals(Arrays.asList("192.168.3.3", "192.168.3.4"), DhcpUtils.parseDnsServersOption("192.168.3.3, 192.168.3.4"));
        Assert.assertEquals(Arrays.asList("192.168.3.3", "192.168.3.4"), DhcpUtils.parseDnsServersOption("192.168.3.3, 192.168.3.4, 0.0.0.0"));
        Assert.assertEquals(Arrays.asList("192.168.3.3", "192.168.3.4"), DhcpUtils.parseDnsServersOption("192.168.3.3, 192.168.3.4, 0.0.0.0, !!ERROR!!"));
    }

}

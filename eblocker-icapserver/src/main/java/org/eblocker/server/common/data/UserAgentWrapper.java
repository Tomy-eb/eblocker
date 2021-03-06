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
package org.eblocker.server.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAgentWrapper extends ModelObject {

    private String agentName;
    private String agentSpec;
    private Boolean isCustomUserAgent;

    public UserAgentWrapper(@JsonProperty("agentSpec") String agentSpec,
                            @JsonProperty("agentName") String agentName,
                            @JsonProperty("isCustomUserAgent") Boolean isCustomUserAgent) {
        this.agentSpec = agentSpec;
        this.agentName = agentName;
        this.isCustomUserAgent = isCustomUserAgent;
    }

    public String getAgentSpec() {
        return agentSpec;
    }

    public void setAgentSpec(String agentSpec) {
        this.agentSpec = agentSpec;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Boolean getIsCustomUserAgent() {
        return isCustomUserAgent;
    }

    public void setIsCustomUserAgent(Boolean custom) {
        isCustomUserAgent = custom;
    }
}

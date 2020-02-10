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

import org.eblocker.server.common.data.NetworkConfiguration;
import org.eblocker.server.common.data.NetworkStateId;

/**
 * Represents the following network state:
 * The IP address is statically configured.
 * Clients get their addresses from an external DHCP server,
 * e.g. running on the router.
 */
public class NetworkStateExternalDhcp extends NetworkState {

	@Override
	public void onExit(NetworkServices services) {
	}

	@Override
	public void onEntry(NetworkServices services, NetworkConfiguration configuration, boolean willReboot) {
		services.enableStaticIp(configuration);
		services.setNameserverAddresses(configuration);
	}

	@Override
	public NetworkStateId getId() {
		return NetworkStateId.EXTERNAL_DHCP;
	}

}

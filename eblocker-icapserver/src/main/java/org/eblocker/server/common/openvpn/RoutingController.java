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
package org.eblocker.server.common.openvpn;

import org.eblocker.server.icap.resources.EblockerResource;
import org.eblocker.server.icap.resources.ResourceHandler;
import org.eblocker.server.icap.resources.SimpleResource;
import org.eblocker.server.common.system.ScriptRunner;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class RoutingController {
    private static final Logger logger = LoggerFactory.getLogger(RoutingController.class);

    private static final String CUSTOM_ROUTES_MARKER = "# do not edit this or below this line - generated by icapserver";

    private final String ipRouteRtTablesFile;
    private final String ipRouteRtTablesTempFile;
    private final String ipRouteRtTablesTableNamePrefix;
    private final int ipRouteRtTablesOffset;
    private final String reconfigureRoutingTablesScript;
    private final String reconfigurePolicyScript;
    private final String setClientRouteScript;
    private final String clearClientRouteScript;

    private final ScriptRunner scriptRunner;

    private SortedSet<Integer> availableRoutes;
    private SortedSet<Integer> allocatedRoutes;

    @Inject
    public RoutingController(@Named("vpn.iproute2.rttables.file.path") String ipRouteRtTablesFile,
                             @Named("vpn.iproute2.rttables.temp.result.file.path") String ipRouteRtTablesTempFile,
                             @Named("vpn.iproute2.rttables.name.prefix") String ipRouteRtTablesTableNamePrefix,
                             @Named("vpn.iproute2.rttables.offset") int ipRouteRtTablesOffset,
                             @Named("vpn.iproute2.rttables.route.min") int ipRouteRtTablesRouteMin,
                             @Named("vpn.iproute2.rttables.route.max") int ipRouteRtTablesRouteMax,
                             @Named("vpn.reconfigure.routing.tables.command") String reconfigureRoutingTablesScript,
                             @Named("vpn.reconfigure.policy") String reconfigurePolicyScript,
                             @Named("vpn.set.client.route") String setClientRouteScript,
                             @Named("vpn.clear.client.route") String clearClientRouteScript,
                             ScriptRunner scriptRunner) {
        this.ipRouteRtTablesFile = ipRouteRtTablesFile;
        this.ipRouteRtTablesTempFile = ipRouteRtTablesTempFile;
        this.ipRouteRtTablesTableNamePrefix = ipRouteRtTablesTableNamePrefix;
        this.ipRouteRtTablesOffset = ipRouteRtTablesOffset;
        this.reconfigureRoutingTablesScript = reconfigureRoutingTablesScript;
        this.reconfigurePolicyScript = reconfigurePolicyScript;
        this.setClientRouteScript = setClientRouteScript;
        this.clearClientRouteScript = clearClientRouteScript;
        this.scriptRunner = scriptRunner;

        initializeRoutes(ipRouteRtTablesRouteMin, ipRouteRtTablesRouteMax);
        updateRoutes();
    }

    public synchronized Integer createRoute() {
        if (availableRoutes.isEmpty()) {
            return null;
        }

        Iterator<Integer> i = availableRoutes.iterator();
        int route = i.next();
        i.remove();

        allocatedRoutes.add(route);
        updateRoutes();
        return route;
    }

    public synchronized void deleteRoute(Integer route) {
        if (!allocatedRoutes.contains(route)) {
            logger.error("trying to delete unallocated route {}", route);
            return;
        }
        allocatedRoutes.remove(route);
        availableRoutes.add(route);
        updateRoutes();
    }

    public void setClientRoute(int id, String virtualInterfaceName, String routeNetGateway, String routeVpnGateway, String trustedIp) {
        try {
            if (!allocatedRoutes.contains(id)) {
                logger.error("can not set unallocated route {}", id);
                throw new IllegalArgumentException("can not set unallocated route");
            }

            scriptRunner.runScript(setClientRouteScript, String.valueOf(id), virtualInterfaceName, routeNetGateway, routeVpnGateway, trustedIp);
        } catch (IOException e) {
            logger.error("failed to set client {} routes", e);
        } catch (InterruptedException e) {
            logger.error("setClientRoute interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public void clearClientRoute(int id, String trustedIp) {
        if (!allocatedRoutes.contains(id)) {
            logger.error("can not clear unallocated route {}", id);
            throw new IllegalArgumentException("can not clear unallocated route");
        }

        try {
            scriptRunner.runScript(clearClientRouteScript, String.valueOf(id), trustedIp);
        } catch (IOException e) {
            logger.error("failed to clear client {} routes", e);
        } catch (InterruptedException e) {
            logger.error("clearClientRoute interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private void initializeRoutes(int min, int max) {
        availableRoutes = new TreeSet<>();
        for(int i = min; i <= max; ++i) {
            availableRoutes.add(i);
        }
        allocatedRoutes = new TreeSet<>();
    }

    private void updateRoutes() {
        setupRoutingTables();
        setupPolicyBasedRoutes();
    }

    private void setupRoutingTables() {
        List<String> routingTables = readDefaultRoutingTables();

        routingTables.add(CUSTOM_ROUTES_MARKER);
        for (Integer id : allocatedRoutes) {
            routingTables.add(String.format("%d  %s", ipRouteRtTablesOffset + id, ipRouteRtTablesTableNamePrefix + id));
        }

        //write result to temporary file
        ResourceHandler.writeLines(new SimpleResource(ipRouteRtTablesTempFile), routingTables);

        //copy temporary file to '/etc/iproute2/rt_tables' and apply changes
        try {
            scriptRunner.runScript(reconfigureRoutingTablesScript);
        } catch (Exception e) {
            logger.error("Error while trying to reconfigure /etc/iproute2/rt_tables!: ", e);
        }
    }

    private List<String> readDefaultRoutingTables() {
        EblockerResource routes = new SimpleResource(ipRouteRtTablesFile);
        List<String> output = new ArrayList<>(64);
        for (String line : ResourceHandler.readLines(routes)) {
            if (line.equals(CUSTOM_ROUTES_MARKER)) {
                return output;
            }
            output.add(line);
        }
        return output;
    }

    private void setupPolicyBasedRoutes() {
        String[] arguments = new String[2 + allocatedRoutes.size()];
        arguments[0] = ipRouteRtTablesTableNamePrefix;
        arguments[1] = String.valueOf(ipRouteRtTablesOffset);

        int i = 2;
        for (Integer id : allocatedRoutes) {
            arguments[i++] = String.valueOf(id);
        }

        try {
            scriptRunner.runScript(reconfigurePolicyScript, arguments);
        } catch (Exception e) {
            logger.error("Failed to setup ip routes", e);
        }
    }
}
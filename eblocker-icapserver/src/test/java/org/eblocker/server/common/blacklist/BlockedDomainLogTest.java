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
package org.eblocker.server.common.blacklist;

import org.eblocker.server.common.data.parentalcontrol.BlockedDomainLogEntry;
import org.eblocker.server.common.data.statistic.BlockedDomainsStatisticService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BlockedDomainLogTest {

    private BlockedDomainLog log;
    private BlockedDomainsStatisticService statisticService;

    @Before
    public void setup() {
        statisticService = Mockito.mock(BlockedDomainsStatisticService.class);
        Executor executor = Executors.newSingleThreadExecutor();
        log = new BlockedDomainLog(statisticService, executor);
    }

    @Test
    public void test() throws InterruptedException {
        log.addEntry("device:10101099", "setup.eblocker.com", 2);

        Thread.sleep(100);

        ArgumentCaptor<BlockedDomainLogEntry> captor = ArgumentCaptor.forClass(BlockedDomainLogEntry.class);
        Mockito.verify(statisticService).countBlockedDomain(captor.capture());

        BlockedDomainLogEntry entry = captor.getValue();
        Assert.assertEquals("device:10101099", entry.getDeviceId());
        Assert.assertEquals("setup.eblocker.com", entry.getDomain());
        Assert.assertEquals(Integer.valueOf(2), entry.getListId());
    }

}

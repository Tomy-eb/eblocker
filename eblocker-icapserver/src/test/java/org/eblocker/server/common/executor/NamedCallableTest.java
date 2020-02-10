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
package org.eblocker.server.common.executor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

public class NamedCallableTest {

    private static final String NAME = "unit-test";

    private Callable<Integer> delegateCallable;
    private NamedCallable<Integer> namedCallable;

    @Before
    public void setUp() {
        delegateCallable = () -> 23;
        namedCallable = new NamedCallable<>(NAME, delegateCallable);
    }

    @Test
    public void getName() {
        Assert.assertEquals(NAME, namedCallable.getName());
    }

    @Test
    public void call() throws Exception {
        Assert.assertEquals(Integer.valueOf(23), namedCallable.call());
    }
}

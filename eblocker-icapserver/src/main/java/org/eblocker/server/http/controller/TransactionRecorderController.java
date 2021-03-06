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
package org.eblocker.server.http.controller;

import org.eblocker.server.common.recorder.RecordedTransaction;
import org.eblocker.server.common.recorder.TransactionRecorderInfo;
import org.eblocker.server.http.model.WhatIfModeDTO;
import org.restexpress.Request;
import org.restexpress.Response;

import java.util.List;

public interface TransactionRecorderController {
    void start(Request request, Response response);

    void stop(Request request, Response response);

    TransactionRecorderInfo info(Request request, Response response);

    List<RecordedTransaction> getAll(Request request, Response response);

    String getAllAsCSV(Request request, Response response);

    List<RecordedTransaction> get(Request request, Response response);

    WhatIfModeDTO getWhatIfMode(Request request, Response response);

    void setWhatIfMode(Request request, Response response);
}

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
export default {
    templateUrl: 'app/components/systemPending/shutdown/shutdown.component.html',
    controller: Controller,
    controllerAs: 'vm'
};

function Controller($interval, SystemService, StateService, STATES) {
    'ngInject';
    'use strict';

    const vm = this;
    vm.isCheckingStatus = isCheckingStatus;

    vm.$onInit = function() {
        startPollingForStatus();
    };

    vm.$onDestroy = function() {
        stopPollingForStatus();
    };

    let inter;

    let counter = 0;
    const TIMEOUT_STABLE_STATE = 2;

    function startPollingForStatus() {
        inter = $interval(function() {
            SystemService.getStatusPromise().then(function s(r) {
                vm.systemStatus = r;
                vm.exeState = r.executionState;
                if (vm.exeState !== 'SHUTTING_DOWN' && vm.exeState !== 'SHUTTING_DOWN_FOR_REBOOT') {
                    counter++;
                    if (counter > TIMEOUT_STABLE_STATE) {
                        // Done here, go back#
                        StateService.goToState(STATES.STAND_BY);
                    }
                } else {
                    // ** reset counter
                    counter = 0;
                }
            });
        }, 1000);
    }

    function stopPollingForStatus() {
        if (angular.isDefined(inter)){
            $interval.cancel(inter);
            inter = undefined;
        }
    }

    function isCheckingStatus() {
        return SystemService.isScanningForStatus();
    }
}

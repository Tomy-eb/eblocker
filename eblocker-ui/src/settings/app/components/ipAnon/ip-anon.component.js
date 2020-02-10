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
    templateUrl: 'app/components/ipAnon/ip-anon.component.html',
    controller: Controller,
    controllerAs: 'vm'
};

function Controller(StateService, $state) {
    'ngInject';
    'use strict';

    const vm = this;

    vm.selectedState = $state.$current;
    vm.subStates = StateService.getSubStates('anonymizationstate');
    vm.selectedIndex = getIndexOfState(vm.selectedState);
    vm.switchTab = switchTab;

    function getIndexOfState(state) {
        let ret = -1;
        vm.subStates.forEach((each, index) => {
            if (each.name === state.name) {
                ret = index;
            }
        });
        return ret;
    }

    function switchTab(state) {
        StateService.goToState(state.name);
        vm.selectedState = state;
    }

    vm.$doCheck = function() {
        // ** REQUIRED: to select tabs when routing via URL
        vm.selectedState = $state.$current;
        vm.selectedIndex = getIndexOfState(vm.selectedState);
    };
}

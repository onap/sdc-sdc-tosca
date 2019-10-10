/*-
 * ============LICENSE_START=======================================================
 * sdc-tosca
 * ================================================================================
 * Copyright (C) 2017 - 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.tosca.parser.enums;

public enum FilterType {

    CONTAINS("contains") {
        @Override
        public boolean isMatch(String value, String pattern) {
            return value.contains(pattern);
        }
    },
    EQUALS("equals") {
        @Override
        public boolean isMatch(String value, String pattern) {
            return value.equals(pattern);
        }
    };

    String filterName;

    FilterType(String name) {
        this.filterName = name;
    }

    public abstract boolean isMatch(String value, String pattern);

}

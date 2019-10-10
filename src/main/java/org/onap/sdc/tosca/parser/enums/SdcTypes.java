/*-
 * ============LICENSE_START=======================================================
 * sdc-tosca
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SdcTypes {

    CP("CP"), VL("VL"), VF("VF"), CR("CR"), VFC("VFC"), PNF("PNF"), SERVICE("Service"), CVFC("CVFC"),
    SERVICE_PROXY("Service Proxy"), CONFIGURATION("Configuration"), VFC_ALLOTTED_RESOURCE("AllottedResource");

    private static List<String> complexTypes = Arrays.asList(VF, PNF, CR, SERVICE, CVFC).stream()
        .map(SdcTypes::getValue).collect(Collectors.toList());
    private String value;

    SdcTypes(String value) {
        this.value = value;
    }

    public static boolean isComplex(String sdcType) {
        return complexTypes.contains(sdcType);
    }

    public String getValue() {
        return value;
    }
}

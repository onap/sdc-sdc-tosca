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

package org.onap.sdc.tosca.parser.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;
import org.onap.sdc.tosca.parser.api.parameters.Input;
import org.testng.annotations.Test;

public class ToscaParserServiceInputTest extends SdcToscaParserBasicTest {

    //region getServiceInputs
    @Test
    public void testGetServiceInputs() {
        List<Input> serviceInputs = fdntCsarHelper.getServiceInputs();
        assertNotNull(serviceInputs);
        assertEquals(1, serviceInputs.size());
    }

    @Test
    public void testServiceInputs() {
        List<Input> inputs = rainyCsarHelperSingleVf.getServiceInputs();
        assertNotNull(inputs);
        assertEquals(0, inputs.size());
    }
    //endregion

    //region getServiceInputLeafValueOfDefault
    @Test
    public void testGetServiceInputLeafValue() {
        String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default");
        assertEquals("test service naming", serviceInputLeafValue);
    }

//    @Test
//    public void testGetServiceInputLeafValueWithGetInput(){
//        String serviceInputLeafValue = fdntCsarHelperWithInputs.getServiceInputLeafValueOfDefault("my_input#default");
//        assertEquals(null, serviceInputLeafValue);
//    }

    @Test
    public void testGetServiceInputLeafValueNotExists() {
        String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default#kuku");
        assertNull(serviceInputLeafValue);
    }

    @Test
    public void testGetServiceInputLeafValueNull() {
        String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault(null);
        assertNull(serviceInputLeafValue);
    }
    //endregion

    //region getServiceInputLeafValueOfDefaultAsObject
    @Test
    public void testGetServiceInputLeafValueOfDefaultAsObject() {
        Object serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default");
        assertEquals("test service naming", serviceInputLeafValue);
    }

    @Test
    public void testGetServiceComplexInputLeafValueOfDefault() {
        String serviceInputLeafValue = fdntCsarHelperWithInputs
            .getServiceInputLeafValueOfDefault("complex_input#default#ipv4_subnet_default_assignment#cidr_mask");
        assertEquals(serviceInputLeafValue, "24");
    }

    @Test
    public void testGetServiceDummyComplexInputLeafValueOfDefault() {
        String serviceInputLeafValue = fdntCsarHelperWithInputs
            .getServiceInputLeafValueOfDefault("complex_input#default#ipv4_subnet_default_assignment#XXX");
        assertNull(serviceInputLeafValue);
    }

    //endregion
}

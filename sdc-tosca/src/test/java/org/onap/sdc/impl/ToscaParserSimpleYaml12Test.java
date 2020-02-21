/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation. All rights reserved.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.impl;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.testng.annotations.Test;

public class ToscaParserSimpleYaml12Test {

    private static final SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();

    @Test
    public void testVersion() throws Exception {

        verify("csars/resource-Networkfunction-csar.csar");
        verify("csars/resource-Networkservice-csar.csar");
        verify("csars/resource-Amf-csar.csar");
        verify("csars/resource-Resource-csar.csar");
    }

    private void verify(final String resourceFileName) throws Exception {
        final ISdcCsarHelper fdntCsarHelper = getCsarHelper(resourceFileName);

        assertNotNull(fdntCsarHelper.getServiceMetadata());
        assertNull(fdntCsarHelper.getServiceSubstitutionMappingsTypeName());
        assertNotNull(fdntCsarHelper.getServiceMetadataAllProperties());
        assertNull(fdntCsarHelper.getServiceInputs());
        assertNotNull(fdntCsarHelper.getConformanceLevel());
        assertNotNull(fdntCsarHelper.getDataTypes());
        assertNull(fdntCsarHelper.getInputsWithAnnotations());
        assertNotNull(fdntCsarHelper.getVFModule());
    }

    private ISdcCsarHelper getCsarHelper(final String path) throws SdcToscaParserException {
        System.out.println("Parsing CSAR " + path + "...");
        final String fileName = SdcToscaParserBasicTest.class.getClassLoader().getResource(path).getFile();
        return factory.getSdcCsarHelper((new File(fileName)).getAbsolutePath());
    }

}

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

package org.onap.sdc.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.InterfacesDef;

@ExtendWith({SdcToscaParserBasicTest.class})
class ToscaParserInterfaceTest extends SdcToscaParserBasicTest {

    private static List<NodeTemplate> vfs = csarHelperVfInterfaces.getServiceVfList();

    @Test
    public void testGetInterfaceOf() {
        Map<String, List<InterfacesDef>> interfaceDetails = csarHelperVfInterfaces.getInterfacesOf(vfs.get(0));
        assertNotNull(interfaceDetails);
        assertEquals(interfaceDetails.size(), 2);
    }

    @Test
    public void testGetInterfaces() {
        List<String> interfaceNames = csarHelperVfInterfaces.getInterfaces(vfs.get(0));
        assertNotNull(interfaceNames);
        assertEquals(interfaceNames, Arrays.asList("org.openecomp.interfaces.node.lifecycle.CxVnf1", "tosca.interfaces.node.lifecycle.Standard"));
    }

    @Test
    public void testGetInterfaceDetails() {
        List<InterfacesDef> interfaceDetails = csarHelperVfInterfaces.getInterfaceDetails(vfs.get(0),
            "org.openecomp.interfaces.node.lifecycle.CxVnf1");
        assertNotNull(interfaceDetails);
        assertEquals(interfaceDetails.get(0).getOperationName(), "instantiate");
        assertEquals(interfaceDetails.get(1).getOperationName(), "upgrade");
    }

    @Test
    public void testGetAllInterfaceOperations() {
        List<String> operations = csarHelperVfInterfaces.getAllInterfaceOperations(vfs.get(0), "org.openecomp.interfaces.node.lifecycle.CxVnf1");
        assertNotNull(operations);
        assertEquals(operations, Arrays.asList("instantiate", "upgrade", "create", "configure", "start", "stop", "delete"));
    }

    @Test
    public void testGetInterfaceOperationDetails() {
        InterfacesDef interfaceDef = csarHelperVfInterfaces.getInterfaceOperationDetails(vfs.get(0), "org.openecomp.interfaces.node.lifecycle.CxVnf1",
            "instantiate");
        assertNotNull(interfaceDef);
        assertEquals(interfaceDef.getOperationName(), "instantiate");
    }

    @Test
    public void testGetInterfaceOperationImplementationDetails() {
        InterfacesDef interfaceDef = csarHelperVfInterfaces.getInterfaceOperationDetails(vfs.get(0), "org.openecomp.interfaces.node.lifecycle.CxVnf1",
            "upgrade");
        assertNotNull(interfaceDef);
        assertNotNull(interfaceDef.getImplementation());
        assertEquals(((Map) interfaceDef.getImplementation()).get("primary"), "Artifacts/Deployment/WORKFLOW/CreateWorkFlow.json");
        assertEquals(((Map) interfaceDef.getImplementation()).get("dependencies"), "TestDependency1");
    }

}

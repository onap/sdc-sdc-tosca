package org.onap.sdc.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import org.mockito.internal.util.collections.Sets;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.InterfacesDef;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ToscaParserInterfaceTest extends SdcToscaParserBasicTest {

    List<NodeTemplate> vfs;

    @BeforeClass
    public void setup(){
        vfs = csarHelperVfInterfaces.getServiceVfList();
    }

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
        assertEquals(interfaceNames, Sets.newSet("org.openecomp.interfaces.node.lifecycle.CxVnf1", "tosca.interfaces.node.lifecycle.Standard"));
    }

    @Test
    public void testGetInterfaceDetails() {
        List<InterfacesDef> interfaceDetails = csarHelperVfInterfaces.getInterfaceDetails(vfs.get(0), "org.openecomp.interfaces.node.lifecycle.CxVnf1");
        assertNotNull(interfaceDetails);
        assertEquals(interfaceDetails.get(0).getOperationName(), "instantiate");
        assertEquals(interfaceDetails.get(1).getOperationName(), "upgrade");
    }

    @Test
    public void testGetAllInterfaceOperations() {
        List<String> operations = csarHelperVfInterfaces.getAllInterfaceOperations(vfs.get(0), "org.openecomp.interfaces.node.lifecycle.CxVnf1");
        assertNotNull(operations);
        assertEquals(operations, Sets.newSet("instantiate", "upgrade", "create", "configure", "start", "stop", "delete"));
    }

    @Test
    public void testGetInterfaceOperationDetails() {
        InterfacesDef interfaceDef = csarHelperVfInterfaces.getInterfaceOperationDetails(vfs.get(0), "org.openecomp.interfaces.node.lifecycle.CxVnf1", "instantiate");
        assertNotNull(interfaceDef);
        assertEquals(interfaceDef.getOperationName(), "instantiate");
    }

}

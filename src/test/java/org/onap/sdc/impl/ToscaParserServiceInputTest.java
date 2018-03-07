package org.onap.sdc.impl;

import org.testng.annotations.Test;
import org.onap.sdc.toscaparser.api.parameters.Input;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class ToscaParserServiceInputTest extends SdcToscaParserBasicTest {

    //region getServiceInputs
    @Test
    public void testGetServiceInputs(){
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
    public void testGetServiceInputLeafValue(){
        String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default");
        assertEquals("test service naming", serviceInputLeafValue);
    }

//    @Test
//    public void testGetServiceInputLeafValueWithGetInput(){
//        String serviceInputLeafValue = fdntCsarHelperWithInputs.getServiceInputLeafValueOfDefault("my_input#default");
//        assertEquals(null, serviceInputLeafValue);
//    }

    @Test
    public void testGetServiceInputLeafValueNotExists(){
        String serviceInputLeafValue = fdntCsarHelper.getServiceInputLeafValueOfDefault("service_naming#default#kuku");
        assertNull(serviceInputLeafValue);
    }

    @Test
    public void testGetServiceInputLeafValueNull(){
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
        String serviceInputLeafValue = fdntCsarHelperWithInputs.getServiceInputLeafValueOfDefault("complex_input#default#ipv4_subnet_default_assignment#cidr_mask");
        assertEquals(serviceInputLeafValue, "24");
    }
    
    @Test
    public void testGetServiceDummyComplexInputLeafValueOfDefault() {
        String serviceInputLeafValue = fdntCsarHelperWithInputs.getServiceInputLeafValueOfDefault("complex_input#default#ipv4_subnet_default_assignment#XXX");
        assertNull(serviceInputLeafValue);
    }
    
    
    //endregion
}

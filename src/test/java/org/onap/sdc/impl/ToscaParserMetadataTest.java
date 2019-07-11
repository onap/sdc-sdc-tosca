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

import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.config.ConfigurationManager;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class ToscaParserMetadataTest extends SdcToscaParserBasicTest {

    //region getServiceMetadata
    @Test
    public void testGetServiceMetadata() {
        Metadata serviceMetadata = fdntCsarHelper.getServiceMetadata();
        assertNotNull(serviceMetadata);
        assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", serviceMetadata.getValue("invariantUUID"));
        assertEquals("Service FDNT", serviceMetadata.getValue("name"));
        assertEquals("true", String.valueOf(serviceMetadata.getValue("serviceEcompNaming")));
    }

    @Test
    public void testServiceMetadata() {
        Metadata metadata = rainyCsarHelperSingleVf.getServiceMetadata();
        assertNull(metadata);
    }
    //endregion

    //region getMetadataPropertyValue
    @Test
    public void testGetMetadataProperty(){
        Metadata serviceMetadata = fdntCsarHelper.getServiceMetadata();
        String metadataPropertyValue = fdntCsarHelper.getMetadataPropertyValue(serviceMetadata, "invariantUUID");
        assertEquals("78c72999-1003-4a35-8534-bbd7d96fcae3", metadataPropertyValue);
    }

    @Test
    public void testGetNullMetadataPropertyValue() {
        String value = rainyCsarHelperMultiVfs.getMetadataPropertyValue(null, "XXX");
        assertNull(value);
    }

    @Test
    public void testGetMetadataByNullPropertyValue() {
        Metadata metadata = rainyCsarHelperMultiVfs.getServiceMetadata();
        String value = rainyCsarHelperMultiVfs.getMetadataPropertyValue(metadata, null);
        assertNull(value);
    }

    @Test
    public void testGetMetadataByEmptyPropertyValue() {
        Metadata metadata =  rainyCsarHelperMultiVfs.getServiceMetadata();
        String value = rainyCsarHelperMultiVfs.getMetadataPropertyValue(metadata, "");
        assertNull(value);
    }
    //endregion

    @Test
    public void GetServiceNodeTemplateMetadataTypeCR() {
        NodeTemplate nodeTemplate = csarHelperServiceWithCrs.getServiceNodeTemplateByNodeName("chaya best cr 0");
        Metadata nodeTemplateMetadata = csarHelperServiceWithCrs.getNodeTemplateMetadata(nodeTemplate);
        assertNotNull(nodeTemplateMetadata);
        assertEquals(nodeTemplateMetadata.getValue("resourceVendorModelNumber"), "");
        assertEquals(nodeTemplateMetadata.getValue("type"), "CR");
        assertEquals(nodeTemplateMetadata.getValue("name"), "chaya best cr");
        assertEquals(nodeTemplateMetadata.getValue("version"), "0.1");
    }

    //region getConformanceLevel
    @Test
    public void testSunnyGetConformanceLevel() {
        String conformanceLevel = fdntCsarHelper.getConformanceLevel();
        assertNotNull(conformanceLevel);
        assertEquals("3.0", conformanceLevel);
    }
    //endregion

    //region getServiceMetadataProperties
    @Test
    public void testNullServiceMetadataPropertiesMap() {
        Map<String, Object> metadata = rainyCsarHelperSingleVf.getServiceMetadataProperties();
        assertNull(metadata);
    }

    @Test
    public void testServiceMetadataPropertiesMap() {
        Map<String, Object> metadata = fdntCsarHelper.getServiceMetadataProperties();
        assertNotNull(metadata);
        assertEquals(metadata.size(),9);
        assertEquals(metadata.get("namingPolicy"),"test");
    }
    //endregion

    //region getServiceMetadataAllProperties
    @Test
    public void testNullServiceMetadataAllPropertiesMap() {
        Map<String, String> metadata = rainyCsarHelperSingleVf.getServiceMetadataAllProperties();
        assertNull(metadata);
    }

    @Test
    public void testServiceMetadataAllPropertiesMap() {
        Map<String, String> metadata = fdntCsarHelper.getServiceMetadataAllProperties();
        assertNotNull(metadata);
        assertEquals(metadata.size(),9);
        assertEquals(metadata.get("namingPolicy"),"test");
    }
    //endregion

    //region getNodeTemplateMetadata
    @Test
    public void testGetNodeTemplateMetadata() {
        List<NodeTemplate> vfs = fdntCsarHelper.getServiceVfList();
        Metadata metadata = fdntCsarHelper.getNodeTemplateMetadata(vfs.get(0));
        assertNotNull(metadata);
        assertEquals("VF", metadata.getValue("type"));
        assertEquals("1.0", metadata.getValue("version"));
    }

    @Test
    public void testGetNodeTemplateMetadataByNull() {
        Metadata metadata = fdntCsarHelper.getNodeTemplateMetadata(null);
        assertNull(metadata);
    }
    //endregion
    
    //QA tests region for US 319197 -port mirroring
    
   	//getNodeTemplateMetadata (All Types)
   	@Test
   	public void GetServiceNodeTemplateMetadataTypeVF() {
   		NodeTemplate nodeTemplate = QAServiceForToscaParserTests.getServiceNodeTemplateByNodeName("VF_1_V_port_1 0");
   		Metadata nodeTemplateMetadata = QAServiceForToscaParserTests.getNodeTemplateMetadata(nodeTemplate);
   		assertNotNull(nodeTemplateMetadata);
   		assertEquals(nodeTemplateMetadata.getValue("resourceVendorRelease"), "12-12-12");
   		assertEquals(nodeTemplateMetadata.getValue("type"), "VF");
   	}
   	
   	@Test
   	public void GetServiceNodeTemplateMetadataTypeVL() {
   		NodeTemplate nodeTemplate = QAServiceForToscaParserTests.getServiceNodeTemplateByNodeName("ExtVL 0");
   		Metadata nodeTemplateMetadata = QAServiceForToscaParserTests.getNodeTemplateMetadata(nodeTemplate);
   		assertNotNull(nodeTemplateMetadata);
   		assertEquals(nodeTemplateMetadata.getValue("resourceVendorRelease"), "1.0.0.wd03");
   		assertEquals(nodeTemplateMetadata.getValue("type"), "VL");
   	}
   	
   	@Test
   	public void GetServiceNodeTemplateMetadataTypeCP() {
   		NodeTemplate nodeTemplate = QAServiceForToscaParserTests.getServiceNodeTemplateByNodeName("ExtCP 0");
   		Metadata nodeTemplateMetadata = QAServiceForToscaParserTests.getNodeTemplateMetadata(nodeTemplate);
   		assertNotNull(nodeTemplateMetadata);
   		assertEquals(nodeTemplateMetadata.getValue("UUID"), "7a883088-5cab-4bfb-8d55-307d3ffd0758");
   		assertEquals(nodeTemplateMetadata.getValue("type"), "CP");
   	}
   	
   	@Test
   	public void GetServiceNodeTemplateMetadataTypePNF() {
   		NodeTemplate nodeTemplate = QAServiceForToscaParserTests.getServiceNodeTemplateByNodeName("PNF TEST 0");
   		Metadata nodeTemplateMetadata = QAServiceForToscaParserTests.getNodeTemplateMetadata(nodeTemplate);
   		assertNotNull(nodeTemplateMetadata);
   		assertEquals(nodeTemplateMetadata.getValue("resourceVendorModelNumber"), "");
   		assertEquals(nodeTemplateMetadata.getValue("type"), "PNF");
   	}
   	
   	//QA end region for US 319197 -port mirroring
   	
    // Added by QA  //region getServiceMetadataAllProperties

    @Test
    public void testGetAllMetadataProperties() {
    	Metadata serviceMetadata = fdntCsarHelper.getServiceMetadata();
    	assertNotNull(serviceMetadata);
        Map<String, String> allProperties = serviceMetadata.getAllProperties();
        assertNotNull(allProperties);
        String invariantUUID = allProperties.get("invariantUUID");
        String UUID = allProperties.get("UUID");
        String name = allProperties.get("name");
        String description = allProperties.get("description");
        String type = allProperties.get("type");
        String category = allProperties.get("category");
        String ecompGeneratedNaming = allProperties.get("ecompGeneratedNaming");
        String namingPolicy = allProperties.get("namingPolicy");
        String serviceEcompNaming = allProperties.get("serviceEcompNaming");

        assertEquals(invariantUUID, "78c72999-1003-4a35-8534-bbd7d96fcae3");
        assertEquals(UUID, "edd0a9f7-d084-4423-8461-a2eff4cb3eb6");
        assertEquals(name, "Service FDNT");
        assertEquals(description, "Service FDNT");
        assertEquals(type, "Service");
        assertEquals(category, "Network L1-3");
        assertEquals(ecompGeneratedNaming, "true");
        assertEquals(namingPolicy, "test");
        assertEquals(serviceEcompNaming, "true");
    }
    //endregion

  @Test
  public void testCSARMissingConformanceLevelWithCustomErrorConfig() throws
      SdcToscaParserException {

    ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    try {
      configurationManager.setErrorConfiguration("error-configuration-test.yaml");
      SdcToscaParserFactory.setConfigurationManager(configurationManager);
      ISdcCsarHelper missingCSARMetaCsarCustomConfig = getCsarHelper
          ("csars/service-missing-csar-meta-file.csar");
      String conformanceLevel = missingCSARMetaCsarCustomConfig.getConformanceLevel();
      assertNotNull(conformanceLevel);
      assertEquals(conformanceLevel, configurationManager.getConfiguration().getConformanceLevel()
          .getMaxVersion());
    }
    finally {
      configurationManager.setErrorConfiguration("error-configuration.yaml");
      SdcToscaParserFactory.setConfigurationManager(configurationManager);
    }

  }

  @Test(expectedExceptions = SdcToscaParserException.class)
  public void testCSARMissingConformanceLevelWithDefaultErrorConfig() throws
      SdcToscaParserException {
    ISdcCsarHelper missingCSARMetaCsarDefaultConfig = getCsarHelper("csars/service-missing-csar-meta-file.csar");
    missingCSARMetaCsarDefaultConfig.getConformanceLevel();
  }
   
}

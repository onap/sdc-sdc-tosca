package org.openecomp.sdc.impl;

import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.testng.annotations.Test;
import org.openecomp.sdc.toscaparser.api.elements.Metadata;

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
}

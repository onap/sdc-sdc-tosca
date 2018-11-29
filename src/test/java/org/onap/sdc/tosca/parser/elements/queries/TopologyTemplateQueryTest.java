package org.onap.sdc.tosca.parser.elements.queries;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class TopologyTemplateQueryTest {

    @Mock
    private Metadata metadata;

    @Mock
    private NodeTemplate nodeTemplate;

    @Test(expected=IllegalArgumentException.class)
    public void objectIsNotTopologyTemplate() {
       TopologyTemplateQuery.newBuilder(SdcTypes.CP)
               .build();
    }

    @Test
    public void templateIsFoundByTypeOnly() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)).thenReturn("345");
        assertTrue(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }

    @Test
    public void templateIsNotFoundWhenMetadataIsNull() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
                .build();
        when(nodeTemplate.getMetaData()).thenReturn(null);
        assertFalse(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }

    @Test
    public void templateIsFoundIfItIsService() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();
        assertTrue(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }

    @Test
    public void templateIsFoundByTypeAndCUUID() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
                .customizationUUID("345")
                .build();
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.CVFC.getValue());
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)).thenReturn("345");
        assertTrue(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }

    @Test
    public void templateIsNotFoundWhenTypeIsNotMatchedAndCuuidIsNotSet() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
                .build();
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.VF.getValue());
        assertFalse(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }

    @Test
    public void templateIsNotFoundWhenTypeIsNotMatchedAndCuuidIsNullInMetadata() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
                .build();
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)).thenReturn(null);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.VF.getValue());
        assertFalse(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }

    @Test
    public void templateIsNotFoundWhenTypeIsMatchedAndCuuidIsSet() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
                .customizationUUID("345")
                .build();
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.CVFC.getValue());
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)).thenReturn("345");
        assertTrue(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }

    @Test
    public void templateIsNotFoundWhenTypeIsNotMatchedAndCuuidIsSet() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CR)
                .customizationUUID("345")
                .build();
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.CVFC.getValue());
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)).thenReturn("345");
        assertFalse(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }


}

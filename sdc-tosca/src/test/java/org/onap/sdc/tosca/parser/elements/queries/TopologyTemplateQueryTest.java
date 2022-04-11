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

package org.onap.sdc.tosca.parser.elements.queries;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;

@RunWith(MockitoJUnitRunner.class)
public class TopologyTemplateQueryTest {

    @Mock
    private Metadata metadata;

    @Mock
    private NodeTemplate nodeTemplate;

    @Test(expected = IllegalArgumentException.class)
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
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.SERVICE.getValue());
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
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.SERVICE.getValue());
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
    public void templateIsFoundWhenTypeIsMatchedCuuidIsProvidedAndCuuidIsNullInMetadata() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
            .customizationUUID("2345")
            .build();
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)).thenReturn(null);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.VF.getValue());
        assertFalse(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
    }

    @Test
    public void templateIsFoundWhenTypeIsMatchedAndCuuidIsNullInMetadata() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
            .build();
        when(nodeTemplate.getMetaData()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)).thenReturn(null);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn(SdcTypes.VF.getValue());
        assertTrue(topologyTemplateQuery.isMatchingSearchCriteria(nodeTemplate));
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

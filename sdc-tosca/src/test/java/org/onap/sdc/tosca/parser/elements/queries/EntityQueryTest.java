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

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EntityQueryTest {
    @Mock
    private Metadata metadata;

    @Test
    public void findEntityWhenUuidAndCuudNotSetAndToscaTypeNotSet() {
       EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.GROUP)
               .build();
       when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
       when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("12345");
       assertTrue(entityQuery.isSearchCriteriaMatched(metadata, ""));
    }

    @Test
    public void findEntityWhenMetadataIsNull() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.GROUP)
                .build();
        assertTrue(entityQuery.isSearchCriteriaMatched(null,"abc"));
    }

    @Test
    public void findEntityWhenMetadataIsNullAndUuidsAreProvided() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.NODE_TEMPLATE)
                .customizationUUID("2345")
                .uUID("9700")
                .build();
        assertTrue(entityQuery.isSearchCriteriaMatched(null, ""));
    }

    @Test
    public void findEntityWhenUuidIsSetAndMatchedAndToscaTypeNotSet() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.GROUP)
                .uUID("123")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("12345");

        assertTrue(entityQuery.isSearchCriteriaMatched(metadata, "abc"));
    }

    @Test
    public void findEntityWhenUuidIsSetAndMatchedAndCuuidIsNullAndToscaTypeNotSet() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.GROUP)
                .uUID("123")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn(null);

        assertTrue(entityQuery.isSearchCriteriaMatched(metadata, ""));
    }

    @Test
    public void findEntityWhenUuidAndCuuidAreSetAndMatchedAndCuuidIsNullAndToscaTypeNotSet() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.GROUP)
                .uUID("123")
                .customizationUUID("567")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn(null);

        assertFalse(entityQuery.isSearchCriteriaMatched(metadata, ""));
    }


    @Test
    public void findEntityWhenUIDsAreSetAndMatchedAndToscaTypeNotSet() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.POLICY)
                .uUID("123")
                .customizationUUID("345")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("345");

        assertTrue(entityQuery.isSearchCriteriaMatched(metadata, "qwe"));
    }

    @Test
    public void findEntityWhenUIDsAreSetAndMatchedPartiallyAndToscaTypeNotSet() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.POLICY)
                .uUID("123")
                .customizationUUID("345")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("444");

        assertFalse(entityQuery.isSearchCriteriaMatched(metadata, ""));
    }

    @Test
    public void findEntityWhenUuidIsSetAndDoesNotMatchAndToscaTypeNotSet() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.GROUP)
                .uUID("7890")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("12345");

        assertFalse(entityQuery.isSearchCriteriaMatched(metadata, ""));
    }

    @Test
    public void findEntityWhenUIDsAreSetAndMatchedAndToscaTypeIsNull() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.POLICY)
                .uUID("123")
                .customizationUUID("345")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("345");

        assertTrue(entityQuery.isSearchCriteriaMatched(metadata, null));
    }

    @Test
    public void findEntityWhenUIDsAreSetAndMatchedAndToscaTypeIsNotMatched() {
        EntityQuery entityQuery =  EntityQuery.newBuilder("a.policies.b")
                .uUID("123")
                .customizationUUID("345")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("345");

        assertFalse(entityQuery.isSearchCriteriaMatched(metadata, "abc"));
    }

    @Test
    public void findEntityWhenUIDsAreSetAndMatchedAndToscaTypeIsMatched() {
        EntityQuery entityQuery =  EntityQuery.newBuilder("a.groups.b")
                .uUID("123")
                .customizationUUID("345")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("123");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("345");

        assertTrue(entityQuery.isSearchCriteriaMatched(metadata, "a.groups.b"));
    }

    @Test
    public void findEntityWhenUIDsAreNotMatchedAndToscaTypeIsMatched() {
        EntityQuery entityQuery =  EntityQuery.newBuilder("a.groups.b")
                .uUID("123")
                .customizationUUID("345")
                .build();
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_UUID))).thenReturn("12345");
        when(metadata.getValue(eq(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))).thenReturn("3456");

        assertFalse(entityQuery.isSearchCriteriaMatched(metadata, "a.groups.b"));
    }
}

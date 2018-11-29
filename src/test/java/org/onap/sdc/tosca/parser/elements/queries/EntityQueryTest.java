package org.onap.sdc.tosca.parser.elements.queries;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
        assertFalse(entityQuery.isSearchCriteriaMatched(null,"abc"));
    }

    @Test
    public void findEntityWhenMetadataIsNullAndUuidsAreProvided() {
        EntityQuery entityQuery =  EntityQuery.newBuilder(EntityTemplateType.NODE_TEMPLATE)
                .customizationUUID("2345")
                .uUID("9700")
                .build();
        assertFalse(entityQuery.isSearchCriteriaMatched(null, ""));
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

        assertFalse(entityQuery.isSearchCriteriaMatched(metadata, null));
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

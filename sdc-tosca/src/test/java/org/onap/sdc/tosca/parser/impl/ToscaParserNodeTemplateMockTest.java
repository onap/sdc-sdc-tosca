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

package org.onap.sdc.tosca.parser.impl;


import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.enums.PropertySchemaType;
import org.onap.sdc.tosca.parser.utils.PropertyUtils;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Property;
import org.onap.sdc.toscaparser.api.ToscaTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ToscaParserNodeTemplateMockTest {

    @Mock
    private NodeTemplate nodeTemplate;
    @Mock
    private Metadata metadata;
    @Mock
    private ToscaTemplate toscaTemplate;
    @Mock
    private Property property;

    private static final String dataTypeA = String.format(".%s.aaa", PropertySchemaType.DATATYPE.getSchemaTypeName());
    private static final String dataTypeB = String.format(".%s.bbb", PropertySchemaType.DATATYPE.getSchemaTypeName());
    private static final String dataTypeD = String.format(".%s.ddd", PropertySchemaType.DATATYPE.getSchemaTypeName());
    private static final String dataTypeR = String.format(".%s.rrr", PropertySchemaType.DATATYPE.getSchemaTypeName());

    @BeforeEach
    public void setUp(TestInfo info) {
        if (!info.getDisplayName().equals("verifyNodeTypeIsNotSupported()")) {
            when(property.getType()).thenReturn(dataTypeA);
        }
    }

    @Test
    public void verifyCorrectPropertyPath() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "q").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry(dataTypeA, "y", dataTypeB);
        LinkedHashMap<String, Object> cProp = fillDataTypeEntry(dataTypeB, "z", dataTypeD);
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry(dataTypeD, "q", PropertySchemaType.STRING.getSchemaTypeName());

        when(nodeTemplate.getCustomDef())
            .thenReturn(bProp)
            .thenReturn(cProp)
            .thenReturn(dProp);

        assertTrue(PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property));
    }

    @Test
    public void verifyPropertyPathWithMapOfStringsType() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "q").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry(dataTypeA, "y", dataTypeB);
        LinkedHashMap<String, Object> cProp = fillDataTypeEntry(dataTypeB, "z", PropertySchemaType.MAP.getSchemaTypeName());
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry(PropertySchemaType.MAP.getSchemaTypeName(), "q",
            PropertySchemaType.STRING.getSchemaTypeName());

        when(nodeTemplate.getCustomDef())
            .thenReturn(bProp)
            .thenReturn(cProp)
            .thenReturn(dProp);

        assertTrue(PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property));
    }

    @Test
    public void verifyPropertyPathWithMapType() {
        String[] path = String.format("%s#%s#%s", "x", "y", "z").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry(dataTypeA, "y", dataTypeB);
        LinkedHashMap<String, Object> cProp = fillDataTypeEntry(dataTypeB, "z", PropertySchemaType.MAP.getSchemaTypeName());

        when(nodeTemplate.getCustomDef())
            .thenReturn(bProp)
            .thenReturn(cProp);

        assertFalse(PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property));
    }


    @Test
    public void verifyPropertyPathWithListOfDataTypeShouldBeRejected() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "m").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry(dataTypeA, "y", dataTypeB);
        LinkedHashMap<String, Object> cProp = fillDataTypeEntry(dataTypeB, "z", dataTypeD);
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry(dataTypeD, "m", PropertySchemaType.LIST.getSchemaTypeName(), dataTypeR);

        when(nodeTemplate.getCustomDef())
            .thenReturn(bProp)
            .thenReturn(cProp)
            .thenReturn(dProp);

        assertFalse(PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property));
    }

    @Test
    public void verifyPropertyPathWithListOfIntegersAsType() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "m").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry(dataTypeA, "y", dataTypeB);
        LinkedHashMap<String, Object> cProp = fillDataTypeEntry(dataTypeB, "z", dataTypeD);
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry(dataTypeD, "m", PropertySchemaType.LIST.getSchemaTypeName(),
            PropertySchemaType.INTEGER.getSchemaTypeName());

        when(nodeTemplate.getCustomDef())
            .thenReturn(bProp)
            .thenReturn(cProp)
            .thenReturn(dProp);

        assertTrue(PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property));
    }

    @Test
    public void propertyPathIsRejectedAsShorterThanExpected() {
        String[] path = String.format("%s#%s", "x", "y").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry(dataTypeA, "y", dataTypeB);
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry(dataTypeB, "z", PropertySchemaType.STRING.getSchemaTypeName());

        when(nodeTemplate.getCustomDef())
            .thenReturn(bProp)
            .thenReturn(dProp);

        assertFalse(PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property));
    }

    @Test
    public void propertyPathIsRejectedAsLongerThanExpected() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "q").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry(dataTypeA, "y", dataTypeB);
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry(dataTypeB, "z", PropertySchemaType.STRING.getSchemaTypeName());

        when(nodeTemplate.getCustomDef())
            .thenReturn(bProp)
            .thenReturn(dProp);

        assertThrows(NoSuchElementException.class, () -> {
            PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property);
        });
    }

    @Test
    public void propertyPathIsRejectedAsPropertyIsNotFound() {
        String[] path = String.format("%s#%s", "x", "y").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry(dataTypeA, "t", dataTypeB);
        when(nodeTemplate.getCustomDef()).thenReturn(bProp);

        assertThrows(NoSuchElementException.class, () -> {
            assertFalse(PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property));
        });
    }

    @Test
    public void verifyNodeTypeIsNotSupported() {
        when(nodeTemplate.getMetadata()).thenReturn(metadata);
        when(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)).thenReturn("VFC-TEST");

        ISdcCsarHelper sdcCsarHelper = new SdcCsarHelperImpl(toscaTemplate);
        assertFalse(sdcCsarHelper.isNodeTypeSupported(nodeTemplate));
    }


    private LinkedHashMap<String, Object> fillDataTypeEntry(String dataTypeName, String propertyName, String type) {
        return fillDataTypeEntry(dataTypeName, propertyName, type, "");
    }

    private LinkedHashMap<String, Object> fillDataTypeEntry(String dataTypeName, String propertyName, String type, String entrySchemaType) {
        LinkedHashMap<String, Object> dataTypes = new LinkedHashMap<>();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        LinkedHashMap<String, Object> property = new LinkedHashMap<>();
        LinkedHashMap<String, Object> dataType = new LinkedHashMap<>();
        property.put(SdcPropertyNames.PROPERTY_NAME_TYPE, type);
        property.put(SdcPropertyNames.PROPERTY_NAME_NAME, propertyName);

        PropertySchemaType propertySchemaType = PropertySchemaType.getEnumByValue(type);

        if (!StringUtils.isEmpty(entrySchemaType) &&
            (propertySchemaType.getSchemaTypeComplexity() == PropertySchemaType.PropertySchemaComplexity.Complex)) {
            LinkedHashMap<String, Object> entry_schema = new LinkedHashMap<>();
            entry_schema.put(SdcPropertyNames.PROPERTY_NAME_TYPE, entrySchemaType);
            property.put(SdcPropertyNames.PROPERTY_NAME_ENTRY_SCHEMA, entry_schema);
        }
        properties.put(propertyName, property);
        dataType.put(SdcPropertyNames.PROPERTY_NAME_PROPERTIES, properties);
        dataTypes.put(dataTypeName, dataType);
        return dataTypes;
    }


}

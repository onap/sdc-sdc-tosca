package org.onap.sdc.tosca.parser.impl;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.sdc.tosca.parser.enums.ComplexPropertyTypes;
import org.onap.sdc.tosca.parser.enums.SimplePropertyTypes;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Property;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToscaParserNodeTemplateMockTest {
    @Mock
    private NodeTemplate nodeTemplate;

    @Mock
    private Property property;

    /*@Test
    public void verifyCorrectPropertyPath() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "q").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry("aaa", "y", "bbb");
        LinkedHashMap<String, Object> cProp = fillDataTypeEntry("bbb", "z", "ddd");
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry("ddd", "q", SimplePropertyTypes.STRING.getValue());

        when(nodeTemplate.getCustomDef())
                .thenReturn(bProp)
                .thenReturn(cProp)
                .thenReturn(dProp);

        assertEquals(SimplePropertyTypes.STRING.getValue(), PropertyUtils.getInternalPropertyType(nodeTemplate, "aaa", path, 1));
    }

    @Test
    public void verifyPropertyPathWithMapType() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "q").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry("aaa", "y", "bbb");
        LinkedHashMap<String, Object> cProp = fillDataTypeEntry("bbb", "z", ComplexPropertyTypes.MAP.getValue(), "ddd");
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry("ddd", "q", SimplePropertyTypes.STRING.getValue());

        when(nodeTemplate.getCustomDef())
                .thenReturn(bProp)
                .thenReturn(cProp)
                .thenReturn(dProp);

        assertEquals(SimplePropertyTypes.STRING.getValue(), PropertyUtils.getInternalPropertyType(nodeTemplate, "aaa", path, 1));
    }

    @Test
    public void verifyPropertyPathWithListTypeShouldBeRejected() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "q").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry("aaa", "y", "bbb");
        LinkedHashMap<String, Object> cProp = fillDataTypeEntry("bbb", "z", "ddd");
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry("ddd", "m", ComplexPropertyTypes.LIST.getValue(), "mmm");

        when(nodeTemplate.getCustomDef())
                .thenReturn(bProp)
                .thenReturn(cProp)
                .thenReturn(dProp);

        assertEquals("", PropertyUtils.getInternalPropertyType(nodeTemplate, "aaa", path, 1));
    }

    @Test
    public void propertyPathIsRejectedAsShorterThanExpected() {
        String[] path = String.format("%s#%s", "x", "y").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry("aaa", "y", "bbb");
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry("bbb", "z", SimplePropertyTypes.STRING.getValue());

        when(nodeTemplate.getCustomDef())
                .thenReturn(bProp)
                .thenReturn(dProp);

        assertEquals("bbb", PropertyUtils.getInternalPropertyType(nodeTemplate, "aaa", path, 1));

    }

    @Test
    public void propertyPathIsRejectedAsLongerThanExpected() {
        String[] path = String.format("%s#%s#%s#%s", "x", "y", "z", "q").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry("aaa", "y", "bbb");
        LinkedHashMap<String, Object> dProp = fillDataTypeEntry("bbb", "z", SimplePropertyTypes.STRING.getValue());

        when(nodeTemplate.getCustomDef())
                .thenReturn(bProp)
                .thenReturn(dProp);

        assertEquals("", PropertyUtils.getInternalPropertyType(nodeTemplate, "aaa", path, 1));
    }

    @Test
    public void propertyPathIsRejectedAsPropertyIsNotFound() {
        String[] path = String.format("%s#%s", "x", "y").split("#");
        LinkedHashMap<String, Object> bProp = fillDataTypeEntry("aaa", "t", "bbb");

        when(nodeTemplate.getCustomDef())
                .thenReturn(bProp);

        assertEquals("", PropertyUtils.getInternalPropertyType(nodeTemplate, "aaa", path, 1));

    }

    @Test
    public void convertStringPropertyValue() {
        final String value = "String";
        when(property.getType()).thenReturn(SimplePropertyTypes.STRING.getValue());
        when(property.getValue()).thenReturn(value);
        List<String> result = PropertyUtils.convertPropertyValue(property);
        assertEquals(1, result.size());
        assertEquals(value, result.get(0));
    }

    @Test
    public void convertIntegerPropertyValue() {
        final Integer value = 3456;
        when(property.getType()).thenReturn(SimplePropertyTypes.INTEGER.getValue());
        when(property.getValue()).thenReturn(value);

        List<String> result = PropertyUtils.convertPropertyValue(property);
        assertEquals(1, result.size());
        assertEquals(String.valueOf(value), result.get(0));
    }

    @Test
    public void convertBooleanPropertyValue() {
        when(property.getType()).thenReturn(SimplePropertyTypes.BOOLEAN.getValue());
        when(property.getValue()).thenReturn(true);

        List<String> result = PropertyUtils.convertPropertyValue(property);
        assertEquals(1, result.size());
        assertEquals(String.valueOf(true), result.get(0));
    }


    @Test
    public void convertListPropertyValue() {
        List<String> list = Lists.newArrayList("first", "second", "third");
        when(property.getType()).thenReturn(ComplexPropertyTypes.LIST.getValue());
        when(property.getValue()).thenReturn(list);

        List<String> result = PropertyUtils.convertPropertyValue(property);
        assertEquals(list.size(), result.size());
        assertEquals(list.size(), result.stream().filter(r->list.contains(r)).count());
    }

    @Test
    public void convertMapPropertyValue() {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);
        when(property.getType()).thenReturn(ComplexPropertyTypes.MAP.getValue());
        when(property.getValue()).thenReturn(map);

        List<String> result = PropertyUtils.convertPropertyValue(property);
        assertEquals(map.size(), result.size());
        assertEquals(map.size(), result.stream().filter(r->map.values().contains(Integer.valueOf(r))).count());
    }*/


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
        if (!StringUtils.isEmpty(entrySchemaType) &&
                (ComplexPropertyTypes.LIST.getValue().equals(type) || ComplexPropertyTypes.MAP.getValue().equals(type))) {
            LinkedHashMap<String, Object> entry_schema = new LinkedHashMap<>();
            entry_schema.put(SdcPropertyNames.PROPERTY_NAME_TYPE, entrySchemaType);
            property.put(SdcPropertyNames.PROPERTY_NAME_ENTRY_SCHEMA, entry_schema);
        }
        properties.put(propertyName, property);
        dataType.put(SdcPropertyNames.PROPERTY_NAME_PROPERTIES,  properties);
        dataTypes.put(dataTypeName, dataType);
        return dataTypes;
    }



}

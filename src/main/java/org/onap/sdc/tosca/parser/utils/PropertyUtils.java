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

package org.onap.sdc.tosca.parser.utils;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.enums.PropertySchemaType;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.onap.sdc.tosca.parser.enums.PropertySchemaType.PropertySchemaComplexity.Simple;

public class PropertyUtils {

    private static Logger log = LoggerFactory.getLogger(PropertyUtils.class.getName());

    private PropertyUtils() {}

    private static String calculatePropertyType(LinkedHashMap<String, Object> property) {
        String type = (String) property.get(SdcPropertyNames.PROPERTY_NAME_TYPE);
        if (PropertySchemaType.LIST.getSchemaTypeName().equals(type)) {
            //it might be a data type
            return getEntrySchemaType(property);
        }
        return type;
    }

    private static String getEntrySchemaType(LinkedHashMap<String, Object> property) {
        LinkedHashMap<String, Object> entrySchema = (LinkedHashMap<String, Object>)property.get(SdcPropertyNames.PROPERTY_NAME_ENTRY_SCHEMA);
        if (entrySchema != null) {
            return (String) entrySchema.get(SdcPropertyNames.PROPERTY_NAME_TYPE);
        }
        return null;
    }

    private static String calculatePropertyType(Property property) {
         if (PropertySchemaType.LIST.getSchemaTypeName().equals(property.getType())) {
            //if it is list, return entry schema type
            return (String)property.getEntrySchema().get(SdcPropertyNames.PROPERTY_NAME_TYPE);
        }
        return property.getType();
    }

    public static boolean isListOfSimpleTypes(String type) {
        PropertySchemaType entrySchemaType = PropertySchemaType.getEnumByValue(type);
        return entrySchemaType.getSchemaTypeComplexity() == PropertySchemaType.PropertySchemaComplexity.Simple;
    }

    public static boolean isDataPropertyType(String type) {
        PropertySchemaType entrySchemaType = PropertySchemaType.getEnumByValue(type);
        return entrySchemaType == PropertySchemaType.DATATYPE;
    }

    public static Object processProperties(String[] split, LinkedHashMap<String, Property> properties) {
        Optional<Map.Entry<String, Property>> findFirst = properties.entrySet().stream().filter(x -> x.getKey().equals(split[0])).findFirst();
        if (findFirst.isPresent()) {
            Property property = findFirst.get().getValue();
            Object current = property.getValue();
            return iterateProcessPath(1, current, split);
        }
        String propName = (split != null && split.length > 0 ? split[0] : null);
        log.error("processProperties - property {} is not found", propName);
        return null;
    }

    public static List<String> findSimplePropertyValueInListOfDataTypes(List<Object> valueAsObjectList, String[] path) {
        return valueAsObjectList.stream()
                .map(v->iterateProcessPath(1, v, path))
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object iterateProcessPath(Integer index, Object current, String[] split) {
        if (current == null) {
            log.error("iterateProcessPath - this input has no default");
            return null;
        }
        if (split.length > index) {
            for (int i = index; i < split.length; i++) {
                if (current instanceof Map) {
                    current = ((Map<String, Object>) current).get(split[i]);
                } else if (current instanceof List) {
                    current = ((List) current).get(0);
                    i--;
                }
                else {
                    log.error("iterateProcessPath - found an unexpected leaf where expected to find a complex type");
                    return null;
                }
            }
        }
        if (current != null) {
            return current;
        }
        log.error("iterateProcessPath - Path not Found");
        return null;
    }

    public static boolean isPropertyTypeSimpleOrListOfSimpleTypes(NodeTemplate nodeTemplate, String[] path, Property property) {
        PropertySchemaType internalPropertyType = PropertyUtils.getPropertyTypeByPath(nodeTemplate, path, property);
        return internalPropertyType.getSchemaTypeComplexity() == Simple;
    }

    private static PropertySchemaType getPropertyTypeByPath(NodeTemplate nodeTemplate, String[] path, Property property) {
        String propertyType = calculatePropertyType(property);
        String propertyTypeByPath = propertyType;

        if (path.length > 1) {
            propertyTypeByPath = getInternalPropertyType(nodeTemplate, propertyType, path, 1);
        }
        return PropertySchemaType.getEnumByValue(propertyTypeByPath);
    }

    public static List<String> buildSimplePropertValueOrList(Object value) {
        if (value instanceof List) {
            return ((ArrayList<Object>) value)
                    .stream()
                    //it might be null when get_input can't be resolved
                    // e.g.:
                    // - get_input has two parameters: 1. list and 2. index in this list
                    //and list has no value
                    // - neither value no default is defined for get_input
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList(String.valueOf(value));
    }

    private static String getInternalPropertyType(NodeTemplate nodeTemplate, String dataTypeName, String[] path, int index) {
        if (path.length > index) {
            LinkedHashMap<String, Object> complexProperty = (LinkedHashMap<String, Object>) nodeTemplate.getCustomDef().get(dataTypeName);
            if (complexProperty != null) {
                LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) complexProperty.get(SdcPropertyNames.PROPERTY_NAME_PROPERTIES);
                return getPropertyTypeFromCustomDef(nodeTemplate, path, index, properties);
            }
        }
        //stop searching - seems as wrong flow: the path is finished but the value is not found yet
        log.error("The property path {} is incorrect, the request will be rejected", path);
        return null;
    }

    private static String getPropertyTypeFromCustomDef(NodeTemplate nodeTemplate, String[] path, int index, LinkedHashMap<String, Object> properties) {
        final String methodName = "getPropertyTypeFromCustomDef";
        if (properties != null) {
            LinkedHashMap<String, Object> foundProperty = (LinkedHashMap<String, Object>) (properties).get(path[index]);
            if (foundProperty != null) {
                String propertyType = calculatePropertyType(foundProperty);
                log.info("{} - type {} is data type", methodName, propertyType);
                if ((index == path.length - 1)){
                    log.info("{} - the last element {} in the property path is found", methodName, path[index]);
                    return propertyType;
                }
                return getInternalPropertyType(nodeTemplate, propertyType, path, index + 1);
            }
            log.error("{} - the property [{}] is not found", methodName, path[index]);
        }
        return null;
    }


}

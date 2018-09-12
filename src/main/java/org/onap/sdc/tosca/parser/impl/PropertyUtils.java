package org.onap.sdc.tosca.parser.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.onap.sdc.tosca.parser.enums.ComplexPropertyTypes;
import org.onap.sdc.tosca.parser.enums.SimplePropertyTypes;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PropertyUtils {

    private static Logger log = LoggerFactory.getLogger(PropertyUtils.class.getName());

    private static final String GET_INPUT = "get_input";

    private PropertyUtils() {}

    private static String calculatePropertyType(LinkedHashMap<String, Object> property) {
        String type = (String) property.get(SdcPropertyNames.PROPERTY_NAME_TYPE);
        if (isComplexPropertyType(type)) {
            //it might be a data type
            return getEntrySchemaType(property);
        }
        return type;
    }

    private static String calculatePropertyType(Property property) {
        String type = property.getType();
        if (isComplexPropertyType(type)) {
            //it might be a data type
            return (String)property.getEntrySchema().get(SdcPropertyNames.PROPERTY_NAME_TYPE);
        }
        return type;
    }

    private static boolean isSimplePropertyType(String type) {
        return Arrays.stream(SimplePropertyTypes.values()).anyMatch(v->v.getValue().equals(type));
    }

    private static boolean isComplexPropertyType(String type) {
        return Arrays.stream(ComplexPropertyTypes.values()).anyMatch(v->v.getValue().equals(type));
    }

    static boolean isDataPropertyType(String type) {
        return !isSimplePropertyType(type) && !isComplexPropertyType(type);
    }

    private static String getInputValueAsString(Object value) {
        String valueAsString = null;

        if (value instanceof Map) {
            valueAsString = String.valueOf(((LinkedHashMap)value).get(GET_INPUT));
        }
        else if (value instanceof List) {
            Object valueInList = ((ArrayList)value).get(0);
            if (valueInList instanceof Map) {
                valueAsString = String.valueOf(((LinkedHashMap) valueInList).get(GET_INPUT));
            }
        }
        return valueAsString;
    }

    private static String getEntrySchemaType(LinkedHashMap<String, Object> property) {
        LinkedHashMap<String, Object> entrySchema = (LinkedHashMap<String, Object>)property.get(SdcPropertyNames.PROPERTY_NAME_ENTRY_SCHEMA);
        if (entrySchema != null) {
            return (String) entrySchema.get(SdcPropertyNames.PROPERTY_NAME_TYPE);
        }
        return null;
    }

    static PropertyValueContainer calculatePropertyValueAsPerTypeAndGetInput(NodeTemplate nodeTemplate, String[] path, Property property) {
        final String methodName = "calculatePropertyValueAsPerTypeAndGetInput";
       PropertyValueContainer propertyContainer = null;

        String propertyType = calculatePropertyType(property);

        if (path.length == 1) {
            propertyContainer = convertPropertyValue(nodeTemplate, propertyType, path);
        }
        else {
            propertyType = getInternalPropertyType(nodeTemplate, propertyType, path, 1);
            if (propertyType != null) {
                propertyContainer = convertPropertyValue(nodeTemplate, propertyType, path);
            }
        }
        if (log.isInfoEnabled() && propertyContainer != null) {
            log.info("{} - found value for property {}: {}", methodName, path, propertyContainer.getResolvedValue());
        }
        return propertyContainer;
    }

    static Map<String, PropertyValueContainer> resolveGetInputsIfFound(NodeTemplate nodeTemplate, Map<String, PropertyValueContainer> propertyValueMap) {
        List<String> valuesToRemove = Lists.newArrayList();
        List<PropertyValueContainer> getInputContainerList = Lists.newArrayList();

        for (PropertyValueContainer container : propertyValueMap.values()) {
            if (!StringUtils.isEmpty(container.getGetInputValue())) {
                //get_input is not resolved yet
                String[] inputs = parseMultipleGetInputValue(container.getGetInputValue());
                int index = 0;
                for (String input : inputs) {
                    PropertyValueContainer getInputContainer = convertPropertyValue(nodeTemplate, nodeTemplate.getPropertyType(input), new String[]{input}, container.getName());
                    if (getInputContainer != null) {
                        //add this container to list for further processing
                        getInputContainerList.add(getInputContainer);
                        if (index == 1) {
                            getInputContainer.setGetInputIndex(true);
                        }
                    }
                    index++;
                }
                //once inputs values are collected, the property whose value is the input can be removed from the map
                valuesToRemove.add(container.getName());
            }
        }
        valuesToRemove.forEach(propertyValueMap::remove);

        return handleGetInputValues(propertyValueMap, getInputContainerList);
    }

    private static boolean isGetInputResolved(PropertyValueContainer container) {
        return StringUtils.isEmpty(container.getGetInputValue()) && !container.getResolvedValue().isEmpty();
    }

    private static Map<String, PropertyValueContainer> handleGetInputValues(Map<String, PropertyValueContainer> propertyValueMap, List<PropertyValueContainer> getInputs) {
        //check if the get_input got resolved
        for (PropertyValueContainer container : getInputs) {
            if (isGetInputResolved(container)) {
                //the get_input has been resolved,
                // check if all elements of this get_input are resolved,
                // then add to the response and clean the map up
                List<PropertyValueContainer> otherContainersForGivenInput = propertyValueMap.values()
                        .stream()
                        .filter(c -> c.getInputFor().equals(container.getInputFor()))
                        .collect(Collectors.toList());
                if (otherContainersForGivenInput.isEmpty()) {
                    //no additional inputs elements found, update the corresponding property the input was defined for
                    resloveInputInTheContainerMap(propertyValueMap, container);
                } else if (otherContainersForGivenInput.size() == 1 && isGetInputResolved(otherContainersForGivenInput.get(0))) {
                    //all element of this input are resolved
                    handleGetInputPair(propertyValueMap, container, otherContainersForGivenInput);
                } else {
                    //the get_input is not resolved yet, add it to the map
                    propertyValueMap.put(container.getName(), container);
                }
            } else {
                //the get_input is not resolved yet, add it to the map
                propertyValueMap.put(container.getName(), container);
            }
        }
        return propertyValueMap;
    }

    private static void handleGetInputPair(Map<String, PropertyValueContainer> propertyValueMap, PropertyValueContainer container, List<PropertyValueContainer> allContainersForGivenInput) {
        String value = container.getResolvedValue().get(0);
        if (container.isGetInputIndex()) {
            //the index in list is resolved
            value = allContainersForGivenInput.get(0).getResolvedValue().get(Integer.valueOf(container.getResolvedValue().get(0)));
        } else if (allContainersForGivenInput.get(0).isGetInputIndex()) {
            value = container.getResolvedValue().get(Integer.valueOf(allContainersForGivenInput.get(0).getResolvedValue().get(0)));
        }
        PropertyValueContainer inputFor = propertyValueMap.get(container.getInputFor());
        if (inputFor != null) {
            inputFor.handleResolvedValue(Lists.newArrayList(value));
        }
        //todo check
        resloveInputInTheContainerMap(propertyValueMap, container);
    }

    /*           int index = propertyValue.indexOf(GET_INPUT);
               if (index != -1) {
                   String origValue = propertyValue.substring(index + GET_INPUT.length() + 1, propertyValue.length() - 1);
                   valuesToRemove.add(propertyValue);
                   String[] inputs = parseMultipleGetInputValue(origValue);
                   //resolve each input andd add it to the property values response
                   for (String input : inputs) {
                       valuesToAdd.addAll(convertPropertyValue(nodeTemplate, nodeTemplate.getPropertyType(input), new String[] {input}));
                   }
               }
           }
           propertyValues.removeAll(valuesToRemove);
           propertyValues.addAll(valuesToAdd);
           return propertyValues;
       }
   */
/*    private static void cleanUpMapAfterGetInputResolve(Map<String, PropertyValueContainer> propertyValueMap) {
        for (Iterator it = propertyValueMap.keySet().iterator(); it.hasNext(); ) {
            PropertyValueContainer container = (PropertyValueContainer) it.next();
            if (container.)

        }

    }*/
    private static void resloveInputInTheContainerMap(Map<String, PropertyValueContainer> propertyValueMap, PropertyValueContainer inputFoundContainer) {
        while (inputFoundContainer.getInputFor() != null) {
            PropertyValueContainer inputFor = propertyValueMap.get(inputFoundContainer.getInputFor());
            if (inputFor != null) {
                inputFor.handleResolvedValue(inputFoundContainer.getResolvedValue());
                inputFoundContainer = inputFor;
            }
        }
    }
    private static String[] parseMultipleGetInputValue(String origValue) {
        if (origValue.startsWith("[")) {
            //the get_input keeps a list, we should split it
            return origValue.substring(1, origValue.length() - 1).split(", ");
        }
        return new String[] {origValue};
    }

    static Object processProperties(String[] split, LinkedHashMap<String, Property> properties) {
        Optional<Map.Entry<String, Property>> findFirst = properties.entrySet().stream().filter(x -> x.getKey().equals(split[0])).findFirst();
        if (findFirst.isPresent()) {
            Property property = findFirst.get().getValue();
            Object current = property.getValue();
            return iterateProcessPath(1, current, split);
        }
        String propName = (split != null && split.length > 0 ? split[0] : null);
        log.error("processProperties - property {} not found", propName);
        return null;
    }

    private static Object getPropertyValueFromTemplateByPath(NodeTemplate nodeTemplate, String[] path) {
        Object property = nodeTemplate.getPropertyValueFromTemplatesByName(path[0]);
        if (property != null) {
            if (path.length > 1) {
                return iterateProcessPath(1, property, path);
            }
            return property;
        }
        log.error("getPropertyValueFromTemplateByPath - property {} is not found", path[0]);
        return null;
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Object iterateProcessPath(Integer index, Object current, String[] split) {
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

    private static PropertyValueContainer convertPropertyValue(NodeTemplate nodeTemplate, String propertyType, String[] path) {
        return convertPropertyValue(nodeTemplate, propertyType, path, "");
    }

    private static PropertyValueContainer convertPropertyValue(NodeTemplate nodeTemplate, String propertyType, String[] path, String inputFor) {
        final String methodName = "convertPropertyValue";
        final String propertyName = path[path.length - 1];

        if (propertyType == null || isDataPropertyType(propertyType)) {
            log.error("{} - property type {} is either null or data type, reject this request", methodName, propertyType);
            return null;
        }

        Object value = getPropertyValueFromTemplateByPath(nodeTemplate, path);
        if (value == null) {
            log.warn("{} - value of requested property [{}] is not found", methodName, propertyName);
            return null;
        }
        return buildPropertValueByType(propertyName, inputFor, value, propertyType);
    }

    private static PropertyValueContainer buildPropertValueByType(String propertyName, String inputFor, Object value, String type) {
        final String methodName = "buildPropertValueByType";

        String getInputValue = getInputValueAsString(value);

        if (getInputValue != null) {
            log.info("{} - type of requested property [{}] is get_input", methodName, propertyName);
            if (getInputValue.contains(GET_INPUT)) {
                log.error("{} - it is nested get_input - ignore");
                return null;
            }
            return new PropertyValueContainer(propertyName, inputFor, getInputValue);
        }
        if (isSimplePropertyType(type)) {
            log.info("{} - type of requested property [{}] is simple", methodName, propertyName);
            return new PropertyValueContainer(propertyName, inputFor, Collections.singletonList(String.valueOf(value)));

        }
        //assuming that list/map of data types had been detected and rejected before,
        //we can have only list/map of simple types on this stage
        if (type.equals(ComplexPropertyTypes.LIST.getValue())) {
            log.info("{} - type of requested property [{}] is list", methodName, propertyName);
            return new PropertyValueContainer(propertyName, inputFor, ((((ArrayList<Object>) value)
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()))));
        }
        if (type.equals(ComplexPropertyTypes.MAP.getValue())) {
            log.info("{} - type of requested property [{}] is map", methodName, propertyName);
            return new PropertyValueContainer(propertyName, inputFor, (((Map<String, Object>) value)
                    .values()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList())));
        }
        log.error("{} - type of requested property [{}] is incorrect, the property value can't be retrieved, the request will be rejected",
                    methodName, propertyName);
        return null;
    }

    //todo complexProperty should be nodeTemplate.getCustomDef().get(complexPropName), index = 1 -?
    @VisibleForTesting
    private static String getInternalPropertyType(NodeTemplate nodeTemplate, String dataTypeName, String[] path, int index) {
        final String methodName = "getInternalPropertyType";
        if (path.length > index) {
            LinkedHashMap<String, Object> complexProperty = (LinkedHashMap<String, Object>) nodeTemplate.getCustomDef().get(dataTypeName);
            if (complexProperty != null) {
                LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) complexProperty.get(SdcPropertyNames.PROPERTY_NAME_PROPERTIES);
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
                    else {
                        log.error("{} - the property [{}] is not found", methodName, path[index]);
                    }
                }
            }
        }
        //stop searching - seems as wrong flow: the path is finished but the value is not found yet
        log.error("{} - the property path {} is incorrect, the request will be rejected", methodName, path);
        return null;
    }



}

package org.onap.sdc.tosca.parser.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper structure used for resolving property value
 * when this property is located in the internal NodeTemplate
 * and the value is get_input
 */
public class PropertyValueContainer {
    /**
     * Property name
     */
    private String name;
    /**
     * Property value without get inputs,
     * null until get_input got resolved
     */
    private List<String> resolvedValue = new ArrayList<>();
    /**
     * get_input value if exists,
     * null if get_input either is not set as this property value,
     * or is resolved now
     */
//    private List<String> getInputValue = new ArrayList<>();
    private String getInputValue;

    public void setGetInputIndex(boolean getInputIndex) {
        isGetInputIndex = getInputIndex;
    }

    private boolean isGetInputIndex = false;
    /**
     * Name of property a given get_input is used as a value,
     * null if get_input is not set or if the get_input was set
     * and it is the property the search is started from
     */
    private String inputFor;

    public PropertyValueContainer(String name, String inputFor, List<String> resolvedValue) {
        this.name = name;
        this.inputFor = inputFor;
        if (resolvedValue != null) {
            setResolvedValue(resolvedValue);
        }
    }

    public PropertyValueContainer(String name, String inputFor, String getInputValue, boolean isGetInputIndex) {
        this.name = name;
        this.inputFor = inputFor;
        this.isGetInputIndex = isGetInputIndex;
        setGetInputValue(getInputValue);
    }

    public PropertyValueContainer(String name, String inputFor, String getInputValue) {
        this(name, inputFor, getInputValue, false);
    }

    public boolean isGetInputIndex() {
        return isGetInputIndex;
    }

    public void handleResolvedValue(List<String> resolvedValue) {
        if (resolvedValue != null) {
            setResolvedValue(resolvedValue);
        }
        this.getInputValue = null;
    }

    public void setResolvedValue(List<String> resolvedValue) {
        if (resolvedValue != null && !resolvedValue.isEmpty()) {
            this.resolvedValue.addAll(resolvedValue);
        }
    }

    public void setGetInputValue(String getInputValue) {
        this.getInputValue = getInputValue;
    }

    public String getName() {
        return name;
    }

    public List<String> getResolvedValue() {
        return resolvedValue;
    }

    public String getGetInputValue() {
        return getInputValue;
    }

    public String getInputFor() {
        return inputFor;
    }
}

package org.onap.sdc.tosca.parser.enums;

public enum ComplexPropertyTypes {
    LIST("list"),
    MAP("map");

    private String value;

    ComplexPropertyTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

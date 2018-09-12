package org.onap.sdc.tosca.parser.enums;

public enum SimplePropertyTypes {
    STRING("string"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    FLOAT("float"),
    NUMBER("number"),
    TIMESTAMP("timestamp"),
    RANGE("range"),
    VERSION("version"),
    SCALAR_UNIT_SIZE("scalar-unit.size"),
    SCALAR_UNIT_TIME("scalar-unit.time"),
    SCALAR_UNIT_FREQUENCY("scalar-unit.frequency");

    private String value;

    SimplePropertyTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

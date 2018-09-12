package org.onap.sdc.tosca.parser.enums;


import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.onap.sdc.tosca.parser.enums.PropertySchemaType.PropertySchemaComplexity.Complex;
import static org.onap.sdc.tosca.parser.enums.PropertySchemaType.PropertySchemaComplexity.DataType;
import static org.onap.sdc.tosca.parser.enums.PropertySchemaType.PropertySchemaComplexity.Simple;

public enum PropertySchemaType {

    STRING(Simple, "string"),
    INTEGER(Simple, "integer"),
    BOOLEAN(Simple, "boolean"),
    FLOAT(Simple, "float"),
    NUMBER(Simple, "number"),
    TIMESTAMP(Simple, "timestamp"),
    RANGE(Simple, "range"),
    VERSION(Simple, "version"),
    SCALAR_UNIT_SIZE(Simple, "scalar-unit.size"),
    SCALAR_UNIT_TIME(Simple, "scalar-unit.time"),
    SCALAR_UNIT_FREQUENCY(Simple, "scalar-unit.frequency"),
    LIST(Complex, "list"),
    MAP(Complex, "map"),
    DATATYPE(DataType, "datatypes");

    private PropertySchemaComplexity complexity;
    private String schemaType;

    PropertySchemaType(PropertySchemaComplexity complexity, String schemaType) {
        this.complexity = complexity;
        this.schemaType = schemaType;
    }

    public PropertySchemaComplexity getSchemaTypeComplexity() {
        return complexity;
    }

    public String getSchemaTypeName() {
        return schemaType;
    }

    public enum PropertySchemaComplexity {
        Simple, Complex, DataType
    }

    public static PropertySchemaType getEnumByValue(String type){
        if (type == null) {
            throwNoSuchElementException(null);
        }

        if (type.contains(DATATYPE.getSchemaTypeName())) {
            return DATATYPE;
        }
        PropertySchemaType propertySchemaType = Arrays.stream(PropertySchemaType.values())
                .filter(v->v.getSchemaTypeName().equals(type))
                .findFirst().orElse(null);
        if (propertySchemaType == null) {
            throwNoSuchElementException(type);
        }
        return propertySchemaType;
    }

    private static void throwNoSuchElementException(String type) {
        throw new NoSuchElementException(String.format("Value %s is not defined in %s", type, PropertySchemaType.class.getName()));
    }

}

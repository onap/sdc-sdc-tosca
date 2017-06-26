package org.openecomp.sdc.tosca.parser.impl;

public enum FilterType {

    CONTAINS("contains"){
        @Override
        public boolean isMatch(String value, String pattern) {
            return value.contains(pattern);
        }
    },
    EQUALS("equals"){
        @Override
        public boolean isMatch(String value, String pattern) {
            return value.equals(pattern);
        }
    };

    String filterName;

    FilterType(String name) {
        this.filterName = name;
    }

    public abstract boolean isMatch(String value, String pattern);

}

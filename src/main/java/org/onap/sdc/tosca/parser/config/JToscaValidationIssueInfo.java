package org.onap.sdc.tosca.parser.config;

public class JToscaValidationIssueInfo {

    private String issueType;
    private String sinceCsarConformanceLevel;

    public JToscaValidationIssueInfo() {}

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getSinceCsarConformanceLevel() {
        return sinceCsarConformanceLevel;
    }

    public void setSinceCsarConformanceLevel(String sinceCsarConformanceLevel) {
        this.sinceCsarConformanceLevel = sinceCsarConformanceLevel;
    }

    public void cloneData(JToscaValidationIssueInfo other) {
        this.issueType = other.getIssueType();
        this.sinceCsarConformanceLevel = other.getSinceCsarConformanceLevel();
    }

}

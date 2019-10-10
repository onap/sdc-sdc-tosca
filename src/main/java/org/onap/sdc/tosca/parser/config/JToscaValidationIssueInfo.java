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

package org.onap.sdc.tosca.parser.config;

public class JToscaValidationIssueInfo {

    private String issueType;
    private String sinceCsarConformanceLevel;

    public JToscaValidationIssueInfo() {
    }

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

/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.openecomp.sdc.tosca.parser.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JtoscaValidationIssueConfiguration {

    private Map<String, List<JToscaValidationIssueInfo>> validationIssues;

    public Map<String, List<JToscaValidationIssueInfo>> getValidationIssues() {
		return validationIssues;
	}

	public void setValidationIssues(Map<String, List<JToscaValidationIssueInfo>> validationIssues) {
		this.validationIssues = validationIssues;
	}
	
	public List<JToscaValidationIssueInfo> getJtoscaValidationIssueInfo(String key) {
        List<JToscaValidationIssueInfo> clone = new ArrayList<>();
        List<JToscaValidationIssueInfo> other = validationIssues.get(key);
        if (other != null) {
            for (JToscaValidationIssueInfo item: other) {
                JToscaValidationIssueInfo cloneitem = new JToscaValidationIssueInfo();
                cloneitem.cloneData(item);
                clone.add(cloneitem);
            }
        }
        return clone;
    }

}

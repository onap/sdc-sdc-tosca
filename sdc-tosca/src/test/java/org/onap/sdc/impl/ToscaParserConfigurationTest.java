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

package org.onap.sdc.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.sdc.tosca.parser.config.ErrorConfiguration;
import org.onap.sdc.tosca.parser.config.JtoscaValidationIssueConfiguration;
import org.junit.jupiter.api.Test;
import org.onap.sdc.tosca.parser.config.Configuration;
import org.onap.sdc.tosca.parser.config.ConfigurationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SdcToscaParserBasicTest.class})
 class ToscaParserConfigurationTest extends SdcToscaParserBasicTest {

    @Test
    public void testConfigurationConformanceLevel()  {
        Configuration config = ConfigurationManager.getInstance().getConfiguration();
        assertNotNull(config);
        assertNotNull(config.getConformanceLevel());
        assertNotNull(config.getConformanceLevel().getMaxVersion());
        assertNotNull(config.getConformanceLevel().getMinVersion());
    }


    @Test
    public void testErrorConfigurations()  {
        ErrorConfiguration errorConfig = ConfigurationManager.getInstance().getErrorConfiguration();
        assertNotNull(errorConfig);
        assertNotNull(errorConfig.getErrors());
    }

    @Test
    public void testSetErrorConfiguration() {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        try {
            configurationManager.setErrorConfiguration("error-configuration-test.yaml");
            ErrorConfiguration errorConfig = configurationManager.getErrorConfiguration();
            assertEquals(false,
                errorConfig.getErrorInfo("CONFORMANCE_LEVEL_ERROR").getFailOnError());
            assertEquals(true, errorConfig.getErrorInfo("FILE_NOT_FOUND").getFailOnError());
        }
        finally {
            // Reset the configuration for other tests
            configurationManager.setErrorConfiguration("error-configuration.yaml");
        }
    }

    @Test
    public void testSetJtoscaValidationIssueConfiguration() {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        try {
            configurationManager.setJtoscaValidationIssueConfiguration(
                "jtosca-validation-issue-configuration-test.yaml");
            JtoscaValidationIssueConfiguration issueConfig = configurationManager
                .getJtoscaValidationIssueConfiguration();
            assertNotNull(issueConfig);
        }
        finally {
            // Reset the configuration for other tests
            configurationManager.setJtoscaValidationIssueConfiguration
                ("jtosca-validation-issue-configuration.yaml");
        }
    }
}

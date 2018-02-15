package org.openecomp.sdc.impl;

import org.openecomp.sdc.tosca.parser.config.ErrorConfiguration;
import org.openecomp.sdc.tosca.parser.config.JtoscaValidationIssueConfiguration;
import org.testng.annotations.Test;
import org.openecomp.sdc.tosca.parser.config.Configuration;
import org.openecomp.sdc.tosca.parser.config.ConfigurationManager;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ToscaParserConfigurationTest extends SdcToscaParserBasicTest {

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

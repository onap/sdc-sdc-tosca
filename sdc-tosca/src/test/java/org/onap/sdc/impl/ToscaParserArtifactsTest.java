/*
 * -
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.sdc.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.ArtifactTypeDef;

class ToscaParserArtifactsTest {

    private static final String TEST_ARTIFACTS_FILENAME = "csars/resource-VspWithArtifacts.csar";
    private static final String TEST_ARTIFACT_TYPE = "tosca.artifacts.asd.deploymentItem";
    private static final List<String> ARTIFACTS_NAME_LIST = Arrays.asList("sampleapp-db", "sampleapp-services");
    private static ISdcCsarHelper helper = null;

    @BeforeAll
    public static void setup() throws Exception {
        final URL resource = ToscaParserArtifactsTest.class.getClassLoader().getResource(TEST_ARTIFACTS_FILENAME);
        if (resource != null) {
            helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
        }
        assertNotNull(helper);
    }

    @Test
    void testGetArtifacts() {
        final NodeTemplate nodeTemplate = helper.getServiceNodeTemplates().get(0);
        assertNotNull(nodeTemplate);
        final Map<String, ArtifactTypeDef> artifacts = nodeTemplate.getArtifacts();
        assertNotNull(artifacts);
        assertEquals(2, artifacts.size());
        artifacts.entrySet().forEach(entry -> {
            assertTrue(ARTIFACTS_NAME_LIST.contains(entry.getKey()));
            final ArtifactTypeDef artifactTypeDef = entry.getValue();
            assertNotNull(artifactTypeDef);
            final String artifactTypeDefType = artifactTypeDef.getType();
            assertNotNull(artifactTypeDefType);
            assertEquals(TEST_ARTIFACT_TYPE, artifactTypeDefType);
            final LinkedHashMap<String, Object> properties = artifactTypeDef.getProperties();
            assertNotNull(properties);
            assertFalse(properties.isEmpty());
            final String file = (String) artifactTypeDef.getDefinition("file");
            assertNotNull(file);
            assertTrue(file.startsWith("../Artifacts/Deployment/HELM/sampleapp-"));
        });

    }

}

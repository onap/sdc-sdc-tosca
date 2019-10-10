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

package org.onap.sdc.tosca.parser.impl;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URL;
import java.util.List;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.api.NodeTemplate;
import org.onap.sdc.tosca.parser.api.Policy;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ToscaParserPolicyTest {

    private static ISdcCsarHelper helper = null;

    @BeforeClass
    public static void setUpClass() {
        try {
            URL resource = GetEntityPortMirroringTest.class.getClassLoader()
                .getResource("csars/service-CgnatFwVnfNc-csar.csar");
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPolicyOfTargetByNodeTemplate() {
        List<NodeTemplate> vfList = helper.getServiceVfList();
        assertEquals(1, vfList.size());
        List<Policy> policies = helper.getPoliciesOfTarget(vfList.get(0));
        assertNotNull(policies);
        assertTrue(policies.isEmpty());
    }

    @Test
    public void getPolicyTargetFromOrigin() {
        List<NodeTemplate> vfList = helper.getServiceVfList();
        assertEquals(1, vfList.size());
        List<NodeTemplate> targets = helper.getPolicyTargetsFromOrigin(vfList.get(0), "cgnatfwvnf_nc..External..0");
        assertNotNull(targets);
        assertTrue(targets.isEmpty());
    }


}

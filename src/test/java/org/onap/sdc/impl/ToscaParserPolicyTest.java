package org.onap.sdc.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Policy;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

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

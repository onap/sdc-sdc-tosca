package org.openecomp.sdc.impl;

import org.openecomp.sdc.tosca.parser.impl.SdcTypes;
import org.openecomp.sdc.toscaparser.api.CapabilityAssignment;
import org.openecomp.sdc.toscaparser.api.CapabilityAssignments;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.RequirementAssignments;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class ToscaParserReqAndCapTest extends SdcToscaParserBasicTest {

    //region getCapabilitiesOf
    @Test
    public void testGetCapabilitiesOfNodeTemplate() {
        List<NodeTemplate> vfs = fdntCsarHelper.getServiceVfList();
        CapabilityAssignments capabilityAssignments = fdntCsarHelper.getCapabilitiesOf(vfs.get(0));
        assertNotNull(capabilityAssignments);
        assertEquals(13, capabilityAssignments.getAll().size());
        assertNotNull(capabilityAssignments.getCapabilityByName("DNT_FW_RHRG.binding_DNT_FW_INT_DNS_TRUSTED_RVMI"));
        assertEquals(6, capabilityAssignments.getCapabilitiesByType("tosca.capabilities.network.Bindable").getAll().size());
    }

    @Test
    public void testGetCapabilitiesOfNull() {
        CapabilityAssignments capabilityAssignments = fdntCsarHelper.getCapabilitiesOf(null);
        assertNull(capabilityAssignments);
    }
    //endregion

    //region getRequirementsOf
    @Test
    public void testGetRequirementsOfNodeTemplate() {
        List<NodeTemplate> vfs = fdntCsarHelper.getServiceVfList();
        List<NodeTemplate> cps = fdntCsarHelper.getNodeTemplateBySdcType(vfs.get(0), SdcTypes.CP);
        RequirementAssignments requirementAssignments = fdntCsarHelper.getRequirementsOf(cps.get(0));
        assertNotNull(requirementAssignments);
        assertEquals(1, requirementAssignments.getAll().size());
        assertEquals("DNT_FW_RHRG", requirementAssignments.getRequirementsByName("binding").getAll().get(0).getNodeTemplateName());
    }

    @Test
    public void testGetRequirementsOfNull() {
        RequirementAssignments requirementAssignments = fdntCsarHelper.getRequirementsOf(null);
        assertNull(requirementAssignments);
    }
    //endregion

    //region getCapabilityPropertyLeafValue
    @Test
    public void testGetCapabilityPropertyLeafValue() {
        NodeTemplate vf = fdntCsarHelper.getServiceNodeTemplateByNodeName("FDNT 1");
        CapabilityAssignment capabilityAssignment = vf.getCapabilities().getCapabilityByName("DNT_FW_RHRG.scalable_DNT_FW_SERVER");
        assertNotNull(capabilityAssignment);
        String propValue = fdntCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, "max_instances#type");
        assertEquals("integer", propValue);
    }

    @Test
    public void testGetCapabilityHierarchyPropertyLeafValue() {
        NodeTemplate vf = fdntCsarHelper.getServiceNodeTemplateByNodeName("FDNT 1");
        CapabilityAssignment capabilityAssignment = vf.getCapabilities().getCapabilityByName("DNT_FW_RHRG.endpoint_DNT_FW_SERVER");
        assertNotNull(capabilityAssignment);
        String propValue = fdntCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, "ports#entry_schema#type");
        assertEquals("PortSpec", propValue);
    }

    @Test
    public void testGetCapabilityDummyPropertyLeafValue() {
        NodeTemplate vf = fdntCsarHelper.getServiceNodeTemplateByNodeName("FDNT 1");
        CapabilityAssignment capabilityAssignment = vf.getCapabilities().getCapabilityByName("DNT_FW_RHRG.scalable_DNT_FW_SERVER");
        assertNotNull(capabilityAssignment);
        String propValue = fdntCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, "dummy");
        assertNull(propValue);
    }

    @Test
    public void testGetCapabilityNullPropertyLeafValue() {
        NodeTemplate vf = fdntCsarHelper.getServiceNodeTemplateByNodeName("FDNT 1");
        CapabilityAssignment capabilityAssignment = vf.getCapabilities().getCapabilityByName("DNT_FW_RHRG.scalable_DNT_FW_SERVER");
        assertNotNull(capabilityAssignment);
        String propValue = fdntCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, null);
        assertNull(propValue);
    }

    @Test
    public void testGetNullCapabilityPropertyLeafValue() {
        String propValue = fdntCsarHelper.getCapabilityPropertyLeafValue(null, "max_instances#type");
        assertNull(propValue);
    }
    //endregion
}

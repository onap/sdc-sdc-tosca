package org.openecomp.sdc.impl;

import org.openecomp.sdc.tosca.parser.impl.SdcTypes;
import org.openecomp.sdc.toscaparser.api.CapabilityAssignments;
import org.openecomp.sdc.toscaparser.api.CapabilityAssignment;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.openecomp.sdc.toscaparser.api.RequirementAssignments;
import org.openecomp.sdc.toscaparser.api.elements.Metadata;
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
    
	//QA tests region for US 319197 -port mirroring-
    
	//get-CapabilitiesOf (All Types)
	@Test
	public void getServiceNodeTemplateCapabilitiesOfTypeVF() {
		List<NodeTemplate> serviceVfList = QAServiceForToscaParserTests.getServiceNodeTemplateBySdcType(SdcTypes.VF);
		CapabilityAssignments capabilitiesOfVF = QAServiceForToscaParserTests.getCapabilitiesOf(serviceVfList.get(0));
		assertEquals(capabilitiesOfVF.getAll().size(),12);
		assertNotNull(capabilitiesOfVF.getCapabilityByName("neutronport0.network.incoming.packets.rate"));
	}
	
	@Test
	public void getServiceNodeTemplateCapabilitiesOfTypeExVL() {
		List<NodeTemplate> serviceExtVlList = QAServiceForToscaParserTests.getServiceNodeTemplatesByType("org.openecomp.resource.vl.extVL");
		CapabilityAssignments capabilitiesOfExtVL = QAServiceForToscaParserTests.getCapabilitiesOf(serviceExtVlList.get(0));
		assertEquals(capabilitiesOfExtVL.getAll().size(),2);
		assertNotNull(capabilitiesOfExtVL.getCapabilityByName("virtual_linkable").getProperties());
	}
	
	@Test
	public void getServiceNodeTemplateCapabilitiesOfTypeVL() {
		List<NodeTemplate> serviceVlList = QAServiceForToscaParserTests.getServiceNodeTemplatesByType("tosca.nodes.network.Network");
		CapabilityAssignments capabilitiesOfVL = QAServiceForToscaParserTests.getCapabilitiesOf(serviceVlList.get(0));
		assertEquals(capabilitiesOfVL.getAll().size(),2);
		assertNotNull(capabilitiesOfVL.getCapabilityByName("link").getProperties());
	}
	
	@Test
	public void getServiceNodeTemplateCapabilitiesOfTypeCP() {
		List<NodeTemplate> serviceCpList = QAServiceForToscaParserTests.getServiceNodeTemplateBySdcType(SdcTypes.CP);
		CapabilityAssignments capabilitiesOfCP = QAServiceForToscaParserTests.getCapabilitiesOf(serviceCpList.get(0));
		assertEquals(capabilitiesOfCP.getAll().size(),2);
		assertNotNull(capabilitiesOfCP.getCapabilityByName("internal_connectionPoint"));
	}
	
	@Test
	public void getServiceNodeTemplateCapabilitiesOfTypePNF() {
		List<NodeTemplate> servicePnfs = QAServiceForToscaParserTests.getServiceNodeTemplateBySdcType(SdcTypes.PNF);
		CapabilityAssignments capabilitiesOfPnf = QAServiceForToscaParserTests.getCapabilitiesOf(servicePnfs.get(0));
		assertEquals(capabilitiesOfPnf.getAll().size(),1);
		assertNotNull(capabilitiesOfPnf.getCapabilityByName("feature"));
	}
	
	//get-RequirementsOf (All Types)-----------------------------
	
	@Test
	public void getServiceNodeTemplateRequirementsOfTypeVF() {
		List<NodeTemplate> serviceVfList = QAServiceForToscaParserTests.getServiceNodeTemplateBySdcType(SdcTypes.VF);
		RequirementAssignments requirementsOfVF = QAServiceForToscaParserTests.getRequirementsOf(serviceVfList.get(6));
		assertEquals(requirementsOfVF.getAll().size(),3);
//		RequirementAssignments requirementsByName = 
		assertEquals(requirementsOfVF.getRequirementsByName("dependency").getAll().size(),2 );
		//check that API return empty list if requirement property not exist.
		assertEquals(requirementsOfVF.getRequirementsByName("blabla").getAll().size(),0);
	}
	
	@Test
	public void getServiceNodeTemplateRequirementsOfTypeExVL() {
		List<NodeTemplate> serviceExtVlList = QAServiceForToscaParserTests.getServiceNodeTemplatesByType("org.openecomp.resource.vl.extVL");
		RequirementAssignments requirementsOfExtVL = QAServiceForToscaParserTests.getRequirementsOf(serviceExtVlList.get(0));
		assertEquals(requirementsOfExtVL.getAll().size(),1);
	}
	
	@Test
	public void getServiceNodeTemplateRequirementsOfTypeVL() {
		List<NodeTemplate> serviceVlList = QAServiceForToscaParserTests.getServiceNodeTemplatesByType("tosca.nodes.network.Network");
		RequirementAssignments requirementsOfVL = QAServiceForToscaParserTests.getRequirementsOf(serviceVlList.get(1));
		assertEquals(requirementsOfVL.getAll().size(),2);
		assertNotNull(requirementsOfVL.getRequirementsByName("dependency"));
	}
	
	@Test
	public void getServiceNodeTemplateRequirementsOfTypeCP() {
		List<NodeTemplate> serviceCpList = QAServiceForToscaParserTests.getServiceNodeTemplateBySdcType(SdcTypes.CP);
		RequirementAssignments requirementsOfCP = QAServiceForToscaParserTests.getRequirementsOf(serviceCpList.get(0));
		assertEquals(requirementsOfCP.getAll().size(),2);
		assertEquals(requirementsOfCP.getRequirementsByName("virtualBinding").getAll().size(),1);
	}
	
	@Test
	public void getServiceNodeTemplateRequirementsOfTypePNF() {
		List<NodeTemplate> servicePnfs = QAServiceForToscaParserTests.getServiceNodeTemplateBySdcType(SdcTypes.PNF);
		RequirementAssignments requirementsOfPnf = QAServiceForToscaParserTests.getRequirementsOf(servicePnfs.get(0));
		assertEquals(requirementsOfPnf.getAll().size(),2);
		assertNotNull(requirementsOfPnf.getRequirementsByName("feature"));
	}
	
	//QA end region for US 319197 -port mirroring
	
    // Added by QA CapabilityAssignments
    @Test
    public void testGetCapabilitiesByType() {
        List<NodeTemplate> vfs = resolveGetInputCsarQA.getServiceVfList();
        CapabilityAssignments capabilityAssignments = resolveGetInputCsarQA.getCapabilitiesOf(vfs.get(0));
        assertNotNull(capabilityAssignments);
        CapabilityAssignments capabilitiesByType = capabilityAssignments.getCapabilitiesByType("tosca.capabilities.Scalable");
        int capsQty = capabilitiesByType.getAll().size();
        assertEquals(1, capsQty);
        CapabilityAssignment capabilityByName = capabilitiesByType.getCapabilityByName("abstract_pd_server.scalable_pd_server");
        assertNotNull(capabilityByName);
    }
    
    @Test
    public void testGetCapabilityByName() {
        List<NodeTemplate> vfs = resolveGetInputCsarQA.getServiceVfList();
        CapabilityAssignments capabilityAssignments = resolveGetInputCsarQA.getCapabilitiesOf(vfs.get(0));
        assertNotNull(capabilityAssignments);
        CapabilityAssignment capabilityByName = capabilityAssignments.getCapabilityByName("abstract_pd_server.disk.iops_pd_server");
        assertNotNull(capabilityByName);
        String capName = capabilityByName.getName();
        assertEquals(capName, "abstract_pd_server.disk.iops_pd_server");
    }
    
    @Test
    public void testGetCapabilitiesGetAll() {
        List<NodeTemplate> vfs = resolveGetInputCsarQA.getServiceVfList();
        CapabilityAssignments capabilityAssignments = resolveGetInputCsarQA.getCapabilitiesOf(vfs.get(0));
        assertNotNull(capabilityAssignments);
        int capsAssignmentSize = capabilityAssignments.getAll().size();
        assertEquals(65, capsAssignmentSize);        
    }
    
 // Added by QA RequirementsAssignments
    @Test
    public void testGetRequirementsByName() {
    	 List<NodeTemplate> vfs = resolveReqsCapsCsarQA.getServiceVfList();
         List<NodeTemplate> cps = resolveReqsCapsCsarQA.getNodeTemplateBySdcType(vfs.get(0), SdcTypes.CP);
         RequirementAssignments requirementAssignments = resolveReqsCapsCsarQA.getRequirementsOf(cps.get(0));
         assertNotNull(requirementAssignments);
         assertEquals(2, requirementAssignments.getAll().size());
         assertEquals("DNT_FW_RHRG2", requirementAssignments.getRequirementsByName("binding").getAll().get(1).getNodeTemplateName());
  
    }
    
    @Test
    public void testRequirementAssignmentGetNodeGetCapability() {
    	 List<NodeTemplate> vfs = resolveReqsCapsCsarQA.getServiceVfList();
         List<NodeTemplate> cps = resolveReqsCapsCsarQA.getNodeTemplateBySdcType(vfs.get(0), SdcTypes.CP);
         RequirementAssignments requirementAssignments = resolveReqsCapsCsarQA.getRequirementsOf(cps.get(0));
         assertNotNull(requirementAssignments);
         String nodeTemplateName = requirementAssignments.getAll().get(0).getNodeTemplateName();
         String capabilityName = requirementAssignments.getAll().get(0).getCapabilityName();
         assertEquals(nodeTemplateName, "DNT_FW_RHRG");
         assertNull(capabilityName);
    }
    
    
    @Test
    public void testRequirementAssignmentGetCapability() {
    	 List<NodeTemplate> cps = QAServiceForToscaParserTests.getServiceNodeTemplateBySdcType(SdcTypes.CP);
         RequirementAssignments requirementAssignments = QAServiceForToscaParserTests.getRequirementsOf(cps.get(0));
         assertNotNull(requirementAssignments);        
         String nodeTemplateName = requirementAssignments.getAll().get(0).getNodeTemplateName();
         String capabilityName = requirementAssignments.getAll().get(0).getCapabilityName();
         assertEquals(nodeTemplateName, "ExtVL 0");
         assertEquals(capabilityName,"tosca.capabilities.network.Linkable");
    }
    
    @Test
    public void testGetCapabilityProperties() {
        List<NodeTemplate> vfs = fdntCsarHelper.getServiceVfList();
        List<NodeTemplate> cps = fdntCsarHelper.getNodeTemplateBySdcType(vfs.get(0), SdcTypes.CP);
        CapabilityAssignments capabilityAssignments = cps.get(0).getCapabilities();
        assertNotNull(capabilityAssignments);
        assertEquals(12, capabilityAssignments.getAll().size());
        CapabilityAssignment xxxCapability = capabilityAssignments.getCapabilityByName("xxx");
        //GetInput property - resolved in any case
        String getInputProp = fdntCsarHelper.getCapabilityPropertyLeafValue(xxxCapability, "DeploymentFlavor");
        assertEquals("{aaa=bbb}", getInputProp);
        //Simple property
        String simpleProp = fdntCsarHelper.getCapabilityPropertyLeafValue(xxxCapability, "distribution");
        assertEquals("rhel", simpleProp);
    }

}

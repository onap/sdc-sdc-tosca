package org.openecomp.sdc.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.impl.FilterType;
import org.openecomp.sdc.tosca.parser.impl.SdcTypes;
import org.openecomp.sdc.toscaparser.api.Group;
import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.testng.annotations.Test;

public class ToscaParserNodeTemplateTest extends SdcToscaParserBasicTest {

	//region getServiceVfList
	@Test
	public void testNumberOfVfSunnyFlow() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		assertNotNull(serviceVfList);
		assertEquals(2, serviceVfList.size());
	}
	
	@Test
	public void testGetNodeTemplateCustomizationUuid(){
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		boolean found = false;
		for (NodeTemplate nt : serviceVfList){
			if (nt.getName().equals("FDNT 1")){
				found = true;
				assertEquals(fdntCsarHelper.getNodeTemplateCustomizationUuid(nt), "56179cd8-de4a-4c38-919b-bbc4452d2d73");
			}
		}
		assertTrue(found);
	}

	@Test
	public void testSingleVFWithNotMetadata() throws SdcToscaParserException {
		//If there is no metadata on VF level - There is no VF's because the type is taken from metadata values.
		List<NodeTemplate> serviceVfList = rainyCsarHelperSingleVf.getServiceVfList();
		assertNotNull(serviceVfList);
		assertEquals(0, serviceVfList.size());
	}
	//endregion

	//region getNodeTemplatePropertyLeafValue
	@Test
	public void testNodeTemplateFlatProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		assertEquals("2", fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "availability_zone_max_count"));
		assertEquals("3", fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "max_instances"));
		assertEquals("some code", fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "nf_naming_code"));
	}

	@Test
	public void testNodeTemplateFlatFunctionProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelperWithInputs.getServiceVfList();
		assertEquals(null, fdntCsarHelperWithInputs.getNodeTemplatePropertyLeafValue(serviceVfList.get(1), "target_network_role"));
	}

	@Test
	public void testNodeTemplateNestedProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVlList = fdntCsarHelper.getServiceVlList();
		NodeTemplate nodeTemplate = serviceVlList.get(0);
		//System.out.println("node template " + nodeTemplate.toString());
		assertEquals("24", fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "network_assignments#ipv4_subnet_default_assignment#cidr_mask"));
		assertEquals("7a6520b-9982354-ee82992c-105720", fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "network_flows#vpn_binding"));
	}

	@Test
	public void testNodeTemplateNestedPropertyFromInput() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		NodeTemplate nodeTemplate = serviceVfList.get(0);
		//System.out.println("node template " + nodeTemplate.toString());
		assertEquals("true", fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "nf_naming#ecomp_generated_naming"));
		assertEquals("FDNT_instance_VF_2", fdntCsarHelper.getNodeTemplatePropertyLeafValue(nodeTemplate, "nf_naming#naming_policy"));
	}

	@Test
	public void testNodeTemplateNestedPropertyNotExists() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		String nodeTemplatePropertyLeafValue = fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "nf_role#nf_naming#kuku");
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNodeTemplateFlatPropertyByNotFoundProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = rainyCsarHelperMultiVfs.getServiceVfList();
		String nodeTemplatePropertyLeafValue = rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), "XXXX");
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNodeTemplateFlatPropertyByNullProperty() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = rainyCsarHelperMultiVfs.getServiceVfList();
		String nodeTemplatePropertyLeafValue = rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(serviceVfList.get(0), null);
		assertNull(nodeTemplatePropertyLeafValue);
	}

	@Test
	public void testNodeTemplateFlatPropertyByNullNodeTemplate() throws SdcToscaParserException {
		String nodeTemplatePropertyLeafValue = rainyCsarHelperMultiVfs.getNodeTemplatePropertyLeafValue(null, "availability_zone_max_count");
		assertNull(nodeTemplatePropertyLeafValue);
	}
	//endregion

	//region getServiceVlList
	@Test
	public void testServiceVl() {
		List<NodeTemplate> vlList = fdntCsarHelper.getServiceVlList();
		assertEquals(1, vlList.size());
		assertEquals("exVL", vlList.get(0).getName());
	}

	@Test
	public void testNumberOfVLRainyFlow() throws SdcToscaParserException {
		List<NodeTemplate> serviceVlList = rainyCsarHelperMultiVfs.getServiceVlList();
		assertNotNull(serviceVlList);
		assertEquals(0, serviceVlList.size());
	}
	//endregion

	//region getServiceNodeTemplatesByType
	@Test
	public void testServiceNodeTemplatesByType() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceNodeTemplatesByType("org.openecomp.resource.vf.Fdnt");
		assertNotNull(serviceVfList);
		assertEquals(1, serviceVfList.size());
	}

	@Test
	public void testServiceNodeTemplatesByNull() {
		List<NodeTemplate> nodeTemplates = rainyCsarHelperMultiVfs.getServiceNodeTemplatesByType(null);
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}

	@Test
	public void testServiceNodeTemplatesByNotFoundProperty() {
		List<NodeTemplate> nodeTemplates = rainyCsarHelperMultiVfs.getServiceNodeTemplatesByType("XXX");
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}
	//endregion

	//region getTypeOfNodeTemplate
	@Test
	public void testGetTypeOfNodeTemplate() {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		String typeOfNodeTemplate = fdntCsarHelper.getTypeOfNodeTemplate(serviceVfList.get(0));
		assertEquals("org.openecomp.resource.vf.Fdnt", typeOfNodeTemplate);
	}

	@Test
	public void testGetTypeOfNullNodeTemplate() {
		String typeOfNodeTemplate = rainyCsarHelperMultiVfs.getTypeOfNodeTemplate(null);
		assertNull(typeOfNodeTemplate);
	}
	//endregion

	//region getAllottedResources
	@Test
	public void testGetAllottedResources() {
		List<NodeTemplate> allottedResources = fdntCsarHelper.getAllottedResources();
		assertEquals(1, allottedResources.size());
	}

	@Test
	public void testGetAllottedResourcesZero() {
		List<NodeTemplate> allottedResources = rainyCsarHelperMultiVfs.getAllottedResources();
		assertNotNull(allottedResources);
		assertEquals(0, allottedResources.size());
	}
	//endregion

	//region getVfcListByVf
	@Test
	public void testGetVfcFromVf() {
		List<NodeTemplate> vfcListByVf = fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID);
		assertEquals(2, vfcListByVf.size());
	}

	@Test
	public void testVfcListByNull() {
		List<NodeTemplate> vfcList = rainyCsarHelperMultiVfs.getVfcListByVf(null);
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}

	@Test
	public void testVfcListByNotFoundProperty() {
		List<NodeTemplate> vfcList = rainyCsarHelperMultiVfs.getVfcListByVf("XXX");
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}
	//endregion

	//region getCpListByVf
	@Test
	public void testGetCpFromVf() {
		List<NodeTemplate> cpListByVf = fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID);
		assertEquals(1, cpListByVf.size());
		NodeTemplate nodeTemplate = cpListByVf.get(0);
		assertEquals("DNT_PORT", nodeTemplate.getName());
	}

	@Test
	public void testGetCpFromVfByNullId() {
		List<NodeTemplate> cpListByVf = rainyCsarHelperMultiVfs.getCpListByVf(null);
		assertNotNull(cpListByVf);
		assertEquals(0, cpListByVf.size());
	}

	@Test
	public void testGetCpFromVfXxx() {
		List<NodeTemplate> cpListByVf = rainyCsarHelperMultiVfs.getCpListByVf("XXXXX");
		assertNotNull(cpListByVf);
		assertEquals(0, cpListByVf.size());
	}
	//endregion

	//region getNodeTemplatePairsByReqName
	@Test
	public void testGetNodeTemplatePairsByReqName() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(1, nodeTemplatePairsByReqName.size());
		Pair<NodeTemplate, NodeTemplate> pair = nodeTemplatePairsByReqName.get(0);
		NodeTemplate cp = pair.getLeft();
		NodeTemplate vfc = pair.getRight();
		assertEquals("DNT_PORT", cp.getName());
		assertEquals("DNT_FW_RHRG", vfc.getName());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullVF() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				null, fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithEmptyVF() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				new ArrayList<>(), fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullCap() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), null, "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithEmptyCap() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), new ArrayList<>(), "binding");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithNullReq() {
		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), null);
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}

	@Test
	public void testGetNodeTemplatePairsByReqNameWithDummyReq() {

		List<Pair<NodeTemplate, NodeTemplate>> nodeTemplatePairsByReqName = fdntCsarHelper.getNodeTemplatePairsByReqName(
				fdntCsarHelper.getCpListByVf(VF_CUSTOMIZATION_UUID), fdntCsarHelper.getVfcListByVf(VF_CUSTOMIZATION_UUID), "XXX");
		assertNotNull(nodeTemplatePairsByReqName);
		assertEquals(0, nodeTemplatePairsByReqName.size());
	}
	//endregion

	//region getMembersOfVfModule
	@Test
	public void testGetMembersOfVfModule() {
		NodeTemplate vf = fdntCsarHelper.getServiceVfList().get(0);
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
		assertEquals(2, vfModulesByVf.size());
		for (Group group : vfModulesByVf) {
			List<NodeTemplate> membersOfVfModule = fdntCsarHelper.getMembersOfVfModule(vf, group);
			assertNotNull(membersOfVfModule);
			if (group.getName().equals("fdnt1..Fdnt..base_stsi_dnt_frwl..module-0")) {
				assertEquals(1, membersOfVfModule.size());
				NodeTemplate nodeTemplate = membersOfVfModule.get(0);
				assertEquals("DNT_FW_RSG_SI_1", nodeTemplate.getName());
			} else {
				assertEquals("fdnt1..Fdnt..mod_vmsi_dnt_fw_parent..module-1", group.getName());
				assertEquals(1, membersOfVfModule.size());
				NodeTemplate nodeTemplate = membersOfVfModule.get(0);
				assertEquals("DNT_FW_RHRG", nodeTemplate.getName());
			}
		}
	}

	@Test
	public void testMembersOfVfModuleByNullVf() {
		List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
		List<NodeTemplate> nodeTemplates = fdntCsarHelper.getMembersOfVfModule(null, vfModulesByVf.get(0));
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}

	@Test
	public void testMembersOfVfModuleByNullGroup() {
		List<NodeTemplate> serviceVfList = rainyCsarHelperMultiVfs.getServiceVfList();
		List<NodeTemplate> nodeTemplates = rainyCsarHelperMultiVfs.getMembersOfVfModule(serviceVfList.get(0), null);
		assertNotNull(nodeTemplates);
		assertEquals(0, nodeTemplates.size());
	}
	//endregion

	//region getCpPropertiesFromVfc
	@Test
	public void testGetCpPropertiesFromVfc() {
		List<NodeTemplate> vfcs = complexCps.getVfcListByVf("f999e2ca-72c0-42d3-9b11-13f2122fb8ef");
		boolean isChecked = false;
		boolean isChecked1 = false;
		for (int i = 0; i < vfcs.size(); i++) {

			if(vfcs.get(i).getName().equalsIgnoreCase("abstract_ddc"))
			{
				isChecked = true;
				Map<String, Map<String, Object>> cps = complexCps.getCpPropertiesFromVfc(vfcs.get(i));

				assertEquals(3,cps.size());

				assertEquals(new Integer(1), cps.get("port_ddc_int_imbl__port").get("ip_requirements#ip_count_required#count"));
				assertEquals(new Boolean(true), cps.get("port_ddc_int_imbl__port").get("ip_requirements#dhcp_enabled"));
				assertEquals(new Integer(6), cps.get("port_ddc_int_imbl__port").get("ip_requirements#ip_version"));
				assertEquals("subnetpoolid_test", cps.get("port_ddc_int_imbl__port").get("subnetpoolid"));
				assertEquals("int_imbl", cps.get("port_ddc_int_imbl__port").get("network_role_tag"));

			}
			
			if(vfcs.get(i).getName().equalsIgnoreCase("abstract_mda"))
			{
				isChecked1 = true;
				Map<String, Map<String, Object>> cps1 = complexCps.getCpPropertiesFromVfc(vfcs.get(i));

				assertEquals(new Integer(4), cps1.get("port_mda_int_imsp__port").get("ip_requirements#ip_version"));
				assertEquals(null, cps1.get("port_mda_int_imsp__port").get("ip_requirements#ip_count_required#count"));

			}

		}
		assertTrue(isChecked);
		assertTrue(isChecked1);
	}


	@Test
	public void testGetCpPropertiesFromVfcForNullVFC() {
		Map<String, Map<String, Object>> cps = complexCps.getCpPropertiesFromVfc(null);
		assertNotNull(cps);
		assertEquals(0, cps.size());
	}
	//endregion

	//region getNodeTemplatePropertyAsObject
	@Test
	public void testGetNodeTemplatePropertyAsObject() {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		assertEquals("2", fdntCsarHelper.getNodeTemplatePropertyAsObject(serviceVfList.get(0), "availability_zone_max_count"));
		assertEquals(3, fdntCsarHelper.getNodeTemplatePropertyAsObject(serviceVfList.get(0), "max_instances"));
		assertEquals("some code", fdntCsarHelper.getNodeTemplatePropertyAsObject(serviceVfList.get(0), "nf_naming_code"));
	}
	//endregion

	//region getServiceNodeTemplates
	@Test
	public void testServiceNodeTemplates() throws SdcToscaParserException {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceNodeTemplates();
		assertNotNull(serviceVfList);
		assertEquals(3, serviceVfList.size());
		assertEquals(serviceVfList.get(2).getName(), "exVL");
	}
	//endregion

    //region filterNodeTemplatePropertiesByValue
    @Test
    public void testFilterNodeTemplatePropertiesByContains() {
        List<NodeTemplate> vfcs = complexCps.getVfcListByVf("f999e2ca-72c0-42d3-9b11-13f2122fb8ef");
        boolean isChecked = false;
        for (NodeTemplate vfc: vfcs) {
            if(vfc.getName().equalsIgnoreCase("abstract_ddc"))
            {
                isChecked = true;
                Map<String, String> filteredInputs = complexCps.filterNodeTemplatePropertiesByValue(vfc, FilterType.CONTAINS, "get_input");

                assertEquals(16, filteredInputs.size());
                assertEquals("get_input:vnf_id", filteredInputs.get("compute_ddc_metadata#vf_module_id#vnf_id"));
				assertEquals("get_input:[ddc_int_imbl_v6_ips, 3]", filteredInputs.get("port_ddc_int_imbl__port_fixed_ips#ip_address"));

                break;
            }

        }
        assertTrue(isChecked);
    }

    @Test
    public void testFilterNodeTemplatePropertiesByDummyContains() {
        List<NodeTemplate> vfcs = complexCps.getVfcListByVf("f999e2ca-72c0-42d3-9b11-13f2122fb8ef");
        Map<String, String> filteredInputs = complexCps.filterNodeTemplatePropertiesByValue(vfcs.get(0), FilterType.CONTAINS, "dummy");
        assertNotNull(filteredInputs);
        assertEquals(0, filteredInputs.size());
    }

    @Test
    public void testFilterNodeTemplatePropertiesByEquals() {
        List<NodeTemplate> vfcs = complexCps.getVfcListByVf("f999e2ca-72c0-42d3-9b11-13f2122fb8ef");
        boolean isChecked = false;
        for (NodeTemplate vfc: vfcs) {
            if(vfc.getName().equalsIgnoreCase("abstract_ddc"))
            {
                isChecked = true;
                Map<String, String> filteredInputs = complexCps.filterNodeTemplatePropertiesByValue(vfc, FilterType.EQUALS, "ddc");

                assertEquals(2, filteredInputs.size());
                assertEquals("ddc", filteredInputs.get("vm_type_tag"));
                assertEquals("ddc", filteredInputs.get("nfc_naming_code"));
                break;
            }

        }
        assertTrue(isChecked);
    }

	@Test
	public void testFilterNodeTemplatePropertiesByDummyEquals() {
		List<NodeTemplate> vfcs = complexCps.getVfcListByVf("f999e2ca-72c0-42d3-9b11-13f2122fb8ef");
		Map<String, String> filteredInputs = complexCps.filterNodeTemplatePropertiesByValue(vfcs.get(0), FilterType.EQUALS, "dummy");
		assertNotNull(filteredInputs);
		assertEquals(0, filteredInputs.size());
	}

	@Test
	public void testFilterNodeTemplatePropertiesByNullFilterType() {
		List<NodeTemplate> vfcs = complexCps.getVfcListByVf("f999e2ca-72c0-42d3-9b11-13f2122fb8ef");
		Map<String, String> filteredInputs = complexCps.filterNodeTemplatePropertiesByValue(vfcs.get(11), null, "ddc");
		assertNotNull(filteredInputs);
		assertEquals(0, filteredInputs.size());
	}

	@Test
	public void testFilterNodeTemplatePropertiesByNullPattern() {
		List<NodeTemplate> vfcs = complexCps.getVfcListByVf("f999e2ca-72c0-42d3-9b11-13f2122fb8ef");
		Map<String, String> filteredInputs = complexCps.filterNodeTemplatePropertiesByValue(vfcs.get(11), FilterType.EQUALS, null);
		assertNotNull(filteredInputs);
		assertEquals(0, filteredInputs.size());
	}

	@Test
	public void testFilterNodeTemplatePropertiesByNullVfc() {
		Map<String, String> filteredInputs = complexCps.filterNodeTemplatePropertiesByValue(null, FilterType.EQUALS, "ddc");
		assertNotNull(filteredInputs);
		assertEquals(0, filteredInputs.size());
	}
	//endregion
    
	//region getServiceNodeTemplateBySdcType
	@Test
	public void testServiceNodeTemplateBySdcType() {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceNodeTemplateBySdcType(SdcTypes.VF);
		assertNotNull(serviceVfList);
		assertEquals(2, serviceVfList.size());
		assertEquals(serviceVfList.get(0).getName(), "FDNT 1");
	}

	@Test
	public void testServiceNodeTemplateByNullSdcType() {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceNodeTemplateBySdcType(null);
		assertNotNull(serviceVfList);
		assertEquals(serviceVfList.size(), 0);
	}
	//endregion

	//region getNodeTemplateBySdcType
	@Test
	public void testNodeTemplateBySdcType() {
		List<NodeTemplate> vfList = fdntCsarHelper.getServiceVfList();
		List<NodeTemplate> vfcList = fdntCsarHelper.getNodeTemplateBySdcType(vfList.get(0), SdcTypes.VFC);
		assertNotNull(vfcList);
		assertEquals(2, vfcList.size());
		assertEquals("DNT_FW_RSG_SI_1", vfcList.get(0).getName());
	}

	@Test
	public void testNodeTemplateByNullSdcType() {
		List<NodeTemplate> vfList = fdntCsarHelper.getServiceVfList();
		List<NodeTemplate> vfcList = fdntCsarHelper.getNodeTemplateBySdcType(vfList.get(0), null);
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}

	@Test
	public void testNodeTemplateBySdcTypeNullNT() {
		List<NodeTemplate> vfcList = fdntCsarHelper.getNodeTemplateBySdcType(null, SdcTypes.VFC);
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}
	//endregion
           


    
    @Test
    public void testGetVnfConfig() {
    	NodeTemplate vnfConfig = nfodCsarHlper.getVnfConfig("9bb2ef82-f8f6-4391-bc71-db063f15bf57");
    	assertNotNull(vnfConfig);
    	assertEquals("vnfConfiguration", vnfConfig.getMetaData().getValue("name"));
    }
    
    @Test
    public void testGetVnfConfigByNonFoundVNF() {
    	NodeTemplate vnfConfig = complexCps.getVnfConfig("f999e2ca-72c0-42d3-9b11-13f2122fb8ef");
    	assertNull(vnfConfig);
    }
    
    @Test
    public void testGetVnfConfigByDummyUUID() {
    	NodeTemplate vnfConfig = nfodCsarHlper.getVnfConfig("XXX");
    	assertNull(vnfConfig);
    }
    
    @Test
    public void testGetVnfConfigByNullUUID() {
    	NodeTemplate vnfConfig = nfodCsarHlper.getVnfConfig(null);
    	assertNull(vnfConfig);
    }
    
    @Test
    public void testGetVfcTypWithoutVnf() {
    	List<NodeTemplate> vfcList = nfodCsarHlper.getVfcListByVf("9bb2ef82-f8f6-4391-bc71-db063f15bf57");
    	assertNotNull(vfcList);
    	assertEquals(2, vfcList.size());
    }
    
}

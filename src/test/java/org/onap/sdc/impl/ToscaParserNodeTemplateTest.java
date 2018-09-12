package org.onap.sdc.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.enums.FilterType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.CapabilityAssignments;
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Policy;
import org.onap.sdc.toscaparser.api.Property;
import org.onap.sdc.toscaparser.api.parameters.Annotation;
import org.onap.sdc.toscaparser.api.parameters.Input;
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
	public void testNodeTemplateNestedFunctionProperty() throws SdcToscaParserException {
		List<NodeTemplate> vfcs = ipAssignCsarHelper.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		assertEquals(null, ipAssignCsarHelper.getNodeTemplatePropertyLeafValue(vfcs.get(0), "port_pd01_port_ip_requirements#ip_count_required#count"));
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
	@SuppressWarnings("unchecked")
	@Test
	public void testGetCpPropertiesFromVfc() {
		List<NodeTemplate> vfcs = ipAssignCsarHelper.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		boolean isChecked = false;

		for (NodeTemplate vfc: vfcs) {

			if(vfc.getName().equalsIgnoreCase("abstract_pd_server"))
			{
				isChecked = true;
				Map<String, Map<String, Object>> cps = ipAssignCsarHelper.getCpPropertiesFromVfcAsObject(vfc);

				assertEquals(2,cps.size());
				Map<String, Object> pd01 = cps.get("port_pd01_port"); 
				assertEquals(5, pd01.size());
				
				Map<String, Object> firstIpRequirements = (Map<String, Object>) ((List<Object>)pd01.get("ip_requirements")).get(0);
				Map<String, Object> secondIpRequirements = (Map<String, Object>) ((List<Object>)pd01.get("ip_requirements")).get(1);
				
				assertEquals("subnet_role_4", firstIpRequirements.get("subnet_role"));
				assertEquals(4, firstIpRequirements.get("ip_version"));
				assertEquals(true, ((Map<String, Object>) firstIpRequirements.get("ip_count_required")).get("is_required"));
				assertEquals("get_input:node_count", ((Map<String, Object>) firstIpRequirements.get("ip_count_required")).get("count").toString());
				assertEquals(false, ((Map<String, Object>)((Map<String, Object>)pd01.get("mac_requirements")).get("mac_count_required")).get("is_required"));
				assertEquals("test_subnetpoolid", pd01.get("subnetpoolid"));
				assertEquals("oam", pd01.get("network_role_tag"));
				assertEquals(6, secondIpRequirements.get("ip_version"));
			}			

		}
		assertTrue(isChecked);
	}

	@Test
	public void testGetCpPropertiesFromVfcForNullVFC() {
		Map<String, Map<String, Object>> cps = ipAssignCsarHelper.getCpPropertiesFromVfcAsObject(null);
		assertNotNull(cps);
		assertEquals(0, cps.size());
	}
	//endregion

	//region getNodeTemplatePropertyValueAsObject
	@Test
	public void testGetNodeTemplatePropertyAsObject() {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
		assertEquals("2", fdntCsarHelper.getNodeTemplatePropertyValueAsObject(serviceVfList.get(0), "availability_zone_max_count"));
		assertEquals(3, fdntCsarHelper.getNodeTemplatePropertyValueAsObject(serviceVfList.get(0), "max_instances"));
		assertEquals("some code", fdntCsarHelper.getNodeTemplatePropertyValueAsObject(serviceVfList.get(0), "nf_naming_code"));
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
        List<NodeTemplate> vfcs = ipAssignCsarHelper.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
        boolean isChecked = false;
        for (NodeTemplate vfc: vfcs) {
            if(vfc.getName().equalsIgnoreCase("abstract_pd_server"))
            {
                isChecked = true;
                Map<String, String> filteredInputs = ipAssignCsarHelper.filterNodeTemplatePropertiesByValue(vfc, FilterType.CONTAINS, "get_input");

                assertEquals(7, filteredInputs.size());
                assertEquals("get_input:availabilityzone_name", filteredInputs.get("compute_pd_server_availability_zone"));
				assertEquals("get_input:[pd_server_names, 0]", filteredInputs.get("compute_pd_server_name"));
				assertEquals("get_input:node_count", filteredInputs.get("port_pd01_port_ip_requirements#ip_count_required#count"));

                break;
            }

        }
        assertTrue(isChecked);
    }

    @Test
    public void testFilterNodeTemplatePropertiesByDummyContains() {
        List<NodeTemplate> vfcs = ipAssignCsarHelper.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
        Map<String, String> filteredInputs = ipAssignCsarHelper.filterNodeTemplatePropertiesByValue(vfcs.get(0), FilterType.CONTAINS, "dummy");
        assertNotNull(filteredInputs);
        assertEquals(0, filteredInputs.size());
    }

    @Test
    public void testFilterNodeTemplatePropertiesByEquals() {
        List<NodeTemplate> vfcs = ipAssignCsarHelper.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
        boolean isChecked = false;
        for (NodeTemplate vfc: vfcs) {
            if(vfc.getName().equalsIgnoreCase("abstract_pd_server"))
            {
                isChecked = true;
                Map<String, String> filteredInputs = ipAssignCsarHelper.filterNodeTemplatePropertiesByValue(vfc, FilterType.EQUALS, "oam");

                assertEquals(2, filteredInputs.size());
                assertEquals("oam", filteredInputs.get("port_pd02_port_network_role_tag"));
                assertEquals("oam", filteredInputs.get("port_pd01_port_network_role_tag"));
                break;
            }

        }
        assertTrue(isChecked);
    }

	@Test
	public void testFilterNodeTemplatePropertiesByDummyEquals() {
		List<NodeTemplate> vfcs = ipAssignCsarHelper.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Map<String, String> filteredInputs = ipAssignCsarHelper.filterNodeTemplatePropertiesByValue(vfcs.get(0), FilterType.EQUALS, "dummy");
		assertNotNull(filteredInputs);
		assertEquals(0, filteredInputs.size());
	}

	@Test
	public void testFilterNodeTemplatePropertiesByNullFilterType() {
		List<NodeTemplate> vfcs = ipAssignCsarHelper.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Map<String, String> filteredInputs = ipAssignCsarHelper.filterNodeTemplatePropertiesByValue(vfcs.get(0), null, "ddc");
		assertNotNull(filteredInputs);
		assertEquals(0, filteredInputs.size());
	}

	@Test
	public void testFilterNodeTemplatePropertiesByNullPattern() {
		List<NodeTemplate> vfcs = ipAssignCsarHelper.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Map<String, String> filteredInputs = ipAssignCsarHelper.filterNodeTemplatePropertiesByValue(vfcs.get(0), FilterType.EQUALS, null);
		assertNotNull(filteredInputs);
		assertEquals(0, filteredInputs.size());
	}

	@Test
	public void testFilterNodeTemplatePropertiesByNullVfc() {
		Map<String, String> filteredInputs = ipAssignCsarHelper.filterNodeTemplatePropertiesByValue(null, FilterType.EQUALS, "ddc");
		assertNotNull(filteredInputs);
		assertEquals(0, filteredInputs.size());
	}
	//endregion
    
	//region getServiceNodeTemplateBySdcType
	@Test
	public void testServiceNodeTemplateBySdcType() {
		List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceNodeTemplateBySdcType(SdcTypes.VF);
		assertNotNull(serviceVfList);
		assertEquals(serviceVfList.size(), 2);
		assertEquals(serviceVfList.get(0).getName(), "FDNT 1");
	}

	@Test
	public void testServiceNodeTemplateBySdcTypeServiceProxy() {
		List<NodeTemplate> serviceProxies = portMirroring.getServiceNodeTemplateBySdcType(SdcTypes.SERVICE_PROXY);
		assertNotNull(serviceProxies);
		assertEquals(serviceProxies.size(), 2);
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
           
	//region getVnfConfig
    @Test
    public void testGetVnfConfig() {
    	NodeTemplate vnfConfig = nfodCsarHlper.getVnfConfig("9bb2ef82-f8f6-4391-bc71-db063f15bf57");
    	assertNotNull(vnfConfig);
    	assertEquals("vnfConfiguration", vnfConfig.getMetaData().getValue("name"));
    }
    
    @Test
    public void testGetVnfConfigByNonFoundVNF() {
    	NodeTemplate vnfConfig = ipAssignCsarHelper.getVnfConfig("b5190df2-7880-4d6f-836f-56ab17e1b85b");
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
    //endregion

	//region nested vfc
	@Test
	public void testNestedVfcByExistCvfc() {
		List<NodeTemplate> vfcList = nestedVfcCsarHlper.getVfcListByVf("71389f8b-8671-4a43-a991-59fb621d3615");
		assertNotNull(vfcList);
		assertEquals(vfcList.size(), 2);
		assertEquals("VFC1 DUMMY", vfcList.get(0).getName());
		assertEquals("VF_VNF", vfcList.get(1).getName());
	}

	@Test
	public void testNestedVfcByNullVf() {
		List<NodeTemplate> vfcList = nestedVfcCsarHlper.getVfcListByVf(null);
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}

	@Test
	public void testNestedVfcByDummyVf() {
		List<NodeTemplate> vfcList = nestedVfcCsarHlper.getVfcListByVf("dummy");
		assertNotNull(vfcList);
		assertEquals(0, vfcList.size());
	}
	//endregion

	//region hasTopology
	@Test
	public void testHasTopologyByVF() {
		List<NodeTemplate> vfList = nestedVfcCsarHlper.getServiceVfList();
		boolean hasTopology = nestedVfcCsarHlper.hasTopology(vfList.get(0));
		assertEquals(true, hasTopology);
	}

	@Test
	public void testHasTopologyByCVFC() {
		List<NodeTemplate> vfcList = nestedVfcCsarHlper.getVfcListByVf("71389f8b-8671-4a43-a991-59fb621d3615");
		boolean hasTopology = nestedVfcCsarHlper.hasTopology(vfcList.get(1));
		assertEquals(true, hasTopology);
	}

	@Test
	public void testHasTopologyByVL() {
		List<NodeTemplate> serviceVlList = fdntCsarHelper.getServiceVlList();
		boolean hasTopology = fdntCsarHelper.hasTopology(serviceVlList.get(0));
		assertEquals(false, hasTopology);
	}

	@Test
	public void testHasTopologyByNull() {
		boolean hasTopology = fdntCsarHelper.hasTopology(null);
		assertEquals(false, hasTopology);
	}
	//endregion

	//region getNodeTemplateChildren
	@Test
	public void testGetNodeTemplatesListOfNodeTemplateByVF() {
		List<NodeTemplate> vfList = fdntCsarHelper.getServiceVfList();
		List<NodeTemplate> children = fdntCsarHelper.getNodeTemplateChildren(vfList.get(0));
		assertNotNull(children);
		assertEquals(3, children.size());

		children.sort(Comparator.comparing(NodeTemplate::getName));

		assertEquals("DNT_FW_RSG_SI_1", children.get(1).getName());
		assertEquals("VFC", children.get(1).getMetaData().getValue("type"));
		assertEquals("DNT_PORT", children.get(2).getName());
		assertEquals("CP", children.get(2).getMetaData().getValue("type"));
	}

	@Test
	public void testGetNodeTemplatesListOfNodeTemplateByVFC() {
		List<NodeTemplate> vfList = nestedVfcCsarHlper.getServiceVfList();
		List<NodeTemplate> vfChildren = nestedVfcCsarHlper.getNodeTemplateChildren(vfList.get(0));
		assertNotNull(vfChildren);
		assertEquals(vfChildren.size(), 2);
		vfChildren.sort(Comparator.comparing(NodeTemplate::getName));
		assertEquals("VFC1 DUMMY", vfChildren.get(0).getName());
		assertEquals("VF_VNF", vfChildren.get(1).getName());
		assertEquals("CVFC", vfChildren.get(1).getMetaData().getValue("type"));


		List<NodeTemplate> vfcChildren = nestedVfcCsarHlper.getNodeTemplateChildren(vfChildren.get(1));
		assertNotNull(vfcChildren);
		assertEquals(vfcChildren.size(), 3);
		vfcChildren.sort(Comparator.comparing(NodeTemplate::getName));
		assertEquals("Test NIC 02_wan_port", vfcChildren.get(0).getName());
		assertEquals("Test NIC_wan_port", vfcChildren.get(1).getName());
		assertEquals("VF", vfcChildren.get(2).getName());
	}

	@Test
	public void testGetNodeTemplatesListOfNodeTemplateByNull() {
		List<NodeTemplate> children = fdntCsarHelper.getNodeTemplateChildren(null);
		assertNotNull(children);
		assertEquals(0, children.size());
	}
	//endregion

    // added by QA
    // Get specific VNF properties
    @Test
    public void testGetVnfConfigGetProperties() {
    	NodeTemplate vnfConfig = nfodCsarHlper.getVnfConfig("9bb2ef82-f8f6-4391-bc71-db063f15bf57");
    	assertNotNull(vnfConfig);
    	assertEquals("vnfConfiguration", vnfConfig.getMetaData().getValue("name"));

    	String manufacturer_reference_number = nfodCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#ATT_part_12345_for_FortiGate-VM00#vendor_info#manufacturer_reference_number");
    	String num_cpus = nfodCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#ATT_part_67890_for_FortiGate-VM01#compute_flavor#num_cpus");
    	String sp_part_number = nfodCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#ATT_part_67890_for_FortiGate-VM01#sp_part_number");

    	assertEquals("FortiGate-VM00",manufacturer_reference_number);
    	assertEquals("10",num_cpus);
    	assertEquals("ATT_part_67890_for_FortiGate-VM01",sp_part_number);
    }

    // added by QA
    // Check that get vnfconfiguration not return as VFC
    @Test
    public void testGetVfcTypWithoutVnfCheckNames() {
    	List<NodeTemplate> vfcList = nfodCsarHlper.getVfcListByVf("9bb2ef82-f8f6-4391-bc71-db063f15bf57");
    	assertNotNull(vfcList);
    	assertEquals(2, vfcList.size());
    	for (int i = 0; i < vfcList.size(); i++) {

    		String Name= vfcList.get(i).getName();

    		assertEquals(false, Name.equals("vFW_VNF_Configuration"));

		}
    }

    @Test
    public void testNewGetVnfConfigGetProperties() {
    	NodeTemplate vnfConfig = nfodNEWCsarHlper.getVnfConfig("a6587663-b27f-4e88-8a86-604604302ce6");
    	assertNotNull(vnfConfig);
    	assertEquals("vnfConfiguration", vnfConfig.getMetaData().getValue("name"));

    	//Deployment flavor 1
    	String manufacturer_reference_number = nfodNEWCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#123456#vendor_info#manufacturer_reference_number");
    	String num_cpus = nfodNEWCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#123456#compute_flavor#num_cpus");
    	String sp_part_number = nfodNEWCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#123456#sp_part_number");

    	assertEquals("234567",manufacturer_reference_number);
    	assertEquals("2",num_cpus);
    	assertEquals("123456",sp_part_number);

    	//Deployment flavor 2
    	manufacturer_reference_number = nfodNEWCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#FG_partNumbereJqQjUkteF1#vendor_info#manufacturer_reference_number");
    	num_cpus = nfodNEWCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#FG_partNumbereJqQjUkteF1#compute_flavor#num_cpus");
    	sp_part_number = nfodNEWCsarHlper.getNodeTemplatePropertyLeafValue(vnfConfig, "allowed_flavors#FG_partNumbereJqQjUkteF1#sp_part_number");

    	assertEquals("EP_manufacturerReferenceNumberkbAiqZZNzx1",manufacturer_reference_number);
    	assertEquals("1",num_cpus);
    	assertEquals("FG_partNumbereJqQjUkteF1",sp_part_number);
    }

    // added by QA
    // Check that get vnfconfiguration not return as VFC
    @Test
    public void testNewGetVfcTypWithoutVnfCheckNames() {
    	List<NodeTemplate> vfcList = nfodNEWCsarHlper.getVfcListByVf("a6587663-b27f-4e88-8a86-604604302ce6");
    	assertNotNull(vfcList);
    	assertEquals(1, vfcList.size());
    	for (int i = 0; i < vfcList.size(); i++) {

    		String Name= vfcList.get(i).getName();

    		assertEquals(false, Name.equals("name_6GkVrOjnGp1_VNF_Configuration"));
		}
    }

    //region getServiceNodeTemplateByNodeName
	@Test
	public void testGetServiceNodeTemplateByRealNodeName() {
		NodeTemplate nodeTemplate = fdntCsarHelper.getServiceNodeTemplateByNodeName("FDNT 1");
		assertNotNull(nodeTemplate);
		assertEquals(nodeTemplate.getName(), "FDNT 1");
		assertEquals(nodeTemplate.getMetaData().getValue("type"), "VF");
	}

	@Test
	public void testGetServiceNodeTemplateByNullNodeName() {
		NodeTemplate nodeTemplate = fdntCsarHelper.getServiceNodeTemplateByNodeName(null);
		assertNull(nodeTemplate);
	}

	@Test
	public void testGetServiceNodeTemplateByDummyNodeName() {
		NodeTemplate nodeTemplate = fdntCsarHelper.getServiceNodeTemplateByNodeName("dummy");
		assertNull(nodeTemplate);
	}	
	


	//endregion
	//region resolve get_input
	@SuppressWarnings("rawtypes")
	@Test
	public void testResolveGetInputForComplexTypeAndList() {
		//port_pd01_port_ip_requirements is of type list<org.openecomp.datatypes.network.IpRequirements>
		//This test covers:
		// 1) "default" resolving
		// 2) complex type resolving
		// 3) List access resolving
		List<NodeTemplate> vfcs = resolveGetInputCsar.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "port_pd01_port_ip_requirements");
		assertTrue(propertyAsObject instanceof ArrayList);
		assertEquals(2, ((ArrayList) propertyAsObject).size());
		//port_pd01_port_ip_requirements:
		//- get_input: [ip_requirements, 0]
		//- get_input: [ip_requirements, 1]
		assertEquals("subnet_role_4", ((Map) ((ArrayList) propertyAsObject).get(0)).get("subnet_role"));
		assertEquals("subnet_role_6", ((Map) ((ArrayList) propertyAsObject).get(1)).get("subnet_role"));
	}

	@Test
	public void testResolveGetInputForListOnNetworkCloud() {
		final String related_network_role = "related_network_role";
		List<NodeTemplate> serviceVfList = csarHelperServiceNetworkCloud.getServiceVfList();
		assertNotNull(serviceVfList);
		assertFalse(serviceVfList.isEmpty());
		List<NodeTemplate> vfcListByVf = csarHelperServiceNetworkCloud.getVfcListByVf(csarHelperServiceNetworkCloud.getNodeTemplateCustomizationUuid(serviceVfList.get(0)));
		assertNotNull(vfcListByVf);
		assertFalse(vfcListByVf.isEmpty());
		List<NodeTemplate> cpList = csarHelperServiceNetworkCloud.getNodeTemplateChildren(vfcListByVf.get(0));
		assertNotNull(cpList);
		assertFalse(cpList.isEmpty());
		final Object propertyValue = cpList.get(0).getPropertyValue("related_networks");
		assertNotNull(propertyValue);
		ArrayList<Map<String, String>> relatedNetworkList = (ArrayList)propertyValue;
		assertEquals(3, relatedNetworkList.size());
		assertEquals("cor_direct_2", relatedNetworkList.get(0).get(related_network_role));
		assertEquals("sgi_direct_2", relatedNetworkList.get(1).get(related_network_role));
		assertEquals("int_imbl_2", relatedNetworkList.get(2).get(related_network_role));
	}

	@Test
	public void testResolveGetInputForPrimitiveTypeString() {
		//This test covers "default" resolving of primitive - as Object
		List<NodeTemplate> vfcs = resolveGetInputCsar.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "port_pd01_port_network_role_tag");
		assertEquals("oam", propertyAsObject);
	}

	@Test
	public void testResolveGetInputForPrimitiveTypeInteger() {
		//This test covers "default" resolving of primitive - as String
		List<NodeTemplate> vfcs = resolveGetInputCsar.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		String propertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyLeafValue(vfcs.get(0), "port_pd01_port_order");
		assertEquals("1", propertyAsObject);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testResolveGetInputForMap() {
		//This test covers "default" resolving of primitive - as Map
		List<NodeTemplate> vfcs = resolveGetInputCsar.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "port_pd02_port_ip_requirements#ip_count_required");
		assertTrue(propertyAsObject instanceof Map);
		assertEquals(false, ((Map)propertyAsObject).get("is_required"));
	}

	@Test
	public void testResolveGetInputForAllHierarchy() {
		//This test covers "default" resolving from service level
		List<NodeTemplate> vfs = resolveGetInputCsar.getServiceVfList();
		Object vfPropertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyValueAsObject(vfs.get(0), "vm_count");
		assertEquals(2, vfPropertyAsObject);
		//This test covers property assignment resolving on VFI level (service template), from Vf level
		List<NodeTemplate> vfcs = resolveGetInputCsar.getNodeTemplateBySdcType(vfs.get(0), SdcTypes.VFC);
		Object vfcPropertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "my_count");
		assertEquals(2, vfcPropertyAsObject); //takes it from upper level (VF) property
	}

	@Test
	public void testResolveGetInputNoDefValueInnerLevel() {
		//This test covers resolving when no "default" value is supplied to the input - should be null - VF/VFCI level
		List<NodeTemplate> vfcs = resolveGetInputCsar.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		String propertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyLeafValue(vfcs.get(0), "index_value");
		assertNull(propertyAsObject);

	}

	@Test
	public void testResolveGetInputNoDefValueServiceLevel() {
		//This test covers resolving when no "default" value is supplied to the input - should be null - Service/VFI level
		List<NodeTemplate> vfs = resolveGetInputCsar.getServiceVfList();
		Object vfPropertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyValueAsObject(vfs.get(0), "port_order");
		assertNull(vfPropertyAsObject);

	}
	//endregion

	// region Added by QA - Continue with testings of resolve get_input
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testResolveGetInputForComplexTypeAndListWithFalseValue() 
	{
		List<NodeTemplate> vfcs = resolveGetInputCsarFalse.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsarFalse.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "port_pd01_port_ip_requirements");
		assertTrue(propertyAsObject instanceof ArrayList);
		assertEquals(2, ((ArrayList) propertyAsObject).size());
		assertEquals("get_input:[ip_requirements, 0]", ((ArrayList) propertyAsObject).get(0).toString());
		assertEquals("get_input:[ip_requirements, 1]", ((ArrayList) propertyAsObject).get(1).toString());
	}
	
	@Test
	public void testResolveGetInputForPrimitiveTypeStringWithFalseValue() {
		List<NodeTemplate> vfcs = resolveGetInputCsarFalse.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsarFalse.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "port_pd01_port_network_role_tag");
		assertEquals("get_input:role_tag_oam", propertyAsObject.toString());
	}
	
	@Test
	public void testResolveGetInputForPrimitiveTypeListWithFalseValue() {
		List<NodeTemplate> vfcs = resolveGetInputCsarFalse.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsarFalse.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "compute_pd_server_name");
		assertEquals("[get_input:[pd_server_names, 0]]", propertyAsObject.toString());
	}
	
	//@Test // Maybe a bug here.... need to check with Esti - Mait was sent. 
	public void testResolveGetInputForPrimitiveTypeList() {
		List<NodeTemplate> vfcs = resolveGetInputCsar.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsar.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "compute_pd_server_name");
		assertEquals("\"ZRDM1MOGX01MPD001\"", propertyAsObject.toString());				
	}
	
	@Test
        public void testResolveGetInputForPrimitiveNullValue() {
		List<NodeTemplate> vfcs = resolveGetInputCsarQA.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		@SuppressWarnings("unchecked")
		List<String>propertyAsObject = (List<String>) resolveGetInputCsarQA.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "compute_pd_server_availability_zone");
		assertNull(propertyAsObject.get(0));
		}
	@Test
	public void testResolveGetInputForPrimitiveIPValue() {
		List<NodeTemplate> vfcs = resolveGetInputCsarQA.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsarQA.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "vm_image_name");
		assertEquals("107.239.36.5", propertyAsObject.toString());
	}
	
	//QA region getServiceNodeTemplateByNodeName tests
	
	@Test
	public void getServiceNodeTemplateByNodeNameTypeVF() {
		NodeTemplate nodeTemplate = QAServiceForToscaParserTests.getServiceNodeTemplateByNodeName("VF_1_V_port_1 0");
		assertNotNull(nodeTemplate);
		assertEquals(nodeTemplate.getName(), "VF_1_V_port_1 0");
		assertEquals(nodeTemplate.getMetaData().getValue("type"), "VF");
	}
	
	@Test
	public void getServiceNodeTemplateByNodeNameTypeVL() {
		NodeTemplate nodeTemplate = QAServiceForToscaParserTests.getServiceNodeTemplateByNodeName("ExtVL 0");
		assertNotNull(nodeTemplate);
		assertEquals(nodeTemplate.getName(), "ExtVL 0");
		assertEquals(nodeTemplate.getMetaData().getValue("type"), "VL");
	}
	
	@Test
	public void getServiceNodeTemplateByNodeNameTypeCP() {
		NodeTemplate nodeTemplate = QAServiceForToscaParserTests.getServiceNodeTemplateByNodeName("ExtCP 0");
		assertNotNull(nodeTemplate);
		assertEquals(nodeTemplate.getName(), "ExtCP 0");
		assertEquals(nodeTemplate.getMetaData().getValue("type"), "CP");
	}
	
	@Test
	public void getServiceNodeTemplateByNodeNameTypePNF() {
		NodeTemplate nodeTemplate = QAServiceForToscaParserTests.getServiceNodeTemplateByNodeName("PNF TEST 0");
		assertNotNull(nodeTemplate);
		assertEquals(nodeTemplate.getName(), "PNF TEST 0");
		assertEquals(nodeTemplate.getMetaData().getValue("type"), "PNF");
	}
	
	//QA region  getServiceNodeTemplateBySdcType tests 
	
	@Test
	public void getServiceNodeTemplateBySdcType_VF() {
		List<NodeTemplate> vfList = QAServiceForToscaParserTests.getServiceNodeTemplatesByType("org.openecomp.resource.vf.Vf1VPort1");
		assertEquals(2, vfList.size());
		assertEquals("VF_1_V_port_1", vfList.get(0).getMetaData().getValue("name"));
	}
	// endregion Added by QA - Continue with testings of resolve get_input


	@SuppressWarnings("rawtypes")
	@Test
	public void testResolveGetInputArrayStructure() {
		List<NodeTemplate> vfcs = resolveGetInputCsarQA.getVfcListByVf("b5190df2-7880-4d6f-836f-56ab17e1b85b");
		Object propertyAsObject = resolveGetInputCsarQA.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "compute_pd_server_name");
		assertEquals( ((ArrayList)propertyAsObject).get(0).toString(), "\"ZRDM1MOGX01MPD001\"");
		propertyAsObject = resolveGetInputCsarQA.getNodeTemplatePropertyValueAsObject(vfcs.get(0), "port_pd01_port_ip_requirements");
		assertEquals(2, ((ArrayList)propertyAsObject).size());
	}

	@Test
	public void testServiceNodeTemplateByCRType() {
		List<NodeTemplate> serviceCRList = csarHelperServiceWithCrs.getServiceNodeTemplateBySdcType(SdcTypes.CR);
		assertNotNull(serviceCRList);
		assertEquals(serviceCRList.size(), 2);
		assertEquals(serviceCRList.get(0).getName(), "chaya best cr 1");
		assertEquals(serviceCRList.get(0).getType(), "org.openecomp.resource.cr.ChayaBestCr");
		assertEquals(serviceCRList.get(1).getName(), "chaya best cr 0");
		assertEquals(serviceCRList.get(1).getType(), "org.openecomp.resource.cr.ChayaBestCr");
	}

	@Test
	public void testGetCPOfCRNodeTemplate() {
		NodeTemplate nodeTemplate = csarHelperServiceWithCrs.getServiceNodeTemplateByNodeName("chaya best cr 0");
		List<NodeTemplate> crCpChildren = csarHelperServiceWithCrs.getNodeTemplateBySdcType(nodeTemplate, SdcTypes.CP);
		assertEquals(crCpChildren.get(0).getName(), "ContrailPort 0");
		assertEquals(crCpChildren.get(0).getMetaData().getValue("type"), SdcTypes.CP.name());
	}

	@Test
	public void testServiceCRInstanceProps() {
		List<NodeTemplate> serviceCrList = csarHelperServiceWithCrs.getServiceNodeTemplateBySdcType(SdcTypes.CR);
		assertNotNull(serviceCrList);
		NodeTemplate crTemplate = serviceCrList.get(0);
		assertNotNull(crTemplate);
		assertEquals(crTemplate.getPropertyValue("nf_naming").toString(), ImmutableMap.of("ecomp_generated_naming", "true").toString());
		assertEquals(crTemplate.getPropertyValue("contrailport0_virtual_network"), "chaya");
	}
	
	@Test
	public void testGetPoliciesOfOriginOfNodeTemplate() {
		NodeTemplate nt0 = csarHelperServicePolicy.getNodeTemplateByName("al_vf 0");
		NodeTemplate nt1 = csarHelperServicePolicy.getNodeTemplateByName("al_vf 1");
		List<Policy> policies = csarHelperServicePolicy.getPoliciesOfOriginOfNodeTemplate(nt0);
		assertNotNull(policies);
		assertEquals(policies.size(), 3);
		policies = csarHelperServicePolicy.getPoliciesOfOriginOfNodeTemplate(nt1);
		assertNotNull(policies);
		assertEquals(policies.size(), 3);
	}
	
	@Test
	public void testGetPoliciesOfOriginOfNodeTemplateByToscaPolicyType() {
		NodeTemplate nt0 = csarHelperServicePolicy.getNodeTemplateByName("al_vf 0");
		NodeTemplate nt1 = csarHelperServicePolicy.getNodeTemplateByName("al_vf 1");
		List<Policy> policies = csarHelperServicePolicy.getPoliciesOfOriginOfNodeTemplateByToscaPolicyType(nt0, "org.openecomp.policies.placement.Colocate");
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
		
		policies = csarHelperServicePolicy.getPoliciesOfOriginOfNodeTemplateByToscaPolicyType(nt0, "org.openecomp.policies.placement.Antilocate");
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
		
		policies = csarHelperServicePolicy.getPoliciesOfOriginOfNodeTemplateByToscaPolicyType(nt0, "org.openecomp.policies.placement.valet.Diversity");
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
		
		policies = csarHelperServicePolicy.getPoliciesOfOriginOfNodeTemplateByToscaPolicyType(nt1, "org.openecomp.policies.placement.Colocate");
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
		
		policies = csarHelperServicePolicy.getPoliciesOfOriginOfNodeTemplateByToscaPolicyType(nt1, "org.openecomp.policies.placement.Antilocate");
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
		
		policies = csarHelperServicePolicy.getPoliciesOfOriginOfNodeTemplateByToscaPolicyType(nt1, "org.openecomp.policies.placement.valet.Diversity");
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
	}
	
	@Test
	public void testGetPolicyTargetNodeTemplatesFromOrigin() {
		List<NodeTemplate> nodeTemplates = csarHelperServicePolicy.getPolicyTargetsFromOrigin(csarHelperServicePolicy.getNodeTemplateByName("al_vf 1"),"policy..Colocate..0");
		assertNotNull(nodeTemplates);
		assertEquals(nodeTemplates.size(), 2);
	}
	
	@Test
	public void testGetPoliciesOfNodeTemplate() {
		NodeTemplate nt0 = csarHelperVfPolicy.getNodeTemplateByName("al_vfc 1");
		List<Policy> policies = csarHelperVfPolicy.getPoliciesOfTarget(nt0);
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
	}
	
	@Test
	public void testGetPoliciesOfNodeTemplateByToscaPolicyType() {
		NodeTemplate nt0 = csarHelperVfPolicy.getNodeTemplateByName("al_vfc 1");
		List<Policy> policies = csarHelperVfPolicy.getPoliciesOfTargetByToscaPolicyType(nt0, "org.openecomp.policies.placement.Colocate");
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
	}
	
	@Test
	public void testGetPoliciesOfTopologyTemplate() {
		List<Policy> policies = csarHelperVfPolicy.getPoliciesOfTopologyTemplate();
		assertNotNull(policies);
		assertEquals(policies.size(), 1);
	}
	
	@Test
	public void testGetPolicyTargetNodeTemplates() {
		List<NodeTemplate> nodeTemplates = csarHelperVfPolicy.getPolicyTargetsFromTopologyTemplate("policy..Colocate..0");
		assertNotNull(nodeTemplates);
		assertEquals(nodeTemplates.size(), 2);
	}
	
	@Test
	public void testGetGroups() {
		NodeTemplate groupsVf = csarHelperServiceGroups.getNodeTemplateByName("GroupsVf 0");
		NodeTemplate vlanGroups = csarHelperServiceGroups.getNodeTemplateByName("VlanGroups 0");
		
		ArrayList<Group> groups = csarHelperServiceGroups.getGroupsOfOriginOfNodeTemplate(groupsVf);
		assertNotNull(groups);
		assertEquals(groups.size(), 5);
		
		groups = csarHelperServiceGroups.getGroupsOfOriginOfNodeTemplate(vlanGroups);
		assertNotNull(groups);
		assertEquals(groups.size(), 4);
		
		groups = csarHelperServiceGroups.getGroupsOfOriginOfNodeTemplateByToscaGroupType(groupsVf, "org.openecomp.groups.VfcInstanceGroup");
		assertNotNull(groups);
		assertEquals(groups.size(), 1);
		
		groups = csarHelperServiceGroups.getGroupsOfOriginOfNodeTemplateByToscaGroupType(vlanGroups, "org.openecomp.groups.VfcInstanceGroup");
		assertNotNull(groups);
		assertEquals(groups.size(), 2);
		
		List<NodeTemplate> members = csarHelperServiceGroups.getGroupMembersOfOriginOfNodeTemplate(groupsVf, "x_group");
		
		assertNotNull(members);
		assertEquals(members.size(), 3);
		Optional<NodeTemplate> memberOpt = (members.stream().filter(m -> m.getName().equals("lb_1"))).findFirst();
		assertTrue(memberOpt.isPresent());
		memberOpt = (members.stream().filter(m -> m.getName().equals("lb_2"))).findFirst();
		assertTrue(memberOpt.isPresent());
		memberOpt = (members.stream().filter(m -> m.getName().equals("mg_4"))).findFirst();
		assertTrue(memberOpt.isPresent());
		
		members = csarHelperServiceGroups.getGroupMembersOfOriginOfNodeTemplate(vlanGroups, "oam_group");
		assertNotNull(members);
		assertEquals(members.size(), 1);
		memberOpt = (members.stream().filter(m -> m.getName().equals("abstract_vdbe_1"))).findFirst();
		assertTrue(memberOpt.isPresent());
		
		members = csarHelperServiceGroups.getGroupMembersOfOriginOfNodeTemplate(vlanGroups, "untr_group");
		assertNotNull(members);
		assertEquals(members.size(), 1);
		memberOpt = (members.stream().filter(m -> m.getName().equals("abstract_vdbe"))).findFirst();
		assertTrue(memberOpt.isPresent());
	}
	
	@Test
	public void testGetGroupsInputsProperties() {
		NodeTemplate vdbe0 = csarHelperServiceGroupsInputs.getNodeTemplateByName("vDBE 0");
		ArrayList<Group> groups = csarHelperServiceGroupsInputs.getGroupsOfOriginOfNodeTemplate(vdbe0);
		assertNotNull(groups);
		assertEquals(groups.size(), 4);
		
		Optional<Group> groupOpt = (groups.stream().filter(g -> g.getName().equals("oam_group"))).findFirst();
		assertTrue(groupOpt.isPresent());
		Group group = groupOpt.get();
		validateInputsProperties(vdbe0, group);
		
		groupOpt = (groups.stream().filter(g -> g.getName().equals("untr_group"))).findFirst();
		assertTrue(groupOpt.isPresent());
		group = groupOpt.get();
		validateInputsProperties(vdbe0, group);
	}
	
	@Test
	public void testGetGroupsInputsCapabilities() {
		NodeTemplate vdbe = csarHelperServiceGroupsCapabilities.getNodeTemplateByName("vdbe_srv_proxy 0");
		CapabilityAssignments capabilities = csarHelperServiceGroupsCapabilities.getCapabilitiesOf(vdbe);
		CapabilityAssignment capability = capabilities.getCapabilityByName("vdbe0.oam_group.vlan_assignment");
		assertNotNull(capability);
	}
	
	@Test
	public void testGetVfGroupsPolicies() {
		List<Policy> policies = csarHelperVfGroupsPolicies.getPoliciesOfTopologyTemplate();
		assertNotNull(policies);
		List<Group> groups = csarHelperVfGroupsPolicies.getGroupsOfTopologyTemplate();
		assertNotNull(groups);
	}
	
	@Test
	public void testGetServiceGroupsPolicies() {
		NodeTemplate nt = csarHelperServiceGroupsPolicies.getNodeTemplateByName("vDBE 0");
		List<Policy> policies = csarHelperServiceGroupsPolicies.getPoliciesOfOriginOfNodeTemplate(nt);
		assertNotNull(policies);
		List<Group> groups = csarHelperServiceGroupsPolicies.getGroupsOfOriginOfNodeTemplate(nt);
		assertNotNull(groups);
	}
	
	@Test
	public void testGetResourceInputsWithAnnotations() {
		List<Input> inputs = csarHelperServiceAnnotations.getServiceInputs();
		assertNotNull(inputs);
		assertEquals(inputs.size(), 19);
		assertTrue(inputs.stream().filter(i -> i.getAnnotations() != null).collect(Collectors.toList()).isEmpty());
		
		inputs = csarHelperServiceAnnotations.getInputsWithAnnotations();
		assertNotNull(inputs);
		assertEquals(inputs.size(), 19);
		validateInputsAnnotations(inputs);
	}
	
	@Test
	public void testGetServiceInputsWithAnnotations() {
		List<Input> inputs = csarHelperServiceAdiodAnnotations.getServiceInputs();
		assertNotNull(inputs);
		assertTrue(inputs.stream().filter(i -> i.getAnnotations() != null).collect(Collectors.toList()).isEmpty());
		
		inputs = csarHelperServiceAdiodAnnotations.getInputsWithAnnotations();
		assertNotNull(inputs);
		validateInputsAnnotations(inputs);
	}

	@Test
	public void getPropertyValueByNamePathAndNodeTypePathWhenPropertyValueIncludesGetInputAndPropertyHasDataType() {
		List<String> propertyValues = csarHelperServiceGroupsInputs.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath("exCP_naming",
				"vDBE 0#abstract_vdbe_1#vdbe_vdbe_untr_port");
		assertEquals(0, propertyValues.size());
	}

	@Test
	public void getPropertyValueByNamePathAndNodeTypePathWhenPropertyPathIsComplex() {
		List<String> propertyValues = csarHelperServiceGroupsInputs.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath("exCP_naming#ecomp_generated_naming",
				"vDBE 0#abstract_vdbe_1#vdbe_vdbe_untr_port");
		assertEquals(1, propertyValues.size());
		assertEquals("true", propertyValues.get(0));
	}

	@Test
	public void getPropertyValueByNamePathAndNodeTypePathWhenNoValueFoundForGetInputProperty() {
		List<String> propertyValues = csarHelperServiceGroupsInputs.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath("virtual_machine_interface_allowed_address_pairs#allowed_address_pair#address_mode",
				"vDBE 0#abstract_vdbe_1#vdbe_vdbe_untr_port_nested2#vdbe_0_subint_oam_vmi_0");
		assertEquals(0, propertyValues.size());
	}

	@Test
	public void getPropertyValueByNamePathAndNodeTemplatePathSuccess() {
		List<String> propertyValues = csarHelperServiceGroupsInputs.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath("is_default",
				"vDBE 0#abstract_vdbe_1#vdbe_vdbe_untr_port_nested2#vdbe_0_subint_oam_vmi_0");
		assertEquals(1, propertyValues.size());
		assertEquals("false", propertyValues.get(0));
	}

    @Test
    public void getPropertyValueByNamePathAndEmptyNodeTypePath() {
        List<String> propertyValues = csarHelperServiceGroupsInputs.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath(null, "");
		assertEquals(0, propertyValues.size());
	}

    @Test
    public void getPropertyValueByNamePathAndNullNodeTypePath() {
		List<String> propertyValues = csarHelperServiceGroupsInputs.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath(null, null);
		assertTrue(propertyValues.isEmpty());
    }

	@Test
	public void getPropertyValueByNamePathAndNodeTypePathForListOfGetInputs() {
		List<String> propertyValues = resolveGetInputCsar.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath("port_pd01_port_ip_requirements#subnet_role",
				"ipAssign4 0#abstract_pd_server");
		assertEquals(2, propertyValues.size());
		assertTrue(propertyValues.get(0).equals("subnet_role_4") || propertyValues.get(0).equals("subnet_role_6"));
        assertTrue(propertyValues.get(1).equals("subnet_role_4") || propertyValues.get(1).equals("subnet_role_6"));
	}

	@Test
    public void getPropertyValueByNamePathAndNodeTypePathForNetworkCloud() {
        List<String> propertyValues = csarHelperServiceNetworkCloud.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath("related_networks#related_network_role",
                "Network Cloud VNF MOCK 0#abstract_testVM#testVM_testVM_SRIOVtrunk1_port");
        assertEquals(3, propertyValues.size());
        assertTrue(propertyValues.contains("cor_direct_2"));
        assertTrue(propertyValues.contains("sgi_direct_2"));
        assertTrue(propertyValues.contains("int_imbl_2"));
    }

    @Test
    public void getPropertyValueByNamePathAndSingleNodeTypeNameForNetworkCloud() {
        List<String> propertyValues = csarHelperServiceNetworkCloud.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath("nf_naming#ecomp_generated_naming",
                "Network Cloud VNF MOCK 0");
        assertEquals(1, propertyValues.size());
        assertEquals("true", propertyValues.get(0));
    }

    @Test
    //todo test for port_order on resolveGetInputCsar
    public void getPropertyValueByNamePathAndNodeTypePathWithGetInputNotSet() {
		List<String> propertyValues = csarHelperServiceNetworkCloud.getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath(
				"name", "Network Cloud VNF MOCK 0#abstract_testVM#testVM");
        assertTrue(propertyValues.isEmpty());
	}


	@SuppressWarnings("unchecked")
	private void validateInputsProperties(NodeTemplate vdbe0, Group group) {
		assertNotNull(group.getPropertiesObjects());
		ArrayList<Property> properties = group.getPropertiesObjects();
		
		List<String> inputs = properties.stream()
		.filter(p -> p.getValue() instanceof Map)
		.map(p -> ((Map<String, String>)p.getValue()).get("get_input"))
		.collect(Collectors.toList());
		
		assertEquals(inputs.size(), 2);
		
		inputs.forEach(i -> assertTrue(vdbe0.getProperties().containsKey(i)));
		
		 List<Object> list = vdbe0.getProperties().entrySet().stream()
				 .filter(e -> inputs.contains(e.getKey()))
				 .map(e -> e.getValue().getValue())
				 .collect(Collectors.toList());
		 assertEquals(list.size(), 2);
	}
	
	private void validateInputsAnnotations(List<Input> inputs) {
		List<Input> inputsWithAnnotations = inputs.stream().filter(i -> i.getAnnotations() != null).collect(Collectors.toList());
		assertTrue(!inputs.isEmpty());
		inputsWithAnnotations.stream().forEach(i -> validateAnnotations(i));
	}
	
	private void validateAnnotations(Input input) {
		assertNotNull(input.getAnnotations());
		assertEquals(input.getAnnotations().size(), 1);
		Annotation annotation = input.getAnnotations().get("source");
		assertEquals(annotation.getName(), "source");
		assertEquals(annotation.getType().toLowerCase(), "org.openecomp.annotations.source");
		assertNotNull(annotation.getProperties());
		Optional<Property> source_type = annotation.getProperties()
				.stream()
				.filter(p->p.getName().equals("source_type"))
				.findFirst();
		assertTrue(source_type.isPresent());
		assertEquals(source_type.get().getValue(), "HEAT");
	}

}



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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.RequirementAssignment;

@ExtendWith({SdcToscaParserBasicTest.class})
 class ToscaParserSubsMappingsTest extends SdcToscaParserBasicTest {

    //region getServiceSubstitutionMappingsTypeName
    @Test
    public void testGetServiceSubstitutionMappingsTypeName() {
        String serviceSubstitutionMappingsTypeName = fdntCsarHelper.getServiceSubstitutionMappingsTypeName();
        assertEquals("org.openecomp.service.ServiceFdnt", serviceSubstitutionMappingsTypeName);
    }

    @Test
    public void testServiceSubstitutionMappingsTypeName() {
        String substitutionMappingsTypeName = rainyCsarHelperMultiVfs.getServiceSubstitutionMappingsTypeName();
        assertNull(substitutionMappingsTypeName);
    }
    //endregion

    //Added by QA - Check for Capabilities in VF level (Capabilities QTY and Names).
    //@Test // - BUG 283369
//		public void testCapabilitiesofVFNames_QTY() throws SdcToscaParserException {
//			List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
//			String sName = serviceVfList.get(0).getName();
//			assertEquals(sName,fdntCsarHelper_Data.get("FDNT").get("VF Name").get(0));
//			Map<String, CapabilityAssignment> lCapabilitys = serviceVfList.get(0).getCapabilities().getAll();
//			List<String> CPkeys = new ArrayList<>(lCapabilitys.keySet());
//			List<String> CapabilitiesNames = new ArrayList<String>(CPkeys.size());
//
//			for (int i = 0; i < CPkeys.size(); i++) {
//
//				CapabilityAssignment cCp = lCapabilitys.get(CPkeys.get(i));
//
//				CapabilitiesNames.add(cCp.getName());
//
//				assertEquals(CPkeys.get(i).toLowerCase(), CapabilitiesNames.get(i).toLowerCase());// Compare keys to values, Should it be checked as Case sensitive????
//
//				//System.out.println(String.format("Value of key: %s , Value of capability: %s", keys.get(i).toLowerCase(), Capabilities.get(i).toLowerCase()));
//				//System.out.println(String.format("Value of key: %s , Value of capability: %s", ActualValues.get(i).toLowerCase(), Capabilities.get(i).toLowerCase()));
//				//System.out.println(String.format("*******%d*******",i));
//			}
//
//			for (int i = 0; i < CPkeys.size(); i++) {
//				assertEquals(true, CapabilitiesNames.stream().map(String::toLowerCase).collect(Collectors.toList()).contains(fdntCsarHelper_Data.get("FDNT").get("capabilities").get(i).toLowerCase())); // Compare capabilities predefined list to actual one.
//			}
//
//			assertEquals(fdntCsarHelper_Data.get("FDNT").get("capabilities").size(), CapabilitiesNames.size()); // Compare capabilities qty expected vs actual
//		}

    //Added by QA - Check for Capabilities in VF level (Capabilities Types and Properties).
    //@Test
//		public void testCapabilitiesofVFTypes_Properties() throws SdcToscaParserException {
//			List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
//			String sName = serviceVfList.get(0).getName();
//			assertEquals(sName,fdntCsarHelper_Data.get("FDNT").get("VF Name").get(0));
//			Map<String, CapabilityAssignment> lCapabilitys = serviceVfList.get(0).getCapabilities().getAll();
//
//			List<String> CPkeys = new ArrayList<>(lCapabilitys.keySet());
//			List<String> CPPropkeys = new ArrayList<>(lCapabilitys.keySet());
//			List<String> CapabilitiesTypes = new ArrayList<String>(CPkeys.size());
//
//			//int iKeysSize = keys.size(); //for debug
//
//			for (int i = 0; i < CPkeys.size(); i++) {
//
//				CapabilityAssignment cCp = lCapabilitys.get(CPkeys.get(i));
//				CapabilityTypeDef CpDef = cCp.getDefinition();
//				CapabilitiesTypes.add(CpDef.getEntityType());
//
//				//LinkedHashMap<String,Object> lProperties = cCp.getDefinition().getProperties();
//				LinkedHashMap<String, Property> lPropertiesR = cCp.getProperties();
//
//	  			List<String> CP_Propkeys = new ArrayList<>(lPropertiesR.keySet());
//
//	  			for (int j = 0; j < CP_Propkeys.size(); j++) {
//
//	  			Property p = lPropertiesR.get(CP_Propkeys.get(j));
//
//				if(p !=  null){
//	  				String sPType = p.getEntityType();
//	  				Boolean bPRequired = p.isRequired();
//
//	  				System.out.println(sPType + "  " + bPRequired);
//
//					}
//
//			}
//
//			}
//
//			for (int i = 0; i < CPkeys.size(); i++) {
//
//			}
//
//			assertEquals(fdntCsarHelper_Data.get("FDNT").get("capabilitiesTypes").size(), CapabilitiesTypes.size()); // Compare capabilities qty expected vs actual
//		}

    //@Test // - BUG 283387
    public void testRequirmentsofVF() throws SdcToscaParserException {
        List<NodeTemplate> serviceVfList = fdntCsarHelper.getServiceVfList();
        String sName = serviceVfList.get(0).getName();
        assertEquals(sName, "FDNT 1");

        List<String> ActualReqsValues = new ArrayList<>(Arrays.asList());

        List<RequirementAssignment> lRequirements = serviceVfList.get(0).getRequirements().getAll();

        assertEquals(fdntCsarHelper_Data.get("FDNT").get("requirements").size(), lRequirements.size()); //

        // Continue from here after bug is fixed ! ! ! !  - Test the Requirements values
    }

}

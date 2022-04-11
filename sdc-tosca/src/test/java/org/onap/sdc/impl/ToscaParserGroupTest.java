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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import java.util.List;

@ExtendWith({SdcToscaParserBasicTest.class})
 class ToscaParserGroupTest extends SdcToscaParserBasicTest{

    //region getVfModulesByVf
    @Test
    public void testVfModulesFromVf(){
        List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
        assertEquals(2, vfModulesByVf.size());
        for (Group group : vfModulesByVf){
            assertTrue(group.getName().startsWith("fdnt1"));
            assertNotNull(group.getMetadata());
            assertNotNull(group.getMetadata().getValue("vfModuleModelCustomizationUUID"));
        }
    }

    @Test
    public void testGetGroupMetadata(){
        List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
        boolean found = false;
        for (Group group : vfModulesByVf){
            if (group.getName().equals("fdnt1..Fdnt..base_stsi_dnt_frwl..module-0")){
                found = true;
                Metadata metadata = group.getMetadata();
                assertNotNull(metadata);
                assertEquals("b458f4ef-ede2-403d-9605-d08c9398b6ee", metadata.getValue("vfModuleModelCustomizationUUID"));
            }
        }
        assertTrue(found);
    }

    @Test
    public void testGetGroupEmptyMetadata(){
        List<Group> vfModulesByVf = rainyCsarHelperMultiVfs.getVfModulesByVf("56179cd8-de4a-4c38-919b-bbc4452d2d72");
        boolean found = false;
        for (Group group : vfModulesByVf){
            if (group.getName().equals("fdnt1..Fdnt..base_stsi_dnt_frwl..module-0")){
                found = true;
                Metadata metadata = group.getMetadata();
                assertNull(metadata);
            }
        }
        assertTrue(found);
    }

    @Test
    public void testGetVfModuleNonExisitingVf() {
        List<Group> vfModulesByVf = rainyCsarHelperSingleVf.getVfModulesByVf("dummy");
        assertNotNull(vfModulesByVf);
        assertEquals(0, vfModulesByVf.size());
    }

    @Test
    public void testGetVfModuleNullVf() {
        List<Group> vfModulesByVf = rainyCsarHelperSingleVf.getVfModulesByVf(null);
        assertNotNull(vfModulesByVf);
        assertEquals(0, vfModulesByVf.size());
    }
    //endregion

    //region getGroupPropertyLeafValue
    @Test
    public void testGroupFlatProperty() throws SdcToscaParserException {
        List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
        String volumeGroup = fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), "volume_group");
        assertEquals("false", volumeGroup);
    }

//    @Test
//    public void testGroupFlatGetInputProperty() throws SdcToscaParserException {
//        List<Group> vfModulesByVf = fdntCsarHelperWithInputs.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
//        String volumeGroup = fdntCsarHelperWithInputs.getGroupPropertyLeafValue(vfModulesByVf.get(1), "volume_group");
//        assertEquals("false", volumeGroup);
//    }

    @Test
    public void testGroupPropertyLeafValueByNullProperty() {
        List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
        String groupProperty = fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), null);
        assertNull(groupProperty);
    }

    @Test
    public void testGroupPropertyLeafValueByDummyProperty() {
        List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
        String groupProperty = fdntCsarHelper.getGroupPropertyLeafValue(vfModulesByVf.get(0), "XXX");
        assertNull(groupProperty);
    }

    @Test
    public void testGroupPropertyLeafValueByNullGroup() {
        String groupProperty = fdntCsarHelper.getGroupPropertyLeafValue(null, "volume_group");
        assertNull(groupProperty);
    }
    //endregion

    //region getGroupPropertyAsObject
    @Test
    public void testGetGroupPropertyAsObject() {
        List<Group> vfModulesByVf = fdntCsarHelper.getVfModulesByVf(VF_CUSTOMIZATION_UUID);
        Object volumeGroup = fdntCsarHelper.getGroupPropertyAsObject(vfModulesByVf.get(0), "volume_group");
        assertEquals(false, volumeGroup);
    }
    //getGroupPropertyAsObject

}

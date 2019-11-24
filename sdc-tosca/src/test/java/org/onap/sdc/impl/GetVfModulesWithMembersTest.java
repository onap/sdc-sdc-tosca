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

import org.junit.Test;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class GetVfModulesWithMembersTest {
    private static final String SERVICE_WITH_SINGLE_VF_CSAR = "csars/service-JennyVtsbcVlanSvc-csar.csar";
    private static final String SERVICE_WITH_DOUBLE_VF_CSAR = "csars/service-Metaswitch1-csar.csar";
    private static final String VF_MODULE_MODEL_INVARIANT_UUID = "vfModuleModelInvariantUUID";
    private static final String VF_MODULE_MODEL_UUID = "vfModuleModelUUID";
    private static final String VF_MODULE_MODEL_VERSION = "vfModuleModelVersion";
    private static final String VF_MODULE_MODEL_CUSTOMIZATION_UUID = "vfModuleModelCustomizationUUID";
    private ISdcCsarHelper helper;
    private static Logger log = LoggerFactory.getLogger(GetVfModulesWithMembersTest.class.getName());

    public  void setUp(String path) {
        try {
            URL resource = GetEntityTest.class.getClassLoader().getResource(path);
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            log.error("Failed to create ISdcCsarHelper object from " + path);
        }
    }

    @Test
    public void getVfModulesByVfSingleVfTest() {
        setUp(SERVICE_WITH_SINGLE_VF_CSAR);
        List<String> expectedVfModules = newArrayList("jennyvtsbcvlanvnf0..JennyVtsbcVlanVnf..module_2_perimeta_rtp_msc..module-1",
                "jennyvtsbcvlanvnf0..JennyVtsbcVlanVnf..base_perimeta_deployment_create..module-0",
                "jennyvtsbcvlanvnf0..JennyVtsbcVlanVnf..module_1_perimeta_ssc_a..module-3",
                "jennyvtsbcvlanvnf0..JennyVtsbcVlanVnf..module_1_perimeta_ssc_b..module-2");

        List<IEntityDetails> entities = helper.getVFModule("05e77410-a1d8-44fe-8440-b9410c8f98ee");
        verifyVfModulesNames(expectedVfModules, entities);

        for(IEntityDetails entity : entities){
            char moduleIndex = entity.getName().charAt(entity.getName().length()-1);
            List<String> actualMembersList = newArrayList();
            switch (moduleIndex){
                case '0':
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_INVARIANT_UUID,"a141bf13-d817-4258-98d6-9f9d51fbe1c6");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_UUID,"e10458d4-6e4f-40a4-b47b-57a97380efc1");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_VERSION,"5");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_CUSTOMIZATION_UUID,"8f07ed2f-7f10-4f06-bba6-8f1222ced3ef");
                    verifyModuleMembers(entity, actualMembersList, Arrays.asList("int_unused_network", "int_ha_rsg", "int_ha_network"));
                    verifyModuleProperties(entity,moduleIndex,null);
                    break;
                case '1':
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_INVARIANT_UUID,"eff0e0fd-67c5-4891-9382-d6959d761442");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_UUID,"d65b3d01-7b3d-4b27-9bd2-0165bab75709");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_VERSION,"5");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_CUSTOMIZATION_UUID,"97b648a3-eaf1-4dfb-a685-259b74a1a6fb");
                    verifyModuleMember(entity, "abstract_rtp_msc");
                    verifyModuleProperties(entity,moduleIndex,"module_2_perimeta_rtp_msc");
                    break;
                case '2':
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_INVARIANT_UUID,"1c0e7c32-897c-454a-bb5d-42cc5e8f135c");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_UUID,"99589e9b-e650-4b0a-aa01-0dbf51adb226");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_VERSION,"5");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_CUSTOMIZATION_UUID,"fedb631d-211b-4659-a7eb-19421905808c");
                    verifyModuleMember(entity, "abstract_ssc");
                    verifyModuleProperties(entity,moduleIndex,"module_1_perimeta_ssc_b");
                    break;
                case '3':
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_INVARIANT_UUID,"5a7f8d9c-e102-4556-9484-3a5be8020977");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_UUID,"6130333c-6e41-4abe-84aa-7c669c6d2287");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_VERSION,"5");
                    verifyModuleMetadata(entity, VF_MODULE_MODEL_CUSTOMIZATION_UUID,"94d27f05-a116-4662-b330-8758c2b049d7");
                    verifyModuleMember(entity, "abstract_ssc");
                    verifyModuleProperties(entity,moduleIndex,"module_1_perimeta_ssc_a");
                    break;
                default:
                    log.error(String.format("Module with index %s does not exist in the model of %s", moduleIndex, SERVICE_WITH_SINGLE_VF_CSAR));
            }
        }
    }

    @Test
    public void getVfModulesByServiceSingleVfTest() {
        setUp(SERVICE_WITH_SINGLE_VF_CSAR);
        List<String> expectedVfModules = newArrayList("jennyvtsbcvlanvnf0..JennyVtsbcVlanVnf..module_2_perimeta_rtp_msc..module-1",
                "jennyvtsbcvlanvnf0..JennyVtsbcVlanVnf..base_perimeta_deployment_create..module-0",
                "jennyvtsbcvlanvnf0..JennyVtsbcVlanVnf..module_1_perimeta_ssc_a..module-3",
                "jennyvtsbcvlanvnf0..JennyVtsbcVlanVnf..module_1_perimeta_ssc_b..module-2");

        List<IEntityDetails> entities = helper.getVFModule();
        verifyVfModulesNames(expectedVfModules, entities);

        for(IEntityDetails entity : entities){
            char moduleIndex = entity.getName().charAt(entity.getName().length()-1);
            List<String> actualMembersList = newArrayList();
            entity.getMembers();
            switch (moduleIndex){
                case '0':
                    verifyModuleMembers(entity, actualMembersList, Arrays.asList("int_unused_network", "int_ha_rsg", "int_ha_network"));
                    break;
                case '1':
                    verifyModuleMember(entity, "abstract_rtp_msc");
                    break;
                case '2':
                case '3':
                    verifyModuleMember(entity, "abstract_ssc");
                    break;
                default:
                    log.error(String.format("Module with index %s does not exist in the model of %s", moduleIndex, SERVICE_WITH_SINGLE_VF_CSAR));
            }
        }
    }

    @Test
    public void getVfModulesByVfDuplicateVfTest() {
        setUp(SERVICE_WITH_DOUBLE_VF_CSAR);
        List<String> expectedVfModules = newArrayList("vsp10..Vsp1..base_perimeta_deployment_create..module-0",
                "vsp10..Vsp1..module_2_perimeta_rtp_msc..module-1",
                "vsp10..Vsp1..module_1_perimeta_ssc_a..module-3",
                "vsp10..Vsp1..module_1_perimeta_ssc_b..module-2");

        List<IEntityDetails> entities = helper.getVFModule("2b5f00de-8816-465c-b7bc-c36e26775e1e");
        verifyVfModulesNames(expectedVfModules, entities);

        for(IEntityDetails entity : entities){
            char moduleIndex = entity.getName().charAt(entity.getName().length()-1);
            List<String> actualMembersList = newArrayList();
            switch (moduleIndex){
                case '0':
                    verifyModuleMembers(entity, actualMembersList, Arrays.asList("shared_perimeta_int_ha_rsg", "shared_perimeta_internal_ha_net_0",
                            "shared_perimeta_internal_unused_net_0"));
                    break;
                case '1':
                    verifyModuleMembers(entity, actualMembersList, Arrays.asList("abstract_rtp_msc_a", "abstract_rtp_msc_b"));
                    break;
                case '2':
                    verifyModuleMember(entity,"abstract_ssc_b");
                    break;
                case '3':
                    verifyModuleMember(entity,"abstract_ssc_a");
                    break;
                default:
                    log.error(String.format("Module with index %s does not exist in the model of %s", moduleIndex, SERVICE_WITH_DOUBLE_VF_CSAR));
            }
        }
    }

    @Test
    public void getVfModulesByServiceDuplicateVfTest() {
        setUp(SERVICE_WITH_DOUBLE_VF_CSAR);
        List<String> expectedVfModules = newArrayList("vsp10..Vsp1..base_perimeta_deployment_create..module-0",
                "vsp10..Vsp1..module_2_perimeta_rtp_msc..module-1",
                "vsp10..Vsp1..module_1_perimeta_ssc_a..module-3",
                "vsp10..Vsp1..module_1_perimeta_ssc_b..module-2",
                "vsp11..Vsp1..base_perimeta_deployment_create..module-0",
                "vsp11..Vsp1..module_2_perimeta_rtp_msc..module-1",
                "vsp11..Vsp1..module_1_perimeta_ssc_a..module-3",
                "vsp11..Vsp1..module_1_perimeta_ssc_b..module-2");

        List<IEntityDetails> entities = helper.getVFModule();
        verifyVfModulesNames(expectedVfModules, entities);

        for(IEntityDetails entity : entities){
            char moduleIndex = entity.getName().charAt(entity.getName().length()-1);
            List<String> actualMembersList = newArrayList();
            switch (moduleIndex){
                case '0':
                    verifyModuleMembers(entity, actualMembersList, Arrays.asList("shared_perimeta_int_ha_rsg",
                            "shared_perimeta_internal_ha_net_0",
                            "shared_perimeta_internal_unused_net_0"));
                    break;
                case '1':
                    verifyModuleMembers(entity, actualMembersList, Arrays.asList("abstract_rtp_msc_a",
                            "abstract_rtp_msc_b"));
                    break;
                case '2':
                    verifyModuleMember(entity,"abstract_ssc_b");
                    break;
                case '3':
                    verifyModuleMember(entity,"abstract_ssc_a");
                    break;
                default:
                    log.error(String.format("Module with index %s does not exist in the model of %s", moduleIndex, SERVICE_WITH_DOUBLE_VF_CSAR));
            }
        }
    }

    @Test
    public void getVfModulesManualServiceTest(){
        setUp("csars/service-Servicenovfmodules-csar.csar");
        List<IEntityDetails> entities = helper.getVFModule();
        assertThat(entities.size()).isEqualTo(0);
    }

    private void verifyVfModulesNames(List<String> expectedVfModules, List<IEntityDetails> entities) {
        List<String> actualVfModules = newArrayList();
        for (IEntityDetails entity : entities) {
            actualVfModules.add(entity.getName());
        }
        assertThat(actualVfModules).containsExactlyInAnyOrderElementsOf(expectedVfModules);
    }

    private void verifyModuleMembers(IEntityDetails entity, List<String> actualMembersList, List<String> expectedMembersList) {
        entity.getMemberNodes().forEach(member -> {
            actualMembersList.add(member.getName());
        });
        assertThat(actualMembersList).containsOnlyElementsOf(expectedMembersList);
        actualMembersList.clear();
    }

    private void verifyModuleMember(IEntityDetails entity, String expectedMember) {
        String actualMember = entity.getMemberNodes().get(0).getName();
        assertThat(actualMember).isEqualTo(expectedMember);
    }

    private void verifyModuleMetadata(IEntityDetails entity, String metadataKey, String metadataValue) {
        assertThat(entity.getMetadata().getValue(metadataKey)).isEqualTo(metadataValue);
    }

    private void verifyModuleProperties(IEntityDetails entity, char moduleNumber, String vfModuleLabel){
        Map<String, String> expectedProperties;
        if (moduleNumber == '0'){
            expectedProperties = getExpectedPropertiesBase();
        } else {
            expectedProperties = getExpectedPropertiesNonBase(vfModuleLabel);
        }
        assertThat(expectedProperties).isEqualTo(getActualProperties(entity));
    }

    private Map<String, String> getExpectedPropertiesNonBase(String vfModuleLabel){
        Map<String, String> properties = new HashMap<>();
        properties.put("min_vf_module_instances","0");
        properties.put("vf_module_label",vfModuleLabel);
        properties.put("vf_module_type","Expansion");
        properties.put("isBase","false");
        properties.put("initial_count","0");
        properties.put("volume_group","false");
        return properties;
    }

    private Map<String,String> getExpectedPropertiesBase(){
        Map<String, String> properties = new HashMap<>();
        properties.put("min_vf_module_instances","1");
        properties.put("vf_module_label","base_perimeta_deployment_create");
        properties.put("max_vf_module_instances","1");
        properties.put("vf_module_type","Base");
        properties.put("isBase","true");
        properties.put("initial_count","1");
        properties.put("volume_group","false");
        return properties;
    }

    private Map<String,String> getActualProperties(IEntityDetails entity){
        Map<String, String> properties = new HashMap<>();
        for (Property prop : entity.getProperties().values()){
            properties.put(prop.getName(),prop.getValue().toString());
        }
        return properties;
    }
}


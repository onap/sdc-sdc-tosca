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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;
import org.onap.sdc.tosca.parser.api.CapabilityAssignment;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.api.Property;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GetEntityTest {

    private static ISdcCsarHelper helper = null;

    @BeforeClass
    public static void setUpClass() {
        try {
            URL resource = GetEntityTest.class.getClassLoader()
                .getResource("csars/service-JennyVtsbcVlanSvc-csar.csar");
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCpEntitiesFromCVFC() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CP)
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .customizationUUID("1fdc9625-dfec-48e1-aaf8-7b92f78ca854")
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(6, entities.size());
    }

    @Test
    public void getOneCpEntityFromCVFC() {
        EntityQuery entityQuery = EntityQuery.newBuilder("org.openecomp.resource.cp.v2.extNeutronCP")
            .uUID("d5e13a34-c983-4a36-a44a-a53a6e850d73")
            .customizationUUID("e97b3399-ab2d-4a34-b07a-9bd5f6461335")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .customizationUUID("1fdc9625-dfec-48e1-aaf8-7b92f78ca854")
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(1, entities.size());
        assertEquals("ssc_ssc_avpn_port_0", entities.get(0).getName());
        assertEquals(18, entities.get(0).getProperties().size());
        assertEquals(1, entities.get(0).getRequirements().size());
        assertEquals(13, entities.get(0).getCapabilities().size());
        assertEquals(0, entities.get(0).getInputs().size());
        List<CapabilityAssignment> capAssignments = entities.get(0).getCapabilities();
        CapabilityAssignment capabilityAssignment = capAssignments.stream()
            .filter(p -> p.getName().equals("network.outgoing.packets.rate")).findAny().orElse(null);
        assertEquals("org.openecomp.capabilities.metric.Ceilometer", capabilityAssignment.getDefinition().getType());

    }

    @Test
    public void getCpsFromCVFCRecursively() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CP)
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(16, entities.size());
        assertEquals("jenny vTSBC vlan VNF 0#abstract_rtp_msc", entities.get(2).getPath());
        assertEquals(
            "jenny vTSBC vlan VNF 0#abstract_rtp_msc#rtp_msc_rtp_msc_avpn_port_0_vlan_subinterface_rtp_msc_avpn",
            entities.get(7).getPath());
    }

    @Test
    public void getCpsFromVfRecursively() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CP)
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(16, entities.size());
        assertEquals("jenny vTSBC vlan VNF 0#abstract_rtp_msc", entities.get(2).getPath());
        assertEquals(
            "jenny vTSBC vlan VNF 0#abstract_rtp_msc#rtp_msc_rtp_msc_avpn_port_0_vlan_subinterface_rtp_msc_avpn",
            entities.get(7).getPath());
    }

    @Test
    public void getCpByUuidsFromCVFCRecursively() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CP)
            .customizationUUID("d674b231-34ba-4777-b83a-78be33960a69")
            .uUID("f511e1c8-1f21-4370-b7b6-f57a61c15211")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(1, entities.size());
        assertEquals("rtp_msc_subint_mis_vmi_0", entities.get(0).getName());
        assertEquals("ff9ae686-f030-4431-afb7-b65d1bf4733e",
            entities.get(0).getParent().getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_UUID));
        assertTrue(entities.get(0).getMemberNodes().isEmpty());
    }

    @Test
    public void getCpByWrongUuidFromCVFCRecursively() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CP)
            .customizationUUID("d674b231-34ba-4777-b83a-78be33960a69")
            .uUID("f511e1c8-1f21-4370-b7b6-f57a61c15213")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(0, entities.size());
    }

    @Test
    public void getServiceGroups() {
        EntityQuery entityQuery = EntityQuery.newBuilder(EntityTemplateType.GROUP)
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(4, entities.size());
        assertTrue(entities.get(0).getRequirements().isEmpty());
        assertTrue(entities.get(1).getCapabilities().isEmpty());
        assertEquals(0, entities.get(0).getInputs().size());
        assertTrue(entities.get(0).getPath().isEmpty() && entities.get(1).getPath().isEmpty() &&
            entities.get(2).getPath().isEmpty() && entities.get(3).getPath().isEmpty());
    }

    @Test
    public void getVfGroupsByType1() {
        EntityQuery entityQuery = EntityQuery.newBuilder("tosca.groups.Root")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(2, entities.size());
        assertTrue(entities.get(0).getMemberNodes().get(0).getName().equals("abstract_rtp_msc") ||
            entities.get(1).getMemberNodes().get(0).getName().equals("abstract_rtp_msc"));
        assertTrue(entities.get(0).getMemberNodes().get(0).getName().equals("abstract_ssc") ||
            entities.get(1).getMemberNodes().get(0).getName().equals("abstract_ssc"));

    }

    @Test
    public void getVfGroupByType2() {
        EntityQuery entityQuery = EntityQuery.newBuilder("org.openecomp.groups.VfcInstanceGroup")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(1, entities.size());
        assertTrue(entities.get(0).getName().equals("avpn_group"));
        assertTrue(entities.get(0).getMemberNodes().get(0).getName().equals("abstract_ssc") ||
            entities.get(0).getMemberNodes().get(1).getName().equals("abstract_ssc"));
        assertTrue(entities.get(0).getMemberNodes().get(0).getName().equals("abstract_rtp_msc") ||
            entities.get(0).getMemberNodes().get(1).getName().equals("abstract_rtp_msc"));
        assertEquals(4, entities.get(0).getProperties().size());
        assertEquals(2, entities.get(0).getMembers().size());
        assertTrue(entities.get(0).getRequirements().isEmpty());

    }

    @Test
    public void getOneServiceGroup() {
        EntityQuery entityQuery = EntityQuery.newBuilder(EntityTemplateType.GROUP)
            .customizationUUID("94d27f05-a116-4662-b330-8758c2b049d7")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(1, entities.size());
        assertEquals("org.openecomp.groups.VfModule", entities.get(0).getToscaType());
        assertTrue(entities.get(0).getPath().isEmpty());
        assertTrue(entities.get(0).getMembers().isEmpty());
        assertNull(entities.get(0).getParent());
        assertTrue(entities.get(0).getTargets().isEmpty());
    }

    @Test
    public void getAllGroups() {
        EntityQuery entityQuery = EntityQuery.newBuilder(EntityTemplateType.GROUP)
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(19, entities.size());
        assertTrue(entities.get(9).getTargetEntities().isEmpty());
        assertEquals(1, entities.get(7).getMembers().size());
        assertEquals("org.openecomp.groups.VfModule", entities.get(7).getToscaType());
        assertEquals("rtp_msc_subint_avpn_vmi_0", entities.get(5).getMemberNodes().get(0).getName());
    }

    @Test
    public void getAllPolicies() {
        EntityQuery entityQuery = EntityQuery.newBuilder(EntityTemplateType.POLICY)
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(1, entities.size());
        assertEquals("jennyvtsbcvlanvnf..External..0",
            entities.get(0).getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_NAME));
        assertEquals(EntityTemplateType.GROUP, entities.get(0).getTargetEntities().get(0).getEntityType());
        assertEquals("org.openecomp.policies.External", entities.get(0).getToscaType());
        assertTrue(entities.get(0).getMembers().isEmpty());
        assertEquals("jenny vTSBC vlan VNF 0", entities.get(0).getPath());
        assertEquals(0, entities.get(0).getInputs().size());
    }

    @Test
    public void getServicePolicy() {
        EntityQuery entityQuery = EntityQuery.newBuilder(EntityTemplateType.POLICY)
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);
        assertEquals(0, entities.size());
    }

    @Test
    public void getVfPolicyByUUID() {
        EntityQuery entityQuery = EntityQuery.newBuilder("org.openecomp.policies.External")
            .uUID("0181f46a-3c68-47dd-9839-8692726356e5")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(1, entities.size());
        assertEquals(EntityTemplateType.GROUP, entities.get(0).getTargetEntities().get(0).getEntityType());
        assertTrue(entities.get(0).getMembers().isEmpty());
        assertTrue(entities.get(0).getRequirements().isEmpty());
        assertTrue(entities.get(0).getCapabilities().isEmpty());
        assertEquals("jenny vTSBC vlan VNF 0", entities.get(0).getPath());
    }

    @Test
    public void getVfPolicyByWrongToscaTypeAndUUID() {
        EntityQuery entityQuery = EntityQuery.newBuilder("org.openecomp.policies.External1")
            .uUID("0181f46a-3c68-47dd-9839-8692726356e5")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.VF)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);
        assertEquals(0, entities.size());
    }

    @Test
    public void getCvfcPolicyByToscaTypeAndUUID() {
        EntityQuery entityQuery = EntityQuery.newBuilder("org.openecomp.policies.External")
            .uUID("0181f46a-3c68-47dd-9839-8692726356e5")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);
        assertEquals(0, entities.size());
    }


    @Test
    public void getAllEntitiesInServiceOnly() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
            .build();

        List<IEntityDetails> entities = helper.getEntity(null, topologyTemplateQuery, false);
        assertEquals(5, entities.size());
        assertEquals("org.openecomp.resource.vf.JennyVtsbcVlanVnf", entities.get(4).getToscaType());
        assertEquals("org.openecomp.groups.VfModule", entities.get(0).getToscaType());
    }


    @Test
    public void getAllEntitiesInServiceRecursively() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
            .build();

        List<IEntityDetails> entities = helper.getEntity(null, topologyTemplateQuery, true);
        assertEquals(48, entities.size());
    }

    @Test
    public void getAllEntitiesInCvfcByCUUID() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .customizationUUID("b90c9f8a-9c07-4507-913f-70b533f5934d")
            .build();

        List<IEntityDetails> entities = helper.getEntity(null, topologyTemplateQuery, false);
        assertEquals(2, entities.size());
        assertTrue(entities.get(1).getMembers().isEmpty());
        assertEquals("ssc_subint_mis_vmi_0", entities.get(0).getMembers().get(0));
        assertTrue("ssc_subint_mis_vmi_0".equals(entities.get(1).getName()));
        assertTrue("vlan_subinterface_ssc_mis_group".equals(entities.get(0).getName()));
        assertEquals("org.openecomp.resource.cp.nodes.heat.network.v2.contrailV2.VLANSubInterface",
            entities.get(1).getToscaType());
        assertEquals("org.openecomp.groups.heat.HeatStack", entities.get(0).getToscaType());
        assertTrue(entities.get(0).getTargetEntities().isEmpty());

    }

    @Test
    public void getAllEntitiesInCvfcByUuidRecursively() {
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .customizationUUID("1fdc9625-dfec-48e1-aaf8-7b92f78ca854")
            .build();

        List<IEntityDetails> entities = helper.getEntity(null, topologyTemplateQuery, true);
        assertEquals(13, entities.size());
    }

    @Test
    public void getCpPropertyWhenCpIsInInternalCVFC() {
        EntityQuery entityQuery = EntityQuery.newBuilder("org.openecomp.resource.cp.nodes.heat.network.neutron.Port")
            .customizationUUID("c03b7d04-5457-4ad2-9102-1edb7806c7b2")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(1, entities.size());
        Property p = entities.get(0).getProperties().get("ip_requirements");
        List<String> valueList = p.getLeafPropertyValue("ip_version");
        assertEquals(1, valueList.size());
        assertEquals("4", valueList.get(0));
    }

    @Test
    public void getCpPropertyOnCVFC() {
        EntityQuery entityQuery = EntityQuery.newBuilder("org.openecomp.resource.cp.v2.extNeutronCP")
            .customizationUUID("e56919d1-b23f-4334-93b0-1daa507fd2a9")
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
            .customizationUUID("1fdc9625-dfec-48e1-aaf8-7b92f78ca854")
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(1, entities.size());
        Property p = entities.get(0).getProperties().get("mac_requirements");
        List<String> valueList = p.getLeafPropertyValue("mac_count_required#is_required");
        assertEquals(1, valueList.size());
        assertEquals("false", valueList.get(0));
    }


    @Test
    public void getServiceInputs() {
        EntityQuery entityQuery = EntityQuery.newBuilder(EntityTemplateType.NODE_TEMPLATE)
            .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
            .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);
        assertEquals(163, entities.get(0).getInputs().size());
    }
}

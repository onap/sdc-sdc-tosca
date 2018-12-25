package org.onap.sdc.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.Property;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GetEntityTest {

    private static ISdcCsarHelper helper = null;

    @BeforeClass
    public static void setUpClass() {
        try {
            String filePath = GetEntityTest.class.getClassLoader().getResource("csars/service-JennyVtsbcVlanSvc-csar.csar").getFile();
            helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(filePath);
        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        //none recursive search for groups in the service
        EntityQuery entityQuery1 = EntityQuery.newBuilder(EntityTemplateType.GROUP)
                .uUID("1233456")
                .build();

        TopologyTemplateQuery topologyTemplateQuery1 = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();
        List<IEntityDetails> entities = helper.getEntity(entityQuery1, topologyTemplateQuery1, false);

        for (IEntityDetails entity: entities) {
            List<IEntityDetails> members = entity.getMemberNodes();
        }

        //recursive search for CPs in a specific CVFC
        EntityQuery entityQuery2 = EntityQuery.newBuilder(SdcTypes.CP)
                .customizationUUID("345678903456")
                .build();

        TopologyTemplateQuery topologyTemplateQuery2 = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
                .customizationUUID("12346767676")
                .build();
        entities = helper.getEntity(entityQuery2, topologyTemplateQuery2, true);

        for (IEntityDetails entity: entities) {
            Map<String, Property> properties = entity.getProperties();
            Property property = properties.get("network_role");
            String network_role_value = (String) property.getValue();
        }

    }

   /* @Test
    public void getCpEntityMock() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CP)
                .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
                .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(1, entities.size());
        assertEquals(1, entities.get(0).getMemberNodes().size());
        assertEquals(EntityTemplateType.NODE_TEMPLATE, entities.get(0).getType());
        assertEquals("28.0", entities.get(0).getMetadata().getValue("version"));
        assertEquals("CP", entities.get(0).getMetadata().getValue("type"));
        assertEquals("extNeutronCP", entities.get(0).getMetadata().getValue("name"));
        assertEquals("abstract_ssc", entities.get(0).getParent().getName());
        assertEquals("jenny vTSBC vlan VNF 0#abstract_ssc#ssc_ssc_avpn_port_0", entities.get(0).getPath());
        assertTrue(entities.get(0).getProperties() != null && entities.get(0).getProperties().size() == 18);

    }*/

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
        assertEquals(13, entities.get(0).getCapabilities().entrySet().size());

    }

    @Test
    public void getCpsFromCVFCRecursively() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CP)
                .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.CVFC)
                .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(16, entities.size());
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
        assertEquals("ff9ae686-f030-4431-afb7-b65d1bf4733e", entities.get(0).getParent().getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_UUID));
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
        assertNull(entities.get(0).getParent());
    }

    @Test
    public void getAllGroups() {
        EntityQuery entityQuery = EntityQuery.newBuilder(EntityTemplateType.GROUP)
                .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, true);

        assertEquals(19, entities.size());
        assertTrue(entities.get(9).getTargetNodes().isEmpty());
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
        assertEquals("jennyvtsbcvlanvnf..External..0", entities.get(0).getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_NAME));
        assertEquals(EntityTemplateType.GROUP, entities.get(0).getTargetNodes().get(0).getType());
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
        assertEquals(EntityTemplateType.GROUP, entities.get(0).getTargetNodes().get(0).getType());
        assertTrue(entities.get(0).getRequirements().isEmpty());
        assertTrue(entities.get(0).getCapabilities().isEmpty());
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
        assertTrue("ssc_subint_mis_vmi_0".equals(entities.get(0).getName()) || "ssc_subint_mis_vmi_0".equals(entities.get(1).getName()));
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

}

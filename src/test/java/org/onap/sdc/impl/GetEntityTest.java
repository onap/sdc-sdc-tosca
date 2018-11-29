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
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Property;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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
            List<NodeTemplate> members = entity.getMemberNodes();
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

    @Test
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

    }
}

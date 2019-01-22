package org.onap.sdc.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.RequirementAssignment;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetEntityPortMirroringTest {

    private final static String cap0 = "vepdg5afn0.vlb_1.port_mirroring_vlb_gn_vepdg_fn_5_RVMI";
    private final static String cap1 = "radcomfnvlbavf11_3_3vepdg0.abstract_vlbagentbase_eph_aff.port_mirroring_vlbagentbase_eph_aff_vlbagentbase_eph_aff_int_pktmirror_1_port";
    private static ISdcCsarHelper helper = null;

    @BeforeClass
    public static void setUpClass() {
        try {
            URL resource = GetEntityPortMirroringTest.class.getClassLoader()
                    .getResource("csars/service-JennnyVepdgPortMirroringTest-csar.csar");
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getServiceConfigurations() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CONFIGURATION)
                .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(1, entities.size());
        assertEquals("54308d8b-21ca-40a1-bd3e-efde64791605", entities.get(0).getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID));
        assertEquals("Port Mirroring Configuration", entities.get(0).getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_NAME));
        assertEquals(2, entities.get(0).getRequirements().size());
        assertTrue(entities.get(0).getProperties().isEmpty());
        assertEquals(1, entities.get(0).getCapabilities().size());
    }

    @Test
    public void getServiceProxyOnService() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.SERVICE_PROXY)
                .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);

        assertEquals(2, entities.size());
        assertTrue("vEPDG-5A-FN SVC Service Proxy".equals(entities.get(0).getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_NAME)) ||
                        "vEPDG-5A-FN SVC Service Proxy".equals(entities.get(1).getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_NAME)));
        assertTrue("Radcom FN vLBA SVC 11_3_3 vEPDG Service Proxy".equals(entities.get(0).getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_NAME)) ||
                "Radcom FN vLBA SVC 11_3_3 vEPDG Service Proxy".equals(entities.get(1).getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_NAME)));

        assertTrue(entities.get(0).getRequirements().isEmpty());
        assertTrue(entities.get(0).getProperties().isEmpty());
        assertEquals(940, entities.get(0).getCapabilities().size());
        assertEquals(197, entities.get(1).getCapabilities().size());
    }

    @Test
    public void getCapabilityForCOnfigurationRequirementOnService() {
        EntityQuery entityQueryC = EntityQuery.newBuilder(SdcTypes.CONFIGURATION)
                .build();

        EntityQuery entityQueryS = EntityQuery.newBuilder(SdcTypes.SERVICE_PROXY)
                .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();

        List<IEntityDetails> configurationNodes = helper.getEntity(entityQueryC, topologyTemplateQuery, false);
        List<IEntityDetails> proxyNodes = helper.getEntity(entityQueryS, topologyTemplateQuery, false);

        assertEquals(2, proxyNodes.size());
        assertEquals(1, configurationNodes.size());

        Map<String, RequirementAssignment> reqMap = configurationNodes.get(0).getRequirements();
        assertEquals(cap0, reqMap.get("source").getCapabilityName());
        assertEquals(cap1, reqMap.get("collector").getCapabilityName());

        assertTrue(isCapabilityFound(proxyNodes.get(0), cap0));
        assertTrue(isCapabilityFound(proxyNodes.get(1), cap1));
    }

    private boolean isCapabilityFound(IEntityDetails nodeDetails, String capName) {
        List<CapabilityAssignment> assignments = nodeDetails.getCapabilities()
                .values()
                .stream()
                .filter(ca->capName.equals(ca.getDefinition()
                        .getName()))
                .collect(Collectors.toList());
        return assignments.size() == 1;
    }
}

package org.onap.sdc.impl;

import org.junit.Test;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;

import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetForwardingPathTest {
    private static ISdcCsarHelper helper = null;

    public  void setUp(String path) {
        try {
            URL resource = GetEntityTest.class.getClassLoader().getResource(path);
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fpTest(){
        setUp("csars/service-Servicefp1-csar.csar");

        EntityQuery entityQuery = EntityQuery.newBuilder("org.openecomp.nodes.ForwardingPath")
                .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);
        assertThat(entities.size()).isEqualTo(1);
    }
}

/*-
 * ============LICENSE_START=======================================================
 * sdc-tosca
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.RequirementAssignment;

import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CapReqWithSameNamesTest {

    private static ISdcCsarHelper helper = null;

    @BeforeAll
    public static void setUpClass() {
        try {
            URL resource = GetEntityPortMirroringTest.class.getClassLoader()
                    .getResource("csars/service-VdbePx-csar.csar");
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verify2reqWithSameName() {
        EntityQuery entityQuery = EntityQuery.newBuilder(SdcTypes.CONFIGURATION)
                .build();
        TopologyTemplateQuery topologyTemplateQuery = TopologyTemplateQuery.newBuilder(SdcTypes.SERVICE)
                .build();

        List<IEntityDetails> entities = helper.getEntity(entityQuery, topologyTemplateQuery, false);
        long count = entities.stream()
                .flatMap(f -> f.getRequirements().stream())
                .map(RequirementAssignment::getName)
                .filter("vlan_assignment"::equals).count();
        assertEquals(2L, count);

    }

}

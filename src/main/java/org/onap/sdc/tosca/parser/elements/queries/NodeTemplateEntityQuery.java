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

package org.onap.sdc.tosca.parser.elements.queries;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.NodeTemplate;
import org.onap.sdc.tosca.parser.api.ToscaTemplate;
import org.onap.sdc.tosca.parser.elements.EntityDetailsFactory;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;

/**
 * Implements EntityQuery object for NodeTemplates
 */
public class NodeTemplateEntityQuery extends EntityQuery {

    NodeTemplateEntityQuery() {
        super(EntityTemplateType.NODE_TEMPLATE, null, null);
    }

    NodeTemplateEntityQuery(SdcTypes nodeTemplateType) {
        super(EntityTemplateType.NODE_TEMPLATE, nodeTemplateType, null);
    }

    NodeTemplateEntityQuery(String toscaType) {
        super(EntityTemplateType.NODE_TEMPLATE, null, toscaType);
    }

    static List<IEntityDetails> convertNodeTemplatesListToEntityDetailsList(Stream<NodeTemplate> nodeTemplates) {
        return nodeTemplates
            .map(nt -> EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, nt))
            .collect(Collectors.toList());
    }

    @Override
    public List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate) {
        if (nodeTemplate.getSubMappingToscaTemplate() != null) {
            return convertNodeTemplatesListToEntityDetailsList(filter(nodeTemplate.getSubMappingToscaTemplate()
                .getNodeTemplates()));
        }
        return Lists.newArrayList();
    }

    @Override
    public List<IEntityDetails> getEntitiesFromService(ToscaTemplate toscaTemplate) {
        return convertNodeTemplatesListToEntityDetailsList(filter(toscaTemplate.getNodeTemplates()));
    }

    private Stream<NodeTemplate> filter(List<NodeTemplate> nodeTemplateList) {
        return nodeTemplateList.stream()
            .filter(nt -> isSearchCriteriaMatched(nt.getMetaData(), nt.getType()))
            .filter(nt -> getNodeTemplateType() == null ||
                isStringMatchingOrNull(nt.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE),
                    getNodeTemplateType().getValue()));
    }


}

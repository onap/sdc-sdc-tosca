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

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.NodeTemplate;
import org.onap.sdc.tosca.parser.api.ToscaTemplate;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs search for entity templates inside node template according to query criteria
 */
class QueryProcessor {

    private static final Logger logger = LoggerFactory.getLogger(QueryProcessor.class.getName());

    private final EntityQuery entityQuery;
    private final TopologyTemplateQuery topologyTemplateQuery;
    private final ToscaTemplate toscaTemplate;
    private boolean isRecursive = false;

    QueryProcessor(ToscaTemplate toscaTemplate, EntityQuery entityQuery, TopologyTemplateQuery topologyTemplateQuery,
                   boolean isRecursive) {
        this.toscaTemplate = toscaTemplate;
        this.entityQuery = entityQuery == null ? EntityQuery.newBuilder(EntityTemplateType.ALL).build() : entityQuery;
        this.topologyTemplateQuery = topologyTemplateQuery;
        this.isRecursive = isRecursive;
    }

    List<IEntityDetails> doQuery() {
        List<IEntityDetails> entityDetailsList = Lists.newArrayList();
        if (isServiceSearch()) {
            //search for entities inside the service
            if (logger.isDebugEnabled()) {
                logger.debug("Service {} is searched for {}",
                    toscaTemplate.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_NAME), entityQuery);
            }
            entityDetailsList.addAll(entityQuery.getEntitiesFromService(toscaTemplate));

            if (!isRecursive) {
                return entityDetailsList;
            }
        }

        List<NodeTemplate> foundTopologyTemplates = getInternalTopologyTemplates(toscaTemplate.getNodeTemplates(),
            false);
        if (isRecursive) {
            if (logger.isDebugEnabled()) {
                logger.debug("Search for entities recursively");
            }
            //go over internal topology templates of the found templates
            // and search for instances of the same type
            //if the queried topology template is "SERVICE",  search for all instances of templates in the service
            List<NodeTemplate> internalTopologyTemplates = foundTopologyTemplates.stream()
                .filter(nt -> nt.getSubMappingToscaTemplate() != null)
                .map(nt -> getInternalTopologyTemplates(nt.getSubMappingToscaTemplate().getNodeTemplates(), true))
                .flatMap(List::stream)
                .collect(Collectors.toList());
            foundTopologyTemplates.addAll(internalTopologyTemplates);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Found topology templates {} matching following query criteria: {}",
                foundTopologyTemplates, topologyTemplateQuery);
        }
        //go over all node templates found according to query criteria and recursive flag and
        // search for the requested entities.
        entityDetailsList.addAll(searchEntitiesInsideTopologyTemplates(foundTopologyTemplates));

        return entityDetailsList;
    }

    private Map<String, NodeTemplate> convertListToMap(List<NodeTemplate> nodeTemplateList) {
        // we use map to avoid duplicate search through same node templates
        return nodeTemplateList.stream()
            .collect(Collectors.toMap(NodeTemplate::getName, nt -> nt, (nt1, nt2) -> nt1));
    }

    private List<IEntityDetails> searchEntitiesInsideTopologyTemplates(List<NodeTemplate> foundTopologyTemplates) {
        return convertListToMap(foundTopologyTemplates)
            .values()
            .stream()
            .map(entityQuery::getEntitiesFromTopologyTemplate)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private boolean isServiceSearch() {
        return topologyTemplateQuery.getNodeTemplateType() == SdcTypes.SERVICE;
    }

    private List<NodeTemplate> getInternalTopologyTemplates(List<NodeTemplate> nodeTemplateList, boolean isRecursive) {
        return nodeTemplateList
            .stream()
            .map(child -> getTopologyTemplatesByQuery(child, isRecursive))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<NodeTemplate> getTopologyTemplatesByQuery(NodeTemplate current, boolean isRecursive) {
        List<NodeTemplate> topologyTemplateList = Lists.newArrayList();

        boolean isTopologyTemplateFound = isRecursive ?
            SdcTypes.isComplex(current.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE))
            : topologyTemplateQuery.isMatchingSearchCriteria(current);
        if (isTopologyTemplateFound) {
            topologyTemplateList.add(current);
            if (!isRecursive) {
                //recursion stop condition
                return topologyTemplateList;
            }
        }
        if (SdcTypes.isComplex(current.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)) &&
            current.getSubMappingToscaTemplate() != null) {
            //search the node template inside a given topology template
            topologyTemplateList.addAll(current.getSubMappingToscaTemplate().getNodeTemplates()
                .stream()
                .map(nt -> getTopologyTemplatesByQuery(nt, isRecursive))
                .flatMap(List::stream)
                .collect(Collectors.toList()));
        }
        return topologyTemplateList;
    }


}

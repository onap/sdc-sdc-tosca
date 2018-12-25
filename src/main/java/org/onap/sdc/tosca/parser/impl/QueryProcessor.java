package org.onap.sdc.tosca.parser.impl;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.ToscaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Performs search for entity templates inside node template according to query criteria
 */
class QueryProcessor {
    private static final Logger logger = LoggerFactory.getLogger(QueryProcessor.class.getName());

    private final EntityQuery entityQuery;
    private final TopologyTemplateQuery topologyTemplateQuery;
    private final ToscaTemplate toscaTemplate;
    private boolean isRecursive = false;

    QueryProcessor(ToscaTemplate toscaTemplate, EntityQuery entityQuery, TopologyTemplateQuery topologyTemplateQuery) {
        this.toscaTemplate = toscaTemplate;
        this.entityQuery = entityQuery == null ? EntityQuery.newBuilder(EntityTemplateType.ALL).build() : entityQuery;
        this.topologyTemplateQuery = topologyTemplateQuery;
    }

    void setRecursiveFlag(boolean isRecursive) {
        this.isRecursive = isRecursive;
    }

    List<IEntityDetails> doQuery() {
        List<IEntityDetails> entityDetailsList = Lists.newArrayList();
        if (isServiceSearch()) {
            //search for entities inside the service
            if (logger.isDebugEnabled()) {
                logger.debug("Service {} is searched for {}", toscaTemplate.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_NAME), entityQuery);
            }
            entityDetailsList.addAll(entityQuery.getEntitiesFromService(toscaTemplate));

            if (!isRecursive) {
                return entityDetailsList;
            }
        }

        List<NodeTemplate> foundTopologyTemplates = getInternalTopologyTemplates(toscaTemplate.getNodeTemplates(), false);
        if (isRecursive) {
            if (logger.isDebugEnabled()) {
                logger.debug("Search for entities recursively");
            }
            //go over internal topology templates of the found templates
            // and search for instances of the same type
            //if the queried topology template is "SERVICE",  search for all instances of templates in the service
            List<NodeTemplate> internalTopologyTemplates = foundTopologyTemplates.stream()
                    .filter(nt->nt.getSubMappingToscaTemplate() != null)
                    .map(nt->getInternalTopologyTemplates(nt.getSubMappingToscaTemplate().getNodeTemplates(), true))
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
                .collect(Collectors.toMap(NodeTemplate::getName, nt->nt, (nt1, nt2)->nt1));
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

    private List<NodeTemplate> getInternalTopologyTemplates(List<NodeTemplate> nodeTemplateList, boolean searchTypeOnly) {
        return nodeTemplateList
            .stream()
            .map(child->getTopologyTemplatesByQuery(child, searchTypeOnly))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<NodeTemplate> getTopologyTemplatesByQuery(NodeTemplate current, boolean searchTypeOnly) {
        List<NodeTemplate> topologyTemplateList = Lists.newArrayList();

        boolean isTopologyTemplateFound = searchTypeOnly ?
                topologyTemplateQuery.isSameSdcType(current.getMetaData()) : topologyTemplateQuery.isMatchingSearchCriteria(current);
        if (isTopologyTemplateFound) {
            topologyTemplateList.add(current);
            if (!isServiceSearch()) {
                //recursion stop condition
                return topologyTemplateList;
            }
        }
        if (SdcTypes.isComplex(current.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)) && current.getSubMappingToscaTemplate() != null) {
            //search the node template inside a given topology template
            topologyTemplateList.addAll(current.getSubMappingToscaTemplate().getNodeTemplates()
                    .stream()
                    .map(nt->getTopologyTemplatesByQuery(nt, searchTypeOnly))
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));
        }
        return topologyTemplateList;
    }


    static QueryProcessorBuilder newBuilder(ToscaTemplate toscaTemplate, EntityQuery entityQuery, TopologyTemplateQuery topologyTemplateQuery) {
        return new QueryProcessorBuilder(toscaTemplate, entityQuery, topologyTemplateQuery);
    }

    static class QueryProcessorBuilder {
        private final QueryProcessor queryProcessor;

        private QueryProcessorBuilder(ToscaTemplate toscaTemplate, EntityQuery entityQuery, TopologyTemplateQuery topologyTemplateQuery) {
            this.queryProcessor = new QueryProcessor(toscaTemplate, entityQuery, topologyTemplateQuery);
        }

        QueryProcessorBuilder setRecursiveFlag(boolean isRecursive) {
            this.queryProcessor.setRecursiveFlag(isRecursive);
            return this;
        }

        QueryProcessor build() {
            return queryProcessor;
        }


    }
}

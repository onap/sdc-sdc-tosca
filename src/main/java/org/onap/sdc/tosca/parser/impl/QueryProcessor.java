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
        List<NodeTemplate> foundTopologyTemplates = toscaTemplate.getNodeTemplates()
                .stream()
                .map(nt->getTopologyTemplatesByQuery(nt, false))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if (isRecursive) {
            if (logger.isDebugEnabled()) {
                logger.debug("Search for entities recursively");
            }
            //go over internal topology templates of the fopund templates
            // and search for instances of the same type
            List<NodeTemplate> internalTopologyTemplates = foundTopologyTemplates.stream()
                    .filter(nt->nt.getSubMappingToscaTemplate() != null)
                    .map(this::getInternalTopologyTemplates)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            foundTopologyTemplates.addAll(internalTopologyTemplates);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Found topology templates {} matching following query criteria: {}",
                    foundTopologyTemplates, topologyTemplateQuery);
        }

        return foundTopologyTemplates.stream()
                .map(entityQuery::getEntitiesFromTopologyTemplate)
                .flatMap(List::stream)
                .collect(Collectors.toList());

    }

    private List<NodeTemplate> getInternalTopologyTemplates(NodeTemplate parent) {
        return parent.getSubMappingToscaTemplate().getNodeTemplates()
            .stream()
            .map(child->getTopologyTemplatesByQuery(child, true))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<NodeTemplate> getTopologyTemplatesByQuery(NodeTemplate current, boolean searchTypeOnly) {
        List<NodeTemplate> topologyTemplateList = Lists.newArrayList();

        boolean isTopologyTemplateFound = searchTypeOnly ?
                topologyTemplateQuery.isSameSdcType(current.getMetaData()) : topologyTemplateQuery.isMatchingSearchCriteria(current);
        if (isTopologyTemplateFound) {
            topologyTemplateList.add(current);
        }
        else if (SdcTypes.isComplex(current.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)) && current.getSubMappingToscaTemplate() != null) {
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

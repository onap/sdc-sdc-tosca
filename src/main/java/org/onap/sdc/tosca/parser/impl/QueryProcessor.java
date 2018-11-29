package org.onap.sdc.tosca.parser.impl;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.ToscaTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class QueryProcessor {

    private final EntityQuery entityQuery;
    private TopologyTemplateQuery topologyTemplateQuery;
    private final ToscaTemplate toscaTemplate;
    private boolean isRecursive = false;

    QueryProcessor(ToscaTemplate toscaTemplate, EntityQuery entityQuery, TopologyTemplateQuery topologyTemplateQuery) {
        this.toscaTemplate = toscaTemplate;
        this.entityQuery = entityQuery;
        this.topologyTemplateQuery = topologyTemplateQuery;
    }

    QueryProcessor(ToscaTemplate toscaTemplate, EntityQuery entityQuery) {
       this(toscaTemplate, entityQuery, null);
    }

    void setTopologyTemplateQuery(TopologyTemplateQuery topologyTemplateQuery) {
        this.topologyTemplateQuery = topologyTemplateQuery;
    }

    void setRecursiveFlag(boolean isRecursive) {
        this.isRecursive = isRecursive;
    }

    public List<IEntityDetails> process() {
        return lookupForEntitiesInContainer(toscaTemplate.getNodeTemplates());
    }

    List<IEntityDetails> lookupForEntitiesInContainer(List<NodeTemplate> nodeTemplates) {
        List<IEntityDetails> entityDetailsList = Lists.newArrayList();
        List<IEntityDetails> foundEntities = nodeTemplates.stream().filter(nt->topologyTemplateQuery.isMatchingSearchCriteria(nt))
                .map(nt->{
                    List<IEntityDetails> entityDetailsList1 = entityQuery.getEntitiesFromTopologyTemplate(nt);
                    if (isRecursive) {
                        entityDetailsList1.addAll(lookupForEntitiesInContainer(nt.getSubMappingToscaTemplate().getNodeTemplates()));
                    }
                    return entityDetailsList1;

                })
                .flatMap(Collectors.toList());
        return entityDetailsList;
    }

    public static QueryProcessorBuilder newBuilder(ToscaTemplate toscaTemplate, EntityQuery entityQuery) {
        return new QueryProcessorBuilder(toscaTemplate, entityQuery);
    }

    public static class QueryProcessorBuilder {
        private final QueryProcessor queryProcessor;

        private QueryProcessorBuilder(ToscaTemplate toscaTemplate, EntityQuery entityQuery) {
            this.queryProcessor = new QueryProcessor(toscaTemplate, entityQuery);
        }

        QueryProcessorBuilder topologyTemplateQuery(TopologyTemplateQuery topologyTemplateQuery) {
            this.queryProcessor.setTopologyTemplateQuery(topologyTemplateQuery);
            return this;
        }

        public QueryProcessorBuilder setRecursiveFlag(boolean isRecursive) {
            this.queryProcessor.setRecursiveFlag(isRecursive);
            return this;
        }

        QueryProcessor build() {
            return queryProcessor;
        }


    }
}

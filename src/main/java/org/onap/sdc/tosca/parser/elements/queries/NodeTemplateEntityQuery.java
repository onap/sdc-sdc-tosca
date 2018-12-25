package org.onap.sdc.tosca.parser.elements.queries;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.elements.EntityDetailsFactory;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.ToscaTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate) {
        if (nodeTemplate.getSubMappingToscaTemplate() != null) {
            return convertNodeTemplatesLisToEntityDetailsList(filter(nodeTemplate.getSubMappingToscaTemplate()
                    .getNodeTemplates()));
        }
        return Lists.newArrayList();
    }

    @Override
    public List<IEntityDetails> getEntitiesFromService(ToscaTemplate toscaTemplate) {
        return convertNodeTemplatesLisToEntityDetailsList(filter(toscaTemplate.getNodeTemplates()));
    }

    static List<IEntityDetails> convertNodeTemplatesLisToEntityDetailsList(Stream<NodeTemplate> nodeTemplates) {
        return nodeTemplates
            .map(nt->EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, nt))
            .collect(Collectors.toList());
    }

    private Stream<NodeTemplate> filter(List<NodeTemplate> nodeTemplateList) {
        return nodeTemplateList.stream()
                .filter(nt->isSearchCriteriaMatched(nt.getMetaData(), nt.getType()))
                .filter(nt->isStringMatchingOrNull(nt.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE), getNodeTemplateType().getValue()));
    }


}

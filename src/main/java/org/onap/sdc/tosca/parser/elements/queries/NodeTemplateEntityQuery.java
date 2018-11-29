package org.onap.sdc.tosca.parser.elements.queries;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.elements.EntityDetailsFactory;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;

import java.util.List;
import java.util.stream.Collectors;

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
            return nodeTemplate.getSubMappingToscaTemplate()
                    .getNodeTemplates()
                    .stream()
                    .filter(nt->isSearchCriteriaMatched(nt.getMetaData(), nodeTemplate.getType()))
                    .filter(nt->isStringMatchingOrNull(nt.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE))
                            .test(getNodeTemplateType().getValue()))
                    .map(nt->EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, nt))
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }
}

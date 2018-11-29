package org.onap.sdc.tosca.parser.elements.queries;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.elements.EntityDetailsFactory;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements EntityQuery object for Policies
 */
public class PolicyEntityQuery extends EntityQuery {

    PolicyEntityQuery() {
        super(EntityTemplateType.POLICY, null, null);
    }

    PolicyEntityQuery(String toscaType) {
        super(EntityTemplateType.POLICY, null, toscaType);
    }

    @Override
    public List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate) {
        if (nodeTemplate.getOriginComponentTemplate() != null) {
            return nodeTemplate.getOriginComponentTemplate()
                    .getPolicies()
                    .stream()
                    .filter(p->isSearchCriteriaMatched(p.getMetaDataObj(), nodeTemplate.getType()))
                    .map(p->EntityDetailsFactory.createEntityDetails(EntityTemplateType.POLICY, p))
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }
}

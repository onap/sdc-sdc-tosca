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
 * Implements EntityQuery object for Groups
 */
public class GroupEntityQuery extends EntityQuery {

    GroupEntityQuery() {
        super(EntityTemplateType.GROUP, null, null);
    }

    @Override
    public List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate) {
        if (nodeTemplate.getOriginComponentTemplate() != null) {
            return nodeTemplate.getOriginComponentTemplate()
                    .getGroups()
                    .stream()
                    .filter(gr->isSearchCriteriaMatched(gr.getMetadata(), nodeTemplate.getType()))
                    .map(gr->EntityDetailsFactory.createEntityDetails(EntityTemplateType.GROUP, gr))
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    GroupEntityQuery(String toscaType) {
        super(EntityTemplateType.GROUP, null, toscaType);
    }
}

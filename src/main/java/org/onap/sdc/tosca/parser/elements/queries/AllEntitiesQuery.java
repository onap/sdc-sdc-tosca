package org.onap.sdc.tosca.parser.elements.queries;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.elements.EntityDetailsFactory;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class AllEntitiesQuery extends EntityQuery {

    AllEntitiesQuery() {
        super(EntityTemplateType.ALL, null, null);
    }

    @Override
    public List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate) {
        List<EntityTemplate> allEntities = Lists.newArrayList();
        if (nodeTemplate.getSubMappingToscaTemplate() != null) {
            allEntities.addAll(nodeTemplate.getSubMappingToscaTemplate().getGroups());
            allEntities.addAll(nodeTemplate.getSubMappingToscaTemplate().getNodeTemplates());
        }
        if (nodeTemplate.getOriginComponentTemplate() != null) {
            allEntities.addAll(nodeTemplate.getOriginComponentTemplate().getPolicies());
        }
        return allEntities.stream()
                    .map(e-> EntityDetailsFactory.createEntityDetails(EntityTemplateType.GROUP, e))
                    .collect(Collectors.toList());
    }


}

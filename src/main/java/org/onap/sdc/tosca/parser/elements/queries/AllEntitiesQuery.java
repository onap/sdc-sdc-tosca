package org.onap.sdc.tosca.parser.elements.queries;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.ToscaTemplate;

import java.util.List;

public class AllEntitiesQuery extends EntityQuery {

    AllEntitiesQuery() {
        super(EntityTemplateType.ALL, null, null);
    }

    @Override
    public List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate) {
        List<IEntityDetails> allEntities = Lists.newArrayList();
        if (nodeTemplate.getSubMappingToscaTemplate() != null) {
            allEntities.addAll(GroupEntityQuery.convertGroupLisToEntityDetailsList(nodeTemplate.getSubMappingToscaTemplate().getGroups().stream()));
            allEntities.addAll(NodeTemplateEntityQuery.convertNodeTemplatesLisToEntityDetailsList(nodeTemplate.getSubMappingToscaTemplate().getNodeTemplates().stream()));
        }
        if (nodeTemplate.getOriginComponentTemplate() != null) {
            allEntities.addAll(PolicyEntityQuery.convertPolicyLisToEntityDetailsList(nodeTemplate.getOriginComponentTemplate().getPolicies().stream()));
        }
        return allEntities;
    }


    @Override
    public List<IEntityDetails> getEntitiesFromService(ToscaTemplate toscaTemplate) {
        List<IEntityDetails> allEntities = Lists.newArrayList();
        allEntities.addAll(GroupEntityQuery.convertGroupLisToEntityDetailsList(toscaTemplate.getGroups().stream()));
        allEntities.addAll(NodeTemplateEntityQuery.convertNodeTemplatesLisToEntityDetailsList(toscaTemplate.getNodeTemplates().stream()));
        allEntities.addAll(PolicyEntityQuery.convertPolicyLisToEntityDetailsList(toscaTemplate.getPolicies().stream()));
        return allEntities;
    }

}



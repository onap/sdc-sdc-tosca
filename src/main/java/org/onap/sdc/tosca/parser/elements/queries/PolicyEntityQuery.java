package org.onap.sdc.tosca.parser.elements.queries;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;

import java.util.List;

/**
 * Implements EntityQuery object for Policies
 */
public class PolicyEntityQuery extends EntityQuery {

    public PolicyEntityQuery() {
        super(EntityTemplateType.POLICY, null, null);
    }

    @Override
    public List<EntityTemplate> searchByTopologyTemplate(TopologyTemplateQuery topologyTemplateQuery) {
        return null;
    }

    @Override
    public EntityTemplateType getType() {
        return EntityTemplateType.POLICY;
    }

    public PolicyEntityQuery(String toscaType) {
        super(EntityTemplateType.POLICY, null, toscaType);
    }
}

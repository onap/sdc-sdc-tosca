package org.onap.sdc.tosca.parser.elements.queries;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;

import java.util.List;

/**
 * Implements EntityQuery object for Groups
 */
public class GroupEntityQuery extends EntityQuery {

    public GroupEntityQuery() {
        super(EntityTemplateType.GROUP, null, null);
    }

    @Override
    public List<EntityTemplate> searchByTopologyTemplate(TopologyTemplateQuery topologyTemplateQuery) {
        return null;
    }

    @Override
    public EntityTemplateType getType() {
        return EntityTemplateType.GROUP;
    }

    public GroupEntityQuery(String toscaType) {
        super(EntityTemplateType.GROUP, null, toscaType);
    }
}

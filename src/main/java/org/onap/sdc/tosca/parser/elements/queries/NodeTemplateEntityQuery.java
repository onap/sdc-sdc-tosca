package org.onap.sdc.tosca.parser.elements.queries;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.toscaparser.api.EntityTemplate;

import java.util.List;

/**
 * Implements EntityQuery object for NodeTemplates
 */
public class NodeTemplateEntityQuery extends EntityQuery {
    public NodeTemplateEntityQuery() {
        super(EntityTemplateType.NODE_TEMPLATE, null, null);
    }

    @Override
    public EntityTemplateType getType() {
        return EntityTemplateType.NODE_TEMPLATE;
    }

    @Override
    public List<EntityTemplate> searchByTopologyTemplate(TopologyTemplateQuery topologyTemplateQuery) {
        return null;
    }

    public NodeTemplateEntityQuery(SdcTypes nodeTemplateType) {
        super(EntityTemplateType.NODE_TEMPLATE, nodeTemplateType, null);
    }

    public NodeTemplateEntityQuery(String toscaType) {
        super(EntityTemplateType.NODE_TEMPLATE, null, toscaType);
    }
}

package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;

public class NodeTemplateEntityDetails extends EntityDetails {

    private final NodeTemplate nodeTemplate;

    NodeTemplateEntityDetails(EntityTemplate entityTemplate, NodeTemplate parentNode) {
        super(entityTemplate, parentNode);
        nodeTemplate = (NodeTemplate)getEntityTemplate();
    }

    @Override
    public EntityTemplateType getType() {
        return EntityTemplateType.NODE_TEMPLATE;
    }

    @Override
    public Metadata getMetadata() {
        return nodeTemplate.getMetaData();
    }
}

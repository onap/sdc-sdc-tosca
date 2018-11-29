package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;

public class EntityDetailsFactory {

    public static EntityDetails createEntityDetails(EntityTemplateType entityTemplateType, EntityTemplate entityTemplate, NodeTemplate parent) {
        EntityDetails entityDetails = null;
        switch (entityTemplateType) {
            case NODE_TEMPLATE:
                entityDetails = new NodeTemplateEntityDetails(entityTemplate, parent);
                break;
            case POLICY:
                entityDetails = new PolicyEntityDetails(entityTemplate, parent);
                break;
            case GROUP:
                entityDetails = new GroupEntityDetails(entityTemplate, parent);
                break;
        }
        return entityDetails;
    }

}

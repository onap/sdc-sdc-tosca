package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;

public class EntityDetailsFactory {

    private EntityDetailsFactory(){}

    public static EntityDetails createEntityDetails(EntityTemplateType entityTemplateType, EntityTemplate entityTemplate) {
        EntityDetails entityDetails = null;
        if (entityTemplate != null && entityTemplateType != null) {
            switch (entityTemplateType) {
                case NODE_TEMPLATE:
                    entityDetails = new NodeTemplateEntityDetails(entityTemplate);
                    break;
                case POLICY:
                    entityDetails = new PolicyEntityDetails(entityTemplate);
                    break;
                case GROUP:
                    entityDetails = new GroupEntityDetails(entityTemplate);
                    break;
                default:
                    break;
            }
        }
        return entityDetails;
    }

}

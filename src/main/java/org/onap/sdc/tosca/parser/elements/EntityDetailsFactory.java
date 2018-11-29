package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;

public class EntityDetailsFactory {

    private EntityDetailsFactory(){}

    //TODO: the parent should be retrieved from the entityTemplate and not passed as a separate parameter
    public static EntityDetails createEntityDetails(EntityTemplateType entityTemplateType, EntityTemplate entityTemplate) {
        EntityDetails entityDetails = null;
        if (entityTemplate != null) {
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
            }
        }
        return entityDetails;
    }

}

package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Policy;
import org.onap.sdc.toscaparser.api.elements.Metadata;

public class PolicyEntityDetails extends EntityDetails {

    private final Policy policy;

    PolicyEntityDetails(EntityTemplate entityTemplate, NodeTemplate parentNode) {
        super(entityTemplate, parentNode);
        policy = (Policy)getEntityTemplate();
    }

    @Override
    public EntityTemplateType getType() {
        return EntityTemplateType.POLICY;
    }

    @Override
    public Metadata getMetadata() {
        return policy.getMetaDataObj();
    }
}

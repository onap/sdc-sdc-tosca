package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.Policy;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import java.util.List;
import java.util.stream.Collectors;

public class PolicyEntityDetails extends EntityDetails {

    private static final String NODE_TEMPLATES_TARGET_TYPE = "node_templates";

    private final Policy policy;

    PolicyEntityDetails(EntityTemplate entityTemplate) {
        super(entityTemplate);
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

    @Override
    public List<String> getTargets() {
        if (policy.getTargets() != null) {
            return policy.getTargets();
        }
        return super.getTargets();
    }

    @Override
    public List<IEntityDetails> getTargetNodes() {
        if (policy.getTargetsType().equals(NODE_TEMPLATES_TARGET_TYPE)) {
            return policy.getTargetsList()
                    .stream()
                    .map(o->EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, (EntityTemplate)o))
                    .collect(Collectors.toList());
        }
        return policy.getTargetsList()
                .stream()
                .map(o->EntityDetailsFactory.createEntityDetails(EntityTemplateType.GROUP, (EntityTemplate)o))
                .collect(Collectors.toList());
    }
}

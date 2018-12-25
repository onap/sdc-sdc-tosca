package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.Property;
import org.onap.sdc.toscaparser.api.RequirementAssignment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class EntityDetails implements IEntityDetails {

    private final EntityTemplate entityTemplate;
    private final IEntityDetails parentNodeTemplate;

    EntityDetails(EntityTemplate entityTemplate) {
        this.entityTemplate = entityTemplate;
        this.parentNodeTemplate = EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, entityTemplate.getParentNodeTemplate());
    }

    @Override
    public String getName() {
        return entityTemplate.getName();
    }

    EntityTemplate getEntityTemplate() {
        return entityTemplate;
    }

    @Override
    public Map<String, Property> getProperties() {
        return entityTemplate.getProperties();
    }

    @Override
    public List<Property> getPropertyList() {
        return entityTemplate.getPropertiesObjects();
    }

    @Override
    public List<IEntityDetails> getMemberNodes() {
        return Collections.emptyList();
    }

    @Override
    public IEntityDetails getParent() {
        return parentNodeTemplate;
    }

    @Override
    public Map<String, RequirementAssignment> getRequirements() {
        return entityTemplate.getRequirements()
                .getAll()
                .stream()
                .collect(Collectors.toMap(RequirementAssignment::getName, ra->ra));
    }

    @Override
    public Map<String, CapabilityAssignment> getCapabilities() {
        return entityTemplate.getCapabilities()
                .getAll()
                .stream()
                .collect(Collectors.toMap(CapabilityAssignment::getName, ca->ca));
    }

    @Override
    public List<String> getTargets() {
        return Collections.emptyList();
    }

    @Override
    public List<IEntityDetails> getTargetEntities() {
        return Collections.emptyList();
    }

    @Override
    public String getPath() {
        //todo - update after adding parent to the EntityTemplate class
        return "jenny vTSBC vlan VNF 0#abstract_ssc#ssc_ssc_avpn_port_0";
    }



}

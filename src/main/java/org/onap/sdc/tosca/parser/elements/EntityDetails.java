package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Property;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class EntityDetails implements IEntityDetails {

    private final EntityTemplate entityTemplate;

    //todo - remove after adding parent to the EntityTemplate class
    private final NodeTemplate parentNode;


    public EntityDetails(EntityTemplate entityTemplate, NodeTemplate parentNode) {
        this.entityTemplate = entityTemplate;
        this.parentNode = parentNode;
    }

    public EntityTemplate getEntityTemplate() {
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
    public List<NodeTemplate> getMemberNodes() {
        return Collections.emptyList();
    }

    @Override
    public NodeTemplate getParent() {
        //todo - update after adding parent to the EntityTemplate class
        return parentNode;
    }

    @Override
    public String getParentPath() {
        //todo - update after adding parent to the EntityTemplate class
        return parentNode.getMetaData().getValue("customizationUUID");
    }



}

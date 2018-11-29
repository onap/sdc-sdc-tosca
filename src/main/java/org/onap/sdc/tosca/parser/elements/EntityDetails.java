package org.onap.sdc.tosca.parser.elements;

import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EntityDetails implements IEntityDetails {

    private final EntityTemplate entityTemplate;

    private final IEntityDetails parentNode;

    EntityDetails(EntityTemplate entityTemplate, NodeTemplate parentNode) {
        this.entityTemplate = entityTemplate;
        this.parentNode = EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, parentNode, null);
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
        List<IEntityDetails> ntList = new ArrayList<>();
        ntList.add(getParent());
        return ntList;
    }

    @Override
    public IEntityDetails getParent() {
        //todo - update after adding parent to the EntityTemplate class
        return parentNode;
    }

    @Override
    public String getPath() {
        //todo - update after adding parent to the EntityTemplate class
        return "jenny vTSBC vlan VNF 0#abstract_ssc#ssc_ssc_avpn_port_0";
    }



}

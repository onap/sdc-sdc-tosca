package org.onap.sdc.tosca.parser.api;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.Property;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import java.util.List;
import java.util.Map;

public interface IEntityDetails {
    EntityTemplateType getType();
    String getName();
    Metadata getMetadata();
    Map<String, Property> getProperties();
    List<Property> getPropertyList();
    List<IEntityDetails> getMemberNodes();
    IEntityDetails getParent();
    String getPath();

}

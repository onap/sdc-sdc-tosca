package org.onap.sdc.tosca.parser.api;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.Property;
import org.onap.sdc.toscaparser.api.RequirementAssignment;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import java.util.List;
import java.util.Map;

public interface IEntityDetails {
    /**
     * Retrieves entity instance template type.
     * @return {@link EntityTemplateType} enum entry describing given object type
     */
    EntityTemplateType getType();

    /**
     * Retrieves entity instance name
     */
    String getName();

    /**
     * Retrieves entity {@link Metadata} object
     */
    Metadata getMetadata();

    /**
     * Retrieves entity instance properties
     * @return map of entity property names and corresponding {@link Property} object instances
     */
    Map<String, Property> getProperties();

    /**
     * Retrieves member nodes of the entity instance
     * @return List of member nodes entity objects
     */
    List<IEntityDetails> getMemberNodes();

    /**
     * Retrieves node template containing the current entity instance.
     * @return parent entity instance or null if the entity is contained by service
     */
    IEntityDetails getParent();

    /**
     * Retrieves path to the searched entity instance in the model. It is based on the collection of the node templates names delimited by #.
     * The entity instance may exist in the service node templates, in the VF node templates or in the nested of nested CVFC.
     * The path will include the VF name, then the names of the CVFC recursively.
     * If the entity instance is located in the service directly, the path is empty string
     */
    String getPath();

    /**
     * Retrieves map of requirements of the entity instance
     * @return map of entity requirement names and corresponding {@link RequirementAssignment} object instances
     */
    Map<String, RequirementAssignment> getRequirements();

    /**
     * Retrieves map of capabilities of the entity instance
     * @return map of entity capability names and corresponding {@link CapabilityAssignment} object instances
     */
    Map<String, CapabilityAssignment> getCapabilities();

    /**
     * Retrieves list of policy target names
     */
    List<String> getTargets();

    /**
     * Retrieves list of policy target entity instances (groups or node templates)
     */
    List<IEntityDetails> getTargetNodes();
}
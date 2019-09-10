/*-
 * ============LICENSE_START=======================================================
 * sdc-tosca
 * ================================================================================
 * Copyright (C) 2017 - 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.tosca.parser.api;

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.Property;
import org.onap.sdc.toscaparser.api.RequirementAssignment;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.onap.sdc.toscaparser.api.parameters.Input;

import java.util.List;
import java.util.Map;

public interface IEntityDetails {
    /**
     * Retrieves entity instance template type.
     * @return {@link EntityTemplateType} enum entry describing given object type
     */
    EntityTemplateType getEntityType();

    /**
     * Retrieves entity instance name
     */
    String getName();

    /**
     * Retrieves entity Tosca type
     */
    String getToscaType();

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
     * Retrieves member names of the entity instance
     * @return List of member names
     */
    List<String> getMembers();

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
    List<RequirementAssignment> getRequirements();

    /**
     * Retrieves map of capabilities of the entity instance
     * @return map of entity capability names and corresponding {@link CapabilityAssignment} object instances
     */
    List<CapabilityAssignment> getCapabilities();

    /**
     * Retrieves list of policy target names
     */
    List<String> getTargets();

    /**
     * Retrieves list of policy target entity instances (groups or node templates)
     */
    List<IEntityDetails> getTargetEntities();

    /**
     * Retrieves list of inputs
     */
    List<Input> getInputs();
}

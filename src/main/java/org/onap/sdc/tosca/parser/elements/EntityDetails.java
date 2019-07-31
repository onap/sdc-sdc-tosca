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
    public List<IEntityDetails> getMemberNodes() {
        return Collections.emptyList();
    }

    @Override
    public IEntityDetails getParent() {
        return parentNodeTemplate;
    }

    @Override
    public List<RequirementAssignment> getRequirements() {
        return entityTemplate.getRequirements()
                .getAll();
    }

    @Override
    public List<CapabilityAssignment> getCapabilities() {
        return entityTemplate.getCapabilities()
                .getAll();
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
        StringBuilder pathBld = new StringBuilder("");
        EntityTemplate currentEntityParent = entityTemplate.getParentNodeTemplate();

        while (currentEntityParent != null) {
            if (pathBld.length() != 0) {
                pathBld.insert(0,"#");
            }
            pathBld.insert(0, currentEntityParent.getName());
            currentEntityParent = currentEntityParent.getParentNodeTemplate();
        }
        return pathBld.toString();
    }

    @Override
    public String getToscaType() {
        return entityTemplate.getType();
    }

    @Override
    public List<String> getMembers() { return Collections.emptyList(); }



}

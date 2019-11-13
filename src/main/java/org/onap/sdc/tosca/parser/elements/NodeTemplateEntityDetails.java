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

import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.EntityTemplate;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.onap.sdc.toscaparser.api.parameters.Input;

import java.util.List;
import java.util.Objects;

public class NodeTemplateEntityDetails extends EntityDetails {

    private final NodeTemplate nodeTemplate;

    NodeTemplateEntityDetails(EntityTemplate entityTemplate) {
        super(entityTemplate);
        nodeTemplate = (NodeTemplate)getEntityTemplate();
    }

    @Override
    public EntityTemplateType getEntityType() {
        return EntityTemplateType.NODE_TEMPLATE;
    }

    @Override
    public Metadata getMetadata() {
        return nodeTemplate.getMetaData();
    }

    @Override
    public List<Input> getInputs(){
        if (nodeTemplate.getSubMappingToscaTemplate()!= null) {
            return nodeTemplate.getSubMappingToscaTemplate().getInputs();
        }
        return super.getInputs();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeTemplateEntityDetails)) return false;
        NodeTemplateEntityDetails that = (NodeTemplateEntityDetails) o;
        return nodeTemplate.equals(that.nodeTemplate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeTemplate);
    }
}
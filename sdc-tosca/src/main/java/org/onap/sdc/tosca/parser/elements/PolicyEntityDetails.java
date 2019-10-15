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
    public EntityTemplateType getEntityType() {
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
    public List<IEntityDetails> getTargetEntities() {
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

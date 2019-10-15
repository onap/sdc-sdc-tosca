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
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GroupEntityDetails extends EntityDetails {
    private final Group group;
    private final List<IEntityDetails> memberNodes;

    GroupEntityDetails(EntityTemplate entityTemplate)  {
        super(entityTemplate);
        group = (Group)getEntityTemplate();
        if (group.getMemberNodes() != null) {
            memberNodes = group.getMemberNodes()
                    .stream()
                    .map(m->EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, m))
                    .collect(Collectors.toList());
        }
        else {
            memberNodes = Collections.emptyList();
        }
    }

    @Override
    public EntityTemplateType getEntityType() {
        return EntityTemplateType.GROUP;
    }

    @Override
    public List<IEntityDetails> getMemberNodes() {
        return memberNodes;
    }

    @Override
    public Metadata getMetadata() {
        return group.getMetadata();
    }

    @Override
    public List<String> getMembers() {
        if (group.getMembers() != null) {
            return group.getMembers();
        }
        return super.getMembers();
    }
}

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

package org.onap.sdc.tosca.parser.elements.queries;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.elements.EntityDetailsFactory;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Policy;
import org.onap.sdc.toscaparser.api.ToscaTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements EntityQuery object for Policies
 */
public class PolicyEntityQuery extends EntityQuery {

    PolicyEntityQuery() {
        super(EntityTemplateType.POLICY, null, null);
    }

    PolicyEntityQuery(String toscaType) {
        super(EntityTemplateType.POLICY, null, toscaType);
    }

    @Override
    public List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate) {
        if (nodeTemplate.getOriginComponentTemplate() != null) {
            return convertPolicyLisToEntityDetailsList(filter(nodeTemplate.getOriginComponentTemplate().getPolicies()));
        }
        return Lists.newArrayList();
    }

    @Override
    public List<IEntityDetails> getEntitiesFromService(ToscaTemplate toscaTemplate) {
        return convertPolicyLisToEntityDetailsList(filter(toscaTemplate.getPolicies()));
    }

    static List<IEntityDetails> convertPolicyLisToEntityDetailsList(Stream<Policy> policies) {
        return policies
            .map(p->EntityDetailsFactory.createEntityDetails(EntityTemplateType.POLICY, p))
            .collect(Collectors.toList());
    }

    private Stream<Policy> filter(List<Policy> policyList) {
        return policyList.stream()
                .filter(p->isSearchCriteriaMatched(p.getMetaDataObj(), p.getType()));
    }

}

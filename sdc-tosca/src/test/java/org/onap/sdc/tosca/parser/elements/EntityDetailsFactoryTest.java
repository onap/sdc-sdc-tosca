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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Policy;

@ExtendWith(MockitoExtension.class)
public class EntityDetailsFactoryTest {

    @Mock
    private NodeTemplate nodeTemplate;

    @Mock
    private Group group;

    @Mock
    private Policy policy;


    @Test
    public void createNodeTemplateEntityDetailsWhenParentNodeIsNotNull() {
        when(nodeTemplate.getParentNodeTemplate())
                .thenReturn(nodeTemplate)
                .thenReturn(null);
        EntityDetails entityDetails = EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, nodeTemplate);
        assertTrue(entityDetails instanceof NodeTemplateEntityDetails);
        assertTrue(entityDetails.getParent() instanceof NodeTemplateEntityDetails);
    }

    @Test
    public void createNodeTemplateEntityDetailsWhenParentNodeIsNull() {
        EntityDetails entityDetails = EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, nodeTemplate);
        assertTrue(entityDetails instanceof NodeTemplateEntityDetails);
        assertEquals(null, entityDetails.getParent());
    }

    @Test
    public void createNodeTemplateEntityDetailsWhenNnIsNull() {
        assertEquals(null, EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, null));
    }

    @Test
    public void createGroupEntityDetailsWhenParentNodeIsNotNull() {
        when(group.getParentNodeTemplate())
                .thenReturn(nodeTemplate)
                .thenReturn(null);
        EntityDetails entityDetails = EntityDetailsFactory.createEntityDetails(EntityTemplateType.GROUP, group);
        assertTrue(entityDetails instanceof GroupEntityDetails);
        assertTrue(entityDetails.getParent() instanceof NodeTemplateEntityDetails);
    }

    @Test
    public void createGroupEntityDetailsWhenParentNodeIsNull() {
        EntityDetails entityDetails = EntityDetailsFactory.createEntityDetails(EntityTemplateType.GROUP, group);
        assertTrue(entityDetails instanceof GroupEntityDetails);
        assertEquals(null, entityDetails.getParent());
    }

    @Test
    public void createGroupEntityDetailsWhenNnIsNull() {
        assertEquals(null, EntityDetailsFactory.createEntityDetails(EntityTemplateType.GROUP, null));
    }

    @Test
    public void createPolicyEntityDetailsWhenParentNodeIsNotNull() {
        when(policy.getParentNodeTemplate())
                .thenReturn(nodeTemplate)
                .thenReturn(null);
        EntityDetails entityDetails = EntityDetailsFactory.createEntityDetails(EntityTemplateType.POLICY, policy);
        assertTrue(entityDetails instanceof PolicyEntityDetails);
        assertTrue(entityDetails.getParent() instanceof NodeTemplateEntityDetails);
    }

    @Test
    public void createPolicyEntityDetailsWhenParentNodeIsNull() {
        EntityDetails entityDetails = EntityDetailsFactory.createEntityDetails(EntityTemplateType.POLICY, policy);
        assertTrue(entityDetails instanceof PolicyEntityDetails);
        assertEquals(null, entityDetails.getParent());
    }

    @Test
    public void createPolicyEntityDetailsWhenNnIsNull() {
        assertEquals(null, EntityDetailsFactory.createEntityDetails(EntityTemplateType.POLICY, null));
    }

    @Test
    public void createWrongEntityDetails() {
        assertThrows(ClassCastException.class, () -> {
            EntityDetailsFactory.createEntityDetails(EntityTemplateType.POLICY, group);
        });
    }


    @Test
    public void createEntityDetailsWhenTypeIsNull() {
        assertEquals(null, EntityDetailsFactory.createEntityDetails(null, group));
    }

}

package org.onap.sdc.tosca.parser.elements;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

    @Test(expected = ClassCastException.class)
    public void createWrongEntityDetails() {
        EntityDetailsFactory.createEntityDetails(EntityTemplateType.POLICY, group);
    }


    @Test
    public void createEntityDetailsWhenTypeIsNull() {
        assertEquals(null, EntityDetailsFactory.createEntityDetails(null, group));
    }

}

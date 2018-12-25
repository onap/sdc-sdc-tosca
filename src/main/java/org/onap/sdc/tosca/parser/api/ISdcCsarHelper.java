/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.sdc.tosca.parser.api;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.enums.FilterType;
import org.onap.sdc.toscaparser.api.*;
import org.onap.sdc.toscaparser.api.elements.InterfacesDef;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.onap.sdc.toscaparser.api.parameters.Input;


public interface ISdcCsarHelper {

	/**
	 * Get all node templates by node_type for this CSAR service.
	 *  
	 * @param nodeType - the TOSCA type of the node.
	 * @return service node templates of this type.
	 */
	@Deprecated
	public List<NodeTemplate> getServiceNodeTemplatesByType(String nodeType);

	/**
	 * Get all node templates for this CSAR service.
	 *
	 * @return service node templates.
	 */
	@Deprecated
	public List<NodeTemplate> getServiceNodeTemplates();

	/**
	 * Get groups of a VF with type "org.onap.groups.VfModule".
	 * 
	 * @param vfCustomizationUuid - customizationUuid of VF instance.
	 * @return list of vfModule groups.
	 */
	@Deprecated
	public List<Group> getVfModulesByVf(String vfCustomizationUuid);


	/**
	 * Get any property leaf value for node template by full path separated by #.<br>
	 * For example, for node template with this property:<br><br>
	 *   network_assignments:<br>
          &nbsp;&nbsp;ecomp_generated_network_assignment: true<br>
          &nbsp;&nbsp;is_shared_network: false<br>
          &nbsp;&nbsp;is_external_network: false<br>
          &nbsp;&nbsp;ipv4_subnet_default_assignments:<br>
            &nbsp;&nbsp;&nbsp;&nbsp;use_ipv4: true<br>
            &nbsp;&nbsp;&nbsp;&nbsp;ip_network_address_plan: 1.2.3.4<br>
            &nbsp;&nbsp;&nbsp;&nbsp;dhcp_enabled: true<br>
            &nbsp;&nbsp;&nbsp;&nbsp;ip_version: 4<br>
            &nbsp;&nbsp;&nbsp;&nbsp;cidr_mask: 24<br>
            &nbsp;&nbsp;&nbsp;&nbsp;min_subnets_count: 1<br>
          &nbsp;&nbsp;ipv6_subnet_default_assignments:<br>
            &nbsp;&nbsp;&nbsp;&nbsp;use_ipv6: false<br><br>
            
	 * calling<br> 
	 * getNodeTemplatePropertyLeafValue(nodeTemplate, "network_assignments#ipv6_subnet_default_assignments#use_ipv6")<br> 
	 * will return "false".
	 * @param nodeTemplate - nodeTemplate where the property should be looked up.
	 * @param pathToPropertyLeafValue - the full path of the required property.
	 * @return the leaf value as String, or null if there's no such property, or it's not a leaf.
	 */
	@Deprecated
	public String getNodeTemplatePropertyLeafValue(NodeTemplate nodeTemplate, String pathToPropertyLeafValue);

	/**
	 * Get any property leaf value for node template by full path separated by #.<br>
	 * For example, for node template with this property:<br><br>
	 *   network_assignments:<br>
	 &nbsp;&nbsp;ecomp_generated_network_assignment: true<br>
	 &nbsp;&nbsp;is_shared_network: false<br>
	 &nbsp;&nbsp;is_external_network: false<br>
	 &nbsp;&nbsp;ipv4_subnet_default_assignments:<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;use_ipv4: true<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;ip_network_address_plan: 1.2.3.4<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;dhcp_enabled: true<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;ip_version: 4<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;cidr_mask: 24<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;min_subnets_count: 1<br>
	 &nbsp;&nbsp;ipv6_subnet_default_assignments:<br>
	 &nbsp;&nbsp;&nbsp;&nbsp;use_ipv6: false<br><br>

	 * calling<br>
	 * getNodeTemplatePropertyLeafValue(nodeTemplate, "network_assignments#ipv6_subnet_default_assignments#use_ipv6")<br>
	 * will return "false".
	 * @param nodeTemplate - nodeTemplate where the property should be looked up.
	 * @param pathToPropertyLeafValue - the full path of the required property.
	 * @return the leaf value as Object, or null if there's no such property. It's up to the caller to cast it to a proper type.
	 */
	@Deprecated
	public Object getNodeTemplatePropertyValueAsObject(NodeTemplate nodeTemplate, String pathToPropertyLeafValue);

	/**
	 * Get any property leaf value for a group definition by full path separated by #.
	 * Same logic as in {@link #getNodeTemplatePropertyLeafValue(NodeTemplate, String) getNodeTemplatePropertyLeafValue}, only for a group.
	 * @param group - group where the property should be looked up.
	 * @param propertyName - the name of the required property.
	 * @return the leaf value as String, or null if there's no such property, or it's not a leaf.
	 */
	@Deprecated
	public String getGroupPropertyLeafValue(Group group, String propertyName);

	/**
	 * Get any property value for a group definition by full path separated by #.
	 * Same logic as in {@link #getNodeTemplatePropertyLeafValue(NodeTemplate, String) getNodeTemplatePropertyLeafValue}, only for a group.
	 * @param group - group where the property should be looked up.
	 * @param propertyName - the name of the required property.
	 * @return the leaf value as Object, or null if there's no such property. It's up to the caller to cast it to a proper type.
	 */
	@Deprecated
	public Object getGroupPropertyAsObject(Group group, String propertyName);

	/**
	 * Get all VL node templates of the CSAR service.
	 * @return - all VL node templates.
	 */
	@Deprecated
	public List<NodeTemplate> getServiceVlList();

	/**
	 * Get all VF node templates of the CSAR service.
	 * @return - all VF node templates.
	 */
	@Deprecated
	public List<NodeTemplate> getServiceVfList();


	/**
	 * 
	 * Get a property from a metadata object.<br>
	 * This is just sugaring method, same as calling metadata.getMetadataPropertyValue(metadataPropertyName).<br>
	 * 
	 * For metadata object representing the below: <br><br>
	 * 
	 *  metadata:<br>
        &nbsp;&nbsp;invariantUUID: 4598a404-00e1-42a6-8767-0bda343e2066<br>
        &nbsp;&nbsp;UUID: e17940d6-42f8-4989-bad0-31de5addc619<br>
        &nbsp;&nbsp;customizationUUID: 83d086b2-a861-4d3b-aa84-3bfbb9b2ec20<br>
        &nbsp;&nbsp;version: '0.1'<br>
        &nbsp;&nbsp;name: vIPR_ATM<br>
        &nbsp;&nbsp;description: vIPR_ATM<br>
        &nbsp;&nbsp;type: VF<br>
        &nbsp;&nbsp;category: category1<br>
        &nbsp;&nbsp;subcategory: subCategory1<br><br>
        
        calling<br> 
        getMetadataPropertyValue(metadata,"invariantUUID")<br>
        will return "4598a404-00e1-42a6-8767-0bda343e2066".
        
	 * @param metadata - metadata object.
	 * @param metadataPropertyName - the name of the metadata property.
	 * @return metadata property value
	 */
	@Deprecated
	public String getMetadataPropertyValue(Metadata metadata, String metadataPropertyName);
	
	
	/**
	 * Get input leaf value for the CSAR service, by full path separated by #.<br>
	 * Same logic as in {@link #getNodeTemplatePropertyLeafValue(NodeTemplate, String) getNodeTemplatePropertyLeafValue}, only for an input full path.
	 * The expected format is "input_name#default[optionally #rest_of_path]"
	 * @param inputLeafValuePath by full path separated by #.
	 * @return input leaf value for the service.
	 */
	public String getServiceInputLeafValueOfDefault(String inputLeafValuePath);

	/**
	 * Get input leaf value for the CSAR service, by full path separated by #.<br>
	 * Same logic as in {@link #getNodeTemplatePropertyLeafValue(NodeTemplate, String) getNodeTemplatePropertyLeafValue}, only for an input full path.
	 * The expected format is "input_name#default[optionally #rest_of_path]"
	 * @param inputLeafValuePath by full path separated by #.
	 * @return input value for the service as Object. It's up to the caller to cast it to a proper type.
	 */
	public Object getServiceInputLeafValueOfDefaultAsObject(String inputLeafValuePath);

	/**
	 * Get the type name of the CSAR service's substitution mappings element.<br> 
	 * 
	 * For the below:<br><br>
	 * 
	 * substitution_mappings:<br>
       &nbsp;&nbsp;type: org.onap.services.ViprATM<br>

    	calling<br> 
    	getServiceSubstitutionMappingsTypeName()<br>
    	 will return "org.onap.services.ViprATM"
	 * @return - the type name of the CSAR service's substitution mappings element
	 */
	public String getServiceSubstitutionMappingsTypeName();
	
	/**
	 * Get service Metadata object.<br>
	 * This object represents the "metadata" section of a CSAR service.
	 * @return - the service Metadata object.
	 */
	public Metadata getServiceMetadata();

	/**
	 * Get the CSAR service metadata as map.
	 * @return - the service metadata object as Map.
	 * @deprecated This function is deprecated since its not tosca compliant. <br>
	 * Tosca defines the Metadata section as map of string (not map of object).<br>
	 *  This function is targeted to be removed as part of 1802.<br>
	 * Please use {@link #getServiceMetadataAllProperties() getServiceMetadataAllProperties()}.
	 */
	@Deprecated
	public Map<String, Object> getServiceMetadataProperties();

	/**
	 * Get the CSAR service metadata as map
	 * @return - the service metadata object as Map
	 */
	public Map<String, String> getServiceMetadataAllProperties();

	/**
	 * Get all VFC node templates from a specified VF.
	 * @param vfCustomizationId - customizationUuid of the VF node template.
	 * @return all VFC node templates from a specified VF
	 */
	@Deprecated
	public List<NodeTemplate> getVfcListByVf(String vfCustomizationId);
	
	/**
	 * Get all CP node templates from a specified VF.
	 * @param vfCustomizationId - customizationUuid of the VF node template.
	 * @return all CP node templates from a specified VF
	 */
	@Deprecated
	public List<NodeTemplate> getCpListByVf(String vfCustomizationId);
	
	/**
	 * Get all members of this group definition.<br>
	 * 
	 * For example, for this group definition:<br><br>
	 * 
	 *   ViprAtm..vIPR-ATM-Base..module-0:<br>   
      &nbsp;&nbsp;type: org.onap.groups.VfModule<br>      
      &nbsp;&nbsp;.................<br>
      &nbsp;&nbsp;members: [vIPR_ATM_Ha_Two, vIPR_ATM_Ha_One, vIPR_ATM_OAM_SG, vIPR_ATM_HA_TWO_SG, vIPR_ATM_HA_ONE_SG]<br><br>
      
      calling<br> 
      getMembersOfVfModule(NoteTemplate vfNodeTemplate, Group group)<br>
      will return List of the following Node templates in the vfNodeTemplate: "vIPR_ATM_Ha_Two, vIPR_ATM_Ha_One, vIPR_ATM_OAM_SG, vIPR_ATM_HA_TWO_SG, vIPR_ATM_HA_ONE_SG"<br>
	 * @param vf - VF to return the node templates from.
	 * @param vfModule - group to return the members from.
	 * @return node templates from vf with the names as in members section.
     * 
	 */
	@Deprecated
	public List<NodeTemplate> getMembersOfVfModule(NodeTemplate vf, Group vfModule);
	
	
	/**
	 * Get list of node template pairs, where for each pair,<br> 
	 * the left node template in pair has requirement with name reqName, <br>
	 * which should be satisfied with respective capability by the right node template in pair.<br>
	 * 
	 * For example, if we have the below two node templates in the vIPR VF:<br><br>
	 * 
	 * oam_extCP:<br>
      &nbsp;&nbsp;type: org.onap.resources.cp.extCP<br> 
      &nbsp;&nbsp;requirements:<br>
        &nbsp;&nbsp;&nbsp;&nbsp;- virtualBinding: vipr_atm_firewall<br><br>
	 * 
	 * vipr_atm_firewall: <br>
      &nbsp;&nbsp;type: org.onap.resources.vfc.ViprAtm.abstract.nodes.heat.vipr_atm<br>
      ........<br><br>
	 * 
        
     * calling<br>
     * getNodeTemplatePairsByReqName(getCpListByVf(viprCustomUuid), getVfcListByVf(viprCustomUuid), "virtualBinding")<br>
     * will return a list with one Pair - where left element of pair will be "oam_extCP" node template,<br>
     * and right element will be "vipr_atm_firewall" node template.<br>
     * 
	 * @param listOfReqNodeTemplates - list of node templates in which the "reqName" requirement should be looked.
	 * @param listOfCapNodeTemplates - list of node templates in which the capability matching the "reqName" requirement should be looked.
	 * @param reqName - the name of a requirement definition to match by.
	 * @return pairs of node templates according to described above.
	 */
	public List<Pair<NodeTemplate,NodeTemplate>> getNodeTemplatePairsByReqName(List<NodeTemplate> listOfReqNodeTemplates, List<NodeTemplate> listOfCapNodeTemplates, String reqName);
	
	/**
	 * Get all allotted node templates from this service.
	 * @return all allotted node templates from this service.
	 */
	@Deprecated
	public List<NodeTemplate> getAllottedResources();
	
	/**
	 * Get node_type of a node template.<br>
	 * 
	 * For this node template:<br>
	 * 
	 * vipr_atm_firewall: <br>
      &nbsp;&nbsp;type: org.onap.resources.vfc.ViprAtm.abstract.nodes.heat.vipr_atm<br>
      ........<br><br>
     * 
     * the function will return "org.onap.resources.vfc.ViprAtm.abstract.nodes.heat.vipr_atm"
     *  
	 * @param nodeTemplate - node template object
	 * @return - node type string.
	 */
	@Deprecated
	public String getTypeOfNodeTemplate(NodeTemplate nodeTemplate);

	/**
	 * Get the CSAR service inputs list.
	 * @return - the service inputs list.
	 */
	public List<Input> getServiceInputs();

	
	/**
	 * Get the conformance level of this CSAR. <br>
	 * The conformance level value of the CSAR is located in csar.meta file at the top level of the CSAR file.<br>
	 * For 1707 CSARs, the conformance level is 3.0.
	 * @return the conformance level of the CSAR. 
	 */
	public String getConformanceLevel();
	
	
	/**
	 * Get the map of CP-related props from a VFC node template. <br>
	 * Let's say there are 5 CPs related to this VFC. Then the output will look like this: <br><br>
	 * {port_fe1_sigtran={ip_requirements#ip_count_required#count=1, ip_requirements#dhcp_enabled=true, ip_requirements#ip_version=4, subnetpoolid="subnet_1", network_role_tag="SIGNET_vrf_B1_direct"},<br> 
	 *  port_fe_cluster={ip_requirements#ip_count_required#count=2, ip_requirements#dhcp_enabled=true, ip_requirements#ip_version=4},<br>
	 *  port_fe_slan={ip_requirements#ip_count_required#count=1, ip_requirements#dhcp_enabled=true, ip_requirements#ip_version=4},<br> 
	 *  port_fe_interce={ip_requirements#ip_count_required#count=1, ip_requirements#dhcp_enabled=true, ip_requirements#ip_version=4},<br> 
	 *  port_fe_oam={ip_requirements#ip_count_required#count=2, ip_requirements#dhcp_enabled=true, ip_requirements#ip_version=4, subnetpoolid="subnet_2", network_role_tag="Mobility_OAM_protected"}}<br><br>
	 * @param vfc - VFC node template to look for CP-related props.
	 * @return map <b>CP node template name</b>  to a map of <b>full path to a property on this CP</b> - <b> value of this property on this CP</b>.
	 * @deprecated This function is deprecated since its flattened form doesn't provide solution for cp properties of type List.
	 * Will be removed in 1802.
	 */
	@Deprecated 
	public Map<String, Map<String, Object>> getCpPropertiesFromVfc(NodeTemplate vfc);
	
    /**
    * Get the map of CP-related props from a VFC node template. <br>
    * Let's say there are 2 CPs (ports) related to this VFC. Then the output will look like this: <br><br>
    * {port_fe_sigtran={ip_requirements={ip_count_required: {count: 1}, dhcp_enabled: true, ip_version: 4}, subnetpoolid: "subnet_1", network_role_tag: "SIGNET_vrf_B1_direct"}<br>
    *  port_fe_cluster={ip_requirements={ip_count_required: {count: 2}, dhcp_enabled: true, ip_version: 4}}<br>
    * @param vfc - VFC node template to look for CP-related props.
    * @return map <b>CP node template name</b>  to a map of <b>property name</b> - <b> property value as object</b>.
    */
    @Deprecated
    public Map<String, Map<String, Object>> getCpPropertiesFromVfcAsObject(NodeTemplate vfc);
	
	/**
	 * Get customization UUID of a node template
	 * @param nt - node template
	 * @return customization UUID of a node template.
	 */
	@Deprecated
	public String getNodeTemplateCustomizationUuid(NodeTemplate nt);

    /**
     * Filter Node Template property values by equals/contains operator and a pattern
     * @param nodeTemplate Node Template to filter its properties
     * @param filterType filter type - equals or contains
     * @param pattern value to filter with it
     * @return Map <b>full path to a property</b> mapped to <b>property value</b> filtered by type and pattern
     */
    public Map<String, String> filterNodeTemplatePropertiesByValue(NodeTemplate nodeTemplate, FilterType filterType, String pattern);
    
	/**
	 * Get all node templates by sdcType for parent Node Template.
	 *
	 * @param parentNodeTemplate - parent node template
	 * @param sdcType - the SDC type of the node.
	 * @return node templates of this SDC type.
	 */
	@Deprecated
	public List<NodeTemplate> getNodeTemplateBySdcType(NodeTemplate parentNodeTemplate, SdcTypes sdcType);

	/**
	 * Get all node templates by SDC type enum for this CSAR service.
	 *
	 * @param sdcType - the SDC type of the node (for example, CP, VF...).
	 * @return service node templates of this SDC type.
	 */
	@Deprecated
	public List<NodeTemplate> getServiceNodeTemplateBySdcType(SdcTypes sdcType);
	
	/**
	 * Get all node templates  for this CSAR service.
	 * @param vfCustomizationUuid - the Customization UUID of the node.
	 * @return VNF Configuration Node Template.
	 */
	@Deprecated
	public NodeTemplate getVnfConfig(String vfCustomizationUuid);

	/**
	 * Check if Node Template has Topology Template
	 * @param nodeTemplate - Node Template to check
	 * @return true if node template has topology template, false if not.
	 */
	@Deprecated
	public boolean hasTopology(NodeTemplate nodeTemplate);

	/**
	 * Get children node templates for node template.
	 * @param nodeTemplate - Node Template to get its children
	 * @return return list of children node templates for node template.
	 */
	@Deprecated
	public List<NodeTemplate> getNodeTemplateChildren(NodeTemplate nodeTemplate);

	/**
	 * Get node template on service level by node template name.
	 * @param nodeName - the name of the node template.
	 * @return service-level node template with this name, or null if no such node template was found.
	 */
	@Deprecated
	public NodeTemplate getServiceNodeTemplateByNodeName(String nodeName);

	/**
	 * Get node template Metadata object.<br>
	 * This object represents the "metadata" section of node template.
	 * @param nt - Node template to get its Metadata object.
	 * @return Metadata for this node template, or null if not found.
	 */
	@Deprecated
	public Metadata getNodeTemplateMetadata(NodeTemplate nt);

	/**
	 * Get CapabilityAssignments object for this node template.<br>
	 * This should be an entry point function for working with capability assignments of node template.<br>
	 * This object allows filtering capability assignments objects.<br>
	 * @param nt - Node Template to get its capability assignments.
	 * @return CapabilitiesAssignments that contains list of capability assignments for the node template.<br>
	 * If none found, an empty list will be returned.
	 */
	@Deprecated
	public CapabilityAssignments getCapabilitiesOf(NodeTemplate nt);

	/**
	 * Get RequirementAssignments object for this node template.<br>
	 * This should be an entry point function for working with requirement assignments of node template.<br>
	 * This object allows filtering requirement assignments objects.<br>
	 * @param nt - Node Template to get its requirement assignments.
	 * @return RequirementAssignments that contains list of requirement assignments for the node template.
	 * If none found, an empty list will be returned.
	 */
	@Deprecated
	public RequirementAssignments getRequirementsOf(NodeTemplate nt);

	/**
	 * Get any property leaf value for capability by full path separated by #.
	 * Same logic as in {@link #getNodeTemplatePropertyLeafValue(NodeTemplate, String) getNodeTemplatePropertyLeafValue}, only for a capability assignment.
	 * @param capability - capability assignment where the property should be looked up.
	 * @param pathToPropertyLeafValue - the full path of the required property.
	 * @return the leaf value as String, or null if there's no such property, or it's not a leaf.
	 */
	@Deprecated
	public String getCapabilityPropertyLeafValue(CapabilityAssignment capability, String pathToPropertyLeafValue);
	
	/**
	 * Get all the policies of the main topology template (either VF or service)
	 * @return	the list of the policies
	 */
	@Deprecated
	public List<Policy> getPoliciesOfTopologyTemplate();
	
	/**
	 * Get all the policies of the main topology template (either VF or service) specified by policy type
	 * @param policyTypeName	the name of the policy type
	 * @return					the list of the policies						
	 */
	@Deprecated
	public List<Policy> getPoliciesOfTopologyTemplateByToscaPolicyType(String policyTypeName);
	
	/**
	 * Get all the policies of the origin component (nested topology template) of the node template
	 * @param nodeTemplate	the node template
	 * @return				the list of the policies
	 */
	@Deprecated
	public List<Policy> getPoliciesOfOriginOfNodeTemplate(NodeTemplate nodeTemplate);
	
	/**
	 * Get all the policies of the origin component (nested topology template) of the node template specified by policy type
	 * @param nodeTemplate		the node template
	 * @param policyTypeName	the name of the policy type
	 * @return					the list of the policies
	 */
	@Deprecated
	List<Policy> getPoliciesOfOriginOfNodeTemplateByToscaPolicyType(NodeTemplate nodeTemplate, String policyTypeName);
	
	/**
	 * Get all the node templates of the topology template, which are the targets of the policy specified by name
	 * @param policyName	the name of the policy
	 * @return				the list of the node templates
	 */
	@Deprecated
	List<NodeTemplate> getPolicyTargetsFromTopologyTemplate(String policyName);
	
	/**
	 * Get all the node templates of the origin component (nested topology template) of node template, which are the targets of the policy specified by name
	 * @param nodeTemplate	the node template
	 * @param policyName	the name of the policy
	 * @return				the list of the node templates
	 */
	@Deprecated
	List<NodeTemplate> getPolicyTargetsFromOrigin(NodeTemplate nodeTemplate, String policyName);
	
	/**
	 * Get the node template of the topology template specified by name
	 * @param nodeTemplateName	the name of the node template
	 * @return					the node template
	 */
	@Deprecated
	NodeTemplate getNodeTemplateByName(String nodeTemplateName);
    
    /**
     * Get all the policies, which contain the specified node template as a target
     * @param targetNode	the node template
     * @return				the list of the policies
     */
    @Deprecated
	List<Policy> getPoliciesOfTarget(NodeTemplate targetNode);
	
	/**
	 * Get all the policies of the specified type, which contain the specified node template as a target
	 * @param nodeTemplate		the node template
	 * @param policyTypeName	the name of the policy type
	 * @return					the list of the policies
	 */
	@Deprecated
    List<Policy> getPoliciesOfTargetByToscaPolicyType(NodeTemplate nodeTemplate, String policyTypeName);

    /**
     * Get all the groups of the origin component (nested topology template) of the node template
     * @param               nodeTemplate  the node template
     * @return              the list of the groups
     */
    @Deprecated
    ArrayList<Group> getGroupsOfOriginOfNodeTemplate(NodeTemplate nodeTemplate);
    
    /**
     * Get all groups of this of the main topology template (either VF or service) by specified tosca group type
     * @param groupType     the group type
     * @return              the list of the groups
     */
    @Deprecated
    ArrayList<Group> getGroupsOfTopologyTemplateByToscaGroupType(String groupType);
    
    /**
     * Get all groups of this of the main topology template (either VF or service)
     * @return              the list of the groups
     */
    @Deprecated
    ArrayList<Group> getGroupsOfTopologyTemplate();
    
    /**
     * Get all groups of this of the origin component (nested topology template) of the node template by specified tosca group type
     * @param nodeTemplate  the node template
     * @param groupType     the group type
     * @return              the list of the groups
     */
    @Deprecated
     ArrayList<Group> getGroupsOfOriginOfNodeTemplateByToscaGroupType(NodeTemplate nodeTemplate, String groupType);
    
    /**
     * Get members of the group belongs to the main topology template (either VF or service) by group name
     * @param groupName     the name of the group
     * @return              the list of the node templates
     */
    @Deprecated
    List<NodeTemplate> getGroupMembersFromTopologyTemplate(String groupName);
    
    /**
     * Get members of the group belongs to the origin component (nested topology template) of the node template by group name
     * @param nodeTemplate  the node template
     * @param groupName     the name of the group
     * @return              the list of the node templates
     */
    @Deprecated
    List<NodeTemplate> getGroupMembersOfOriginOfNodeTemplate(NodeTemplate nodeTemplate, String groupName);
    
    /**
     * Get inputs of the topology template including existing annotations
     * @return				the list of the inputs
     */
    List<Input> getInputsWithAnnotations();

	/**
	 * Get all interface details for given node template.<br>
	 * @return Map that contains the list of all interfaces and their definitions.
	 * If none found, an empty map will be returned.
	 */
	Map<String, List<InterfacesDef>> getInterfacesOf(NodeTemplate nt);

	/**
	 * Get all interface names for given node template.<br>
	 * @return List that contains the name of all interfaces.
	 * If none found, an empty list will be returned.
	 */
	public List<String> getInterfaces(NodeTemplate nt);

	/**
	 * Get all details for given node template and interface name.<br>
	 * @return List that contains the definitions of given interface name.
	 * If none found, an empty list will be returned.
	 */
	List<InterfacesDef> getInterfaceDetails(NodeTemplate nt, String interfaceName);

	/**
	 * Get all operation names for given node template and interface name.<br>
	 * @return List that contains the name of all operations for a given node template and interface name.
	 * If none found, an empty list will be returned.
	 */
	List<String> getAllInterfaceOperations(NodeTemplate nt, String interfaceName);

	/**
	 * Get interface details for a given node template, interface name and operation name.<br>
	 * @return InterfaceDef representing the operation details.
	 * If none found, null will be returned.
	 */
	InterfacesDef getInterfaceOperationDetails(NodeTemplate nt, String interfaceName, String operationName);

	/**
	 * Get property value for a property of given node template.<br>
	 * @param propertyNamePath valid name of property for search.<br>
	 *        To find value in the datatype with datatype entry schema, the property name should be defined with # delimiter
	 * @param nodeTemplatePath path to the model node template that property value will be searched.<br>
	 *        Path is based on the collection of the node templates names delimited by #.
	 * @return List of property values. If none found, empty list will be returned.
	 */
	@Deprecated
	List<String> getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath(String propertyNamePath, String nodeTemplatePath);

	boolean isNodeTypeSupported(NodeTemplate nodeTemplate);

	/**
	 * Retrieves details of one or more entity templates according to provided query parameters from corresponding topology template
	 * @param entityQuery Object describing the searched entity parameters. Includes one of following parameters: entity type,
	 *                    SDC (node template) type, tosca type as well as optional customizationUUID and UUID
	 * @param topologyTemplateQuery parameters of the topology template containing the above entity.
	 *                  Includes SDC type of the container and optional customizationUUID
	 * @param isRecursive indicates if the search is recursive or not
	 * @return list of @{@link IEntityDetails} objects containing information about the found entities.
	 * If either no entities found or the provided query is incorrect, an empty list is returned
	 */
	List<IEntityDetails> getEntity(EntityQuery entityQuery, TopologyTemplateQuery topologyTemplateQuery, boolean isRecursive);
}
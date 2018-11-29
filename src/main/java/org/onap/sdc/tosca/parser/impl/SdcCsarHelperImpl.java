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
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.tosca.parser.impl;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.config.ConfigurationManager;
import org.onap.sdc.tosca.parser.elements.EntityDetailsFactory;
import org.onap.sdc.tosca.parser.elements.NodeTemplateEntityDetails;
import org.onap.sdc.tosca.parser.elements.queries.TopologyTemplateQuery;
import org.onap.sdc.tosca.parser.elements.queries.EntityQuery;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.FilterType;
import org.onap.sdc.tosca.parser.enums.PropertySchemaType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.utils.GeneralUtility;
import org.onap.sdc.tosca.parser.utils.PropertyUtils;
import org.onap.sdc.tosca.parser.utils.SdcToscaUtility;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.CapabilityAssignments;
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Policy;
import org.onap.sdc.toscaparser.api.Property;
import org.onap.sdc.toscaparser.api.RequirementAssignment;
import org.onap.sdc.toscaparser.api.RequirementAssignments;
import org.onap.sdc.toscaparser.api.SubstitutionMappings;
import org.onap.sdc.toscaparser.api.TopologyTemplate;
import org.onap.sdc.toscaparser.api.ToscaTemplate;
import org.onap.sdc.toscaparser.api.elements.InterfacesDef;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.onap.sdc.toscaparser.api.elements.NodeType;
import org.onap.sdc.toscaparser.api.functions.Function;
import org.onap.sdc.toscaparser.api.parameters.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdcCsarHelperImpl implements ISdcCsarHelper {

    private static final String PATH_DELIMITER = "#";
    private static final String CUSTOMIZATION_UUID = "customizationUUID";
    private ToscaTemplate toscaTemplate;
    private ConfigurationManager configurationManager;
    private static Logger log = LoggerFactory.getLogger(SdcCsarHelperImpl.class.getName());

    public SdcCsarHelperImpl(ToscaTemplate toscaTemplate) {
        this.toscaTemplate = toscaTemplate;
    }

    public SdcCsarHelperImpl(ToscaTemplate toscaTemplate, ConfigurationManager configurationManager) {
        this.toscaTemplate = toscaTemplate;
        this.configurationManager = configurationManager;
    }
    
    @Override
    public List<Policy> getPoliciesOfTarget(NodeTemplate nodeTemplate) {
    	return getPoliciesOfNodeTemplate(nodeTemplate.getName())
    	.stream()
    	.sorted(Policy::compareTo)
    	.collect(toList());
    }
    
    @Override
	public List<Policy> getPoliciesOfOriginOfNodeTemplate(NodeTemplate nodeTemplate) {
    	if(StringUtils.isNotEmpty(nodeTemplate.getName())){
    		return getNodeTemplateByName(nodeTemplate.getName()).getOriginComponentTemplate().getPolicies();
    	}
    	return new ArrayList<>();
    }
    
    @Override
    public List<Policy> getPoliciesOfTargetByToscaPolicyType(NodeTemplate nodeTemplate, String policyTypeName) {
    	return getPoliciesOfNodeTemplate(nodeTemplate.getName())
    	.stream()
    	.filter(p->p.getType().equals(policyTypeName))
    	.sorted(Policy::compareTo)
    	.collect(toList());
    }
    
    @Override
    public List<Policy> getPoliciesOfOriginOfNodeTemplateByToscaPolicyType(NodeTemplate nodeTemplate, String policyTypeName) {
    	return getPoliciesOfOriginOfNodeTemplate(nodeTemplate)
    	.stream()
    	.filter(p->p.getType().equals(policyTypeName))
    	.sorted(Policy::compareTo)
    	.collect(toList());
    }

    @Override
    public List<NodeTemplate> getPolicyTargetsFromTopologyTemplate(String policyName) {
    	if(toscaTemplate.getNodeTemplates() == null){
    		return new ArrayList<>();
    	}
    	List<String> targetNames = getPolicyTargets(policyName);
    	return toscaTemplate.getNodeTemplates().stream()
    			.filter(nt->targetNames.contains(nt.getName()))
    			.collect(toList());
    }
	
	@Override
	public List<NodeTemplate> getPolicyTargetsFromOrigin(NodeTemplate nodeTemplate, String policyName) {
		Optional<Policy> policyOpt = null;
    	if(StringUtils.isNotEmpty(nodeTemplate.getName())){
    		policyOpt = getNodeTemplateByName(nodeTemplate.getName()).getOriginComponentTemplate().getPolicies().stream().filter(p -> p.getName().equals(policyName)).findFirst();
    	}
    	if(policyOpt.isPresent()){
    		List<String> targets = policyOpt.get().getTargets();
    		return nodeTemplate.getOriginComponentTemplate().getNodeTemplates()
    				.stream()
    				.filter(nt -> targets.contains(nt.getName())).collect(Collectors.toList());
    	}
    	return new ArrayList<>();
    }
	
	@Override
	public List<Policy> getPoliciesOfTopologyTemplate(){
    	if(toscaTemplate.getPolicies() == null)
    		return new ArrayList<>();
    	return toscaTemplate.getPolicies()
    	.stream()
    	.sorted(Policy::compareTo)
    	.collect(toList());
	}
	
	@Override
	public List<Policy> getPoliciesOfTopologyTemplateByToscaPolicyType(String policyTypeName){
    	if(toscaTemplate.getPolicies() == null)
    		return new ArrayList<>();
    	return toscaTemplate.getPolicies()
    	.stream()
    	.filter(p->p.getType().equals(policyTypeName))
    	.sorted(Policy::compareTo)
    	.collect(toList());
	}
	
    public NodeTemplate getNodeTemplateByName(String nodeTemplateName) {
    	if(toscaTemplate.getNodeTemplates() == null)
    		return null;
    	return toscaTemplate.getNodeTemplates()
    	.stream()
    	.filter(nt -> nt.getName().equals(nodeTemplateName))
    	.findFirst().orElse(null);
    }
    
    private List<Policy> getPoliciesOfNodeTemplate(String nodeTemplateName) {
    	if(toscaTemplate.getPolicies() == null)
    		return new ArrayList<>();
    	return toscaTemplate.getPolicies()
    	.stream()
    	.filter(p -> p.getTargets().contains(nodeTemplateName))
    	.collect(toList());
    }
    
    private List<String> getPolicyTargets(String policyName) {
    	return getPolicyByName(policyName).map(Policy::getTargets).orElse(new ArrayList<>());
    }
    
    private List<String> getGroupMembers(String groupName) {
    	return getGroupByName(groupName).map(Group::getMembers).orElse(new ArrayList<>());
    }
    
    private Optional<Policy> getPolicyByName(String policyName) {
    	if(toscaTemplate.getPolicies() == null)
    		return Optional.empty();
    	return toscaTemplate.getPolicies()
    	.stream()
    	.filter(p -> p.getName().equals(policyName)).findFirst();
    }
    
    private Optional<Group> getGroupByName(String groupName) {
    	if(toscaTemplate.getGroups() == null)
    		return Optional.empty();
    	return toscaTemplate.getGroups()
    	.stream()
    	.filter(g -> g.getName().equals(groupName)).findFirst();
    }
    
    @Override
    //Sunny flow  - covered with UT, flat and nested
    public String getNodeTemplatePropertyLeafValue(NodeTemplate nodeTemplate, String leafValuePath) {
        Object value = getNodeTemplatePropertyValueAsObject(nodeTemplate, leafValuePath);
        return value == null || value instanceof Function ? null : String.valueOf(value);
    }

    @Override
    public Object getNodeTemplatePropertyValueAsObject(NodeTemplate nodeTemplate, String leafValuePath) {
        if (nodeTemplate == null) {
            log.error("getNodeTemplatePropertyValueAsObject - nodeTemplate is null");
            return null;
        }
        if (GeneralUtility.isEmptyString(leafValuePath)) {
            log.error("getNodeTemplatePropertyValueAsObject - leafValuePath is null or empty");
            return null;
        }
        String[] split = getSplittedPath(leafValuePath);
        LinkedHashMap<String, Property> properties = nodeTemplate.getProperties();
        return PropertyUtils.processProperties(split, properties);
    }
    
    public Map<String, Map<String, Object>> getCpPropertiesFromVfcAsObject(NodeTemplate vfc) {
        if (vfc == null) {
            log.error("getCpPropertiesFromVfc - vfc is null");
            return new HashMap<>();
        }

        String presetProperty = "_ip_requirements";
        Map<String, Map<String, Object>> cps = new HashMap<>();

        Map<String, Property> props = vfc.getProperties();
        if (props != null) {
            // find all port names by pre-set property (ip_requirements)
            for (Map.Entry<String, Property> entry : props.entrySet()) {
                if (entry.getKey().endsWith(presetProperty)) {
                    String portName = entry.getKey().replaceAll(presetProperty, "");
                    cps.put(portName, new HashMap<>());
                }
            }

            findPutAllPortsProperties(cps, props);
        }

        return cps;
    }

	private void findPutAllPortsProperties(Map<String, Map<String, Object>> cps, Map<String, Property> props) {
		if (!cps.isEmpty()) {
		    for (Entry<String, Map<String, Object>> port : cps.entrySet()) {
		        for (Map.Entry<String, Property> property: props.entrySet()) {
		            if (property.getKey().startsWith(port.getKey())) {
		                String portProperty = property.getKey().replaceFirst(port.getKey() + "_", "");
		                if (property.getValue() != null) {
		                    cps.get(port.getKey()).put(portProperty, property.getValue().getValue());
		                }
		            }
		        }
		    }
		}
	}

    public Map<String, Map<String, Object>> getCpPropertiesFromVfc(NodeTemplate vfc) {

        if (vfc == null) {
            log.error("getCpPropertiesFromVfc - vfc is null");
            return new HashMap<>();
        }

        String presetProperty = "_ip_requirements";
        Map<String, Map<String, Object>> cps = new HashMap<>();

        Map<String, Property> props = vfc.getProperties();
        if (props != null) {
            // find all port names by pre-set property (ip_requirements)
            for (Map.Entry<String, Property> entry : props.entrySet()) {
                if (entry.getKey().endsWith(presetProperty)) {
                    String portName = entry.getKey().replaceAll(presetProperty, "");
                    cps.put(portName, new HashMap<>());
                }
            }
            findBuildPutAllPortsProperties(cps, props);
        }

        return cps;
    }

	private void findBuildPutAllPortsProperties(Map<String, Map<String, Object>> cps, Map<String, Property> props) {
		if (!cps.isEmpty()) {
		    for (Entry<String, Map<String, Object>> port : cps.entrySet()) {
		        for (Map.Entry<String, Property> property: props.entrySet()) {
		            if (property.getKey().startsWith(port.getKey())) {
		                Map<String, Object> portPaths = new HashMap<>();
		                String portProperty = property.getKey().replaceFirst(port.getKey() + "_", "");
		                buildPathMappedToValue(portProperty, property.getValue().getValue(), portPaths);
		                cps.get(port.getKey()).putAll(portPaths);
		            }
		        }
		    }
		}
	}

    @SuppressWarnings("unchecked")
    private void buildPathMappedToValue(String path, Object property, Map<String, Object> pathsMap) {
        if (property instanceof Map) {
            for (Map.Entry<String, Object> item : ((Map<String, Object>) property).entrySet()) {
                if (item.getValue() instanceof Map || item.getValue() instanceof List) {
                    buildPathMappedToValue(path + PATH_DELIMITER + item.getKey(), item.getValue(), pathsMap);
                } else {
                    pathsMap.put(path + PATH_DELIMITER + item.getKey(), item.getValue());
                }
            }
        } else if (property instanceof List) {
            for (Object item: (List<Object>)property) {
                buildPathMappedToValue(path, item, pathsMap);
            }
        } else {
            pathsMap.put(path, property);
        }

    }

    @Override
    //Sunny flow - covered with UT
    public List<NodeTemplate> getServiceVlList() {
        return getNodeTemplateBySdcType(toscaTemplate.getTopologyTemplate(), SdcTypes.VL);
    }

    @Override
    //Sunny flow - covered with UT
    public List<NodeTemplate> getServiceVfList() {
        return getNodeTemplateBySdcType(toscaTemplate.getTopologyTemplate(), SdcTypes.VF);
    }

    @Override
    //Sunny flow - covered with UT
    public String getMetadataPropertyValue(Metadata metadata, String metadataPropertyName) {
        if (GeneralUtility.isEmptyString(metadataPropertyName)) {
            log.error("getMetadataPropertyValue - the metadataPropertyName is null or empty");
            return null;
        }
        if (metadata == null) {
            log.error("getMetadataPropertyValue - the metadata is null");
            return null;
        }
        return metadata.getValue(metadataPropertyName);
    }


    @Override
    //Sunny flow - covered with UT
    public List<NodeTemplate> getServiceNodeTemplatesByType(String nodeType) {
        if (GeneralUtility.isEmptyString(nodeType)) {
            log.error("getServiceNodeTemplatesByType - nodeType - is null or empty");
            return new ArrayList<>();
        }

        List<NodeTemplate> res = new ArrayList<>();
        List<NodeTemplate> nodeTemplates = toscaTemplate.getNodeTemplates();
        for (NodeTemplate nodeTemplate : nodeTemplates) {
            if (nodeType.equals(nodeTemplate.getTypeDefinition().getType())) {
                res.add(nodeTemplate);
            }
        }

        return res;
    }


    @Override
    public List<NodeTemplate> getServiceNodeTemplates() {
        return toscaTemplate.getNodeTemplates();
    }

    @Override
    //Sunny flow - covered with UT
    public List<NodeTemplate> getVfcListByVf(String vfCustomizationId) {
        if (GeneralUtility.isEmptyString(vfCustomizationId)) {
            log.error("getVfcListByVf - vfCustomizationId - is null or empty");
            return new ArrayList<>();
        }

        List<NodeTemplate> serviceVfList = getServiceVfList();
        NodeTemplate vfInstance = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationId);
        List<NodeTemplate> vfcs = getNodeTemplateBySdcType(vfInstance, SdcTypes.VFC);
        vfcs.addAll(getNodeTemplateBySdcType(vfInstance, SdcTypes.CVFC));

        return vfcs;
    }

    @Override
    //Sunny flow - covered with UT
    public List<Group> getVfModulesByVf(String vfCustomizationUuid) {
        List<NodeTemplate> serviceVfList = getServiceVfList();
        NodeTemplate nodeTemplateByCustomizationUuid = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationUuid);
        if (nodeTemplateByCustomizationUuid != null) {
            String name = nodeTemplateByCustomizationUuid.getName();
            String normaliseComponentInstanceName = SdcToscaUtility.normaliseComponentInstanceName(name);
            List<Group> serviceLevelGroups = toscaTemplate.getTopologyTemplate().getGroups();
            log.debug("getVfModulesByVf - VF node template name {}, normalized name {}. Searching groups on service level starting with VF normalized name...", name, normaliseComponentInstanceName);
            if (serviceLevelGroups != null) {
                return serviceLevelGroups
                        .stream()
                        .filter(x -> "org.openecomp.groups.VfModule".equals(x.getTypeDefinition().getType()) && x.getName().startsWith(normaliseComponentInstanceName))
                        .collect(toList());
            }
        }
        return new ArrayList<>();
    }

    @Override
    //Sunny flow - covered with UT
    public String getServiceInputLeafValueOfDefault(String inputLeafValuePath) {
        if (GeneralUtility.isEmptyString(inputLeafValuePath)) {
            log.error("getServiceInputLeafValueOfDefault - inputLeafValuePath is null or empty");
            return null;
        }

        String[] split = getSplittedPath(inputLeafValuePath);
        if (split.length < 2 || !split[1].equals("default")) {
            log.error("getServiceInputLeafValue - inputLeafValuePath should be of format <input name>#default[optionally #<rest of path>] ");
            return null;
        }

        List<Input> inputs = toscaTemplate.getInputs();
        if (inputs != null) {
            Optional<Input> findFirst = inputs.stream().filter(x -> x.getName().equals(split[0])).findFirst();
            if (findFirst.isPresent()) {
                Input input = findFirst.get();
                Object current = input.getDefault();
                Object property = PropertyUtils.iterateProcessPath(2, current, split);
                return property == null || property instanceof Function? null : String.valueOf(property);
            }
        }
        log.error("getServiceInputLeafValue - value not found");
        return null;
    }

    @Override
    public Object getServiceInputLeafValueOfDefaultAsObject(String inputLeafValuePath) {
        if (GeneralUtility.isEmptyString(inputLeafValuePath)) {
            log.error("getServiceInputLeafValueOfDefaultAsObject - inputLeafValuePath is null or empty");
            return null;
        }

        String[] split = getSplittedPath(inputLeafValuePath);
        if (split.length < 2 || !split[1].equals("default")) {
            log.error("getServiceInputLeafValueOfDefaultAsObject - inputLeafValuePath should be of format <input name>#default[optionally #<rest of path>] ");
            return null;
        }

        List<Input> inputs = toscaTemplate.getInputs();
        if (inputs != null) {
            Optional<Input> findFirst = inputs.stream().filter(x -> x.getName().equals(split[0])).findFirst();
            if (findFirst.isPresent()) {
                Input input = findFirst.get();
                Object current = input.getDefault();
                return PropertyUtils.iterateProcessPath(2, current, split);
            }
        }
        log.error("getServiceInputLeafValueOfDefaultAsObject - value not found");
        return null;
    }

    private String[] getSplittedPath(String leafValuePath) {
        return leafValuePath.split(PATH_DELIMITER);
    }


    @Override
    //Sunny flow - covered with UT
    public String getServiceSubstitutionMappingsTypeName() {
        SubstitutionMappings substitutionMappings = toscaTemplate.getTopologyTemplate().getSubstitutionMappings();
        if (substitutionMappings == null) {
            log.debug("getServiceSubstitutionMappingsTypeName - No Substitution Mappings defined");
            return null;
        }

        NodeType nodeType = substitutionMappings.getNodeDefinition();
        if (nodeType == null) {
            log.debug("getServiceSubstitutionMappingsTypeName - No Substitution Mappings node defined");
            return null;
        }

        return nodeType.getType();
    }

    @Override
    //Sunny flow - covered with UT
    public Metadata getServiceMetadata() {
        return toscaTemplate.getMetaData();
    }

    @Override
    //Sunny flow - covered with UT
    public Map<String, Object> getServiceMetadataProperties() {
        if (toscaTemplate.getMetaData() == null){
            return null;
        }
        return new HashMap<>(toscaTemplate.getMetaData().getAllProperties());
    }

    @Override
    public Map<String, String> getServiceMetadataAllProperties() {
        if (toscaTemplate.getMetaData() == null){
            return null;
        }
        return toscaTemplate.getMetaData().getAllProperties();
    }

    @Override
    //Sunny flow - covered with UT
    public List<Input> getServiceInputs() {
        return toscaTemplate.getInputs();
    }

    @Override
    //Sunny flow - covered with UT
    public String getGroupPropertyLeafValue(Group group, String leafValuePath) {
        if (group == null) {
            log.error("getGroupPropertyLeafValue - group is null");
            return null;
        }

        if (GeneralUtility.isEmptyString(leafValuePath)) {
            log.error("getGroupPropertyLeafValue - leafValuePath is null or empty");
            return null;
        }

        String[] split = getSplittedPath(leafValuePath);
        LinkedHashMap<String, Property> properties = group.getProperties();
        Object property = PropertyUtils.processProperties(split, properties);
        return property == null || property instanceof Function? null : String.valueOf(property);
    }

    @Override
    public Object getGroupPropertyAsObject(Group group, String leafValuePath) {
        if (group == null) {
            log.error("getGroupPropertyAsObject - group is null");
            return null;
        }

        if (GeneralUtility.isEmptyString(leafValuePath)) {
            log.error("getGroupPropertyAsObject - leafValuePath is null or empty");
            return null;
        }

        String[] split = getSplittedPath(leafValuePath);
        LinkedHashMap<String, Property> properties = group.getProperties();
        return PropertyUtils.processProperties(split, properties);
    }

    @Override
    //Sunny flow - covered with UT
    public List<NodeTemplate> getCpListByVf(String vfCustomizationId) {
        List<NodeTemplate> cpList = new ArrayList<>();
        if (GeneralUtility.isEmptyString(vfCustomizationId)) {
            log.error("getCpListByVf vfCustomizationId string is empty");
            return cpList;
        }

        List<NodeTemplate> serviceVfList = getServiceVfList();
        if (serviceVfList == null || serviceVfList.isEmpty()) {
            log.error("getCpListByVf Vfs not exist for vfCustomizationId {}", vfCustomizationId);
            return cpList;
        }
        NodeTemplate vfInstance = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationId);
        if (vfInstance == null) {
            log.debug("getCpListByVf vf list is null");
            return cpList;
        }
        cpList = getNodeTemplateBySdcType(vfInstance, SdcTypes.CP);
        if (cpList == null || cpList.isEmpty())
            log.debug("getCpListByVf cps not exist for vfCustomizationId {}", vfCustomizationId);
        return cpList;
    }

    @Override
    //Sunny flow - covered with UT
    public List<NodeTemplate> getMembersOfVfModule(NodeTemplate vf, Group serviceLevelVfModule) {
        if (vf == null) {
            log.error("getMembersOfVfModule - vf is null");
            return new ArrayList<>();
        }

        if (serviceLevelVfModule == null || serviceLevelVfModule.getMetadata() == null || serviceLevelVfModule.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_VFMODULEMODELINVARIANTUUID) == null) {
            log.error("getMembersOfVfModule - vfModule or its metadata is null. Cannot match a VF group based on invariantUuid from missing metadata.");
            return new ArrayList<>();
        }


        SubstitutionMappings substitutionMappings = vf.getSubMappingToscaTemplate();
        if (substitutionMappings != null) {
            List<Group> groups = substitutionMappings.getGroups();
            if (groups != null) {
                Optional<Group> findFirst = groups
                        .stream()
                        .filter(x -> (x.getMetadata() != null && serviceLevelVfModule.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_VFMODULEMODELINVARIANTUUID).equals(x.getMetadata().getValue(SdcPropertyNames.PROPERTY_NAME_VFMODULEMODELINVARIANTUUID)))).findFirst();
                if (findFirst.isPresent()) {
                    List<String> members = findFirst.get().getMembers();
                    if (members != null) {
                        return substitutionMappings.getNodeTemplates().stream().filter(x -> members.contains(x.getName())).collect(toList());
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Pair<NodeTemplate, NodeTemplate>> getNodeTemplatePairsByReqName(
            List<NodeTemplate> listOfReqNodeTemplates, List<NodeTemplate> listOfCapNodeTemplates, String reqName) {

        if (listOfReqNodeTemplates == null) {
            log.error("getNodeTemplatePairsByReqName - listOfReqNodeTemplates is null");
            return new ArrayList<>();
        }

        if (listOfCapNodeTemplates == null) {
            log.error("getNodeTemplatePairsByReqName - listOfCapNodeTemplates is null");
            return new ArrayList<>();
        }

        if (GeneralUtility.isEmptyString(reqName)) {
            log.error("getNodeTemplatePairsByReqName - reqName is null or empty");
            return new ArrayList<>();
        }

        List<Pair<NodeTemplate, NodeTemplate>> pairsList = new ArrayList<>();

        for (NodeTemplate reqNodeTemplate : listOfReqNodeTemplates) {
            List<RequirementAssignment> requirements = reqNodeTemplate.getRequirements().getRequirementsByName(reqName).getAll();
            for (RequirementAssignment reqEntry : requirements) {
                String node = reqEntry.getNodeTemplateName();
                if (node != null) {
                    Optional<NodeTemplate> findFirst = listOfCapNodeTemplates.stream().filter(x -> x.getName().equals(node)).findFirst();
                    if (findFirst.isPresent()) {
                        pairsList.add(new ImmutablePair<NodeTemplate, NodeTemplate>(reqNodeTemplate, findFirst.get()));
                    }
                }
            }
        }

        return pairsList;
    }

    @Override
    //Sunny flow - covered with UT
    public List<NodeTemplate> getAllottedResources() {
        List<NodeTemplate> nodeTemplates = null;
        nodeTemplates = toscaTemplate.getTopologyTemplate().getNodeTemplates();
        if (nodeTemplates.isEmpty()) {
            log.error("getAllottedResources nodeTemplates not exist");
        }
        nodeTemplates = nodeTemplates.stream().filter(
                x -> x.getMetaData() != null && x.getMetaData().getValue("category").equals("Allotted Resource"))
                .collect(toList());
        if (nodeTemplates.isEmpty()) {
            log.debug("getAllottedResources -  allotted resources not exist");
        }
        return nodeTemplates;
    }

    @Override
    //Sunny flow - covered with UT
    public String getTypeOfNodeTemplate(NodeTemplate nodeTemplate) {
        if (nodeTemplate == null) {

            log.error("getTypeOfNodeTemplate nodeTemplate is null");
            return null;
        }
        return nodeTemplate.getTypeDefinition().getType();
    }

    /**
     * This methdd is returning the csarConformanceLevel for input CSAR
     * When csarConformanceLevel is configured with failOnError as False in Error Configuration; it
     * assigns the default value to csarConformanceLevel which is the max level provided in
     * Configuration file
     * @return csarConformanceLevel
     */
    @Override
    public String getConformanceLevel() {
      LinkedHashMap<String, Object> csarMeta = toscaTemplate.getMetaProperties("csar.meta");
      if (csarMeta == null){
        log.warn("No csar.meta file is found in CSAR - this file should hold the conformance level of the CSAR. This might be OK for older CSARs.");
        if (configurationManager != null && !configurationManager.getErrorConfiguration()
            .getErrorInfo("CONFORMANCE_LEVEL_ERROR").getFailOnError()){
          String csarConLevel = configurationManager.getConfiguration().getConformanceLevel().getMaxVersion();
          log.warn("csarConformanceLevel is not found in input csar; defaulting to max version {}" , csarConLevel);
          return csarConLevel;
        }
        else {
          log.warn("csarConformanceLevel is not found in input csar; returning null as no defaults defined in error configuration");
          return null;
        }
      }

      Object conformanceLevel = csarMeta.get("SDC-TOSCA-Definitions-Version");
      if (conformanceLevel != null){
        String confLevelStr = conformanceLevel.toString();
        log.debug("CSAR conformance level is {}", confLevelStr);
        return confLevelStr;
      } else {
        log.error("Invalid csar.meta file - no entry found for SDC-TOSCA-Definitions-Version key. This entry should hold the conformance level.");
        return null;
      }
    }
	
	
	@Override
	public String getNodeTemplateCustomizationUuid(NodeTemplate nt) {
		String res = null;
		if (nt != null && nt.getMetaData() != null){
			res = nt.getMetaData().getValue(CUSTOMIZATION_UUID);
		} else {
			log.error("Node template or its metadata is null");
		}
		return res;
	}

    public List<NodeTemplate> getNodeTemplateBySdcType(NodeTemplate parentNodeTemplate, SdcTypes sdcType) {
    	return getNodeTemplateBySdcType(parentNodeTemplate, sdcType, false); 
    }

    public boolean isNodeTypeSupported(NodeTemplate nodeTemplate) {
        SdcTypes[] supportedTypes = SdcTypes.values();
        return Arrays.stream(supportedTypes)
                .anyMatch(v->nodeTemplate.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)
                        .equals(v.getValue()));
    }
    
    private List<NodeTemplate> getNodeTemplateBySdcType(NodeTemplate parentNodeTemplate, SdcTypes sdcType, boolean isVNF)  {
    	
    	if (parentNodeTemplate == null) {
            log.error("getNodeTemplateBySdcType - nodeTemplate is null or empty");
            return new ArrayList<>();
        }

        if (sdcType == null) {
            log.error("getNodeTemplateBySdcType - sdcType is null or empty");
            return new ArrayList<>();
        }

        SubstitutionMappings substitutionMappings = parentNodeTemplate.getSubMappingToscaTemplate();

        if (substitutionMappings != null) {
            List<NodeTemplate> nodeTemplates = substitutionMappings.getNodeTemplates();
            if (nodeTemplates != null && !nodeTemplates.isEmpty()) {
            	if (sdcType.equals(SdcTypes.VFC) && isVNF)  {
            		return nodeTemplates.stream()
                    		.filter(x -> (x.getMetaData() != null &&
                    			sdcType.getValue().equals(x.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE))) &&  isVNFType(x))
                    		.collect(toList());
            	}
            	else {
                    return nodeTemplates.stream()
                    		.filter(x -> (x.getMetaData() != null &&
                    			sdcType.getValue().equals(x.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE))) &&  !isVNFType(x))
                    		.collect(toList());
            	}
            }
            else {
                log.debug("getNodeTemplateBySdcType - SubstitutionMappings' node Templates not exist");
            }
        } else
            log.debug("getNodeTemplateBySdcType - SubstitutionMappings not exist");

        return new ArrayList<>();
    }

    public Map<String, String> filterNodeTemplatePropertiesByValue(NodeTemplate nodeTemplate, FilterType filterType, String pattern) {
        Map<String, String> filterMap = new HashMap<>();

        if (nodeTemplate == null) {
            log.error("filterNodeTemplatePropertiesByValue nodeTemplate is null");
            return filterMap;
        }

        if (filterType == null) {
            log.error("filterNodeTemplatePropertiesByValue filterType is null");
            return filterMap;
        }

        if (GeneralUtility.isEmptyString(pattern)) {
            log.error("filterNodeTemplatePropertiesByValue pattern string is empty");
            return filterMap;
        }

        Map<String, Property> ntProperties = nodeTemplate.getProperties();

        if (ntProperties != null && ntProperties.size() > 0) {

            for (Property current : ntProperties.values()) {
                if (current.getValue() != null) {
                    filterProperties(current.getValue(), current.getName(), filterType, pattern, filterMap);
                }
            }
        }

        log.trace("filterNodeTemplatePropertiesByValue - filterMap value: {}", filterMap);

        return filterMap;
    }
    
	public NodeTemplate getVnfConfig(String vfCustomizationUuid) {
		
		if (GeneralUtility.isEmptyString(vfCustomizationUuid)) {
            log.error("getVnfConfig - vfCustomizationId - is null or empty");
            return null;
        }

        List<NodeTemplate> serviceVfList = getServiceVfList();
        NodeTemplate vfInstance = getNodeTemplateByCustomizationUuid(serviceVfList, vfCustomizationUuid);
        return getNodeTemplateBySdcType(vfInstance, SdcTypes.VFC, true).stream().findAny().orElse(null);
	}

    @Override
    public boolean hasTopology(NodeTemplate nodeTemplate) {
        if (nodeTemplate == null) {
            log.error("hasTopology - nodeTemplate - is null");
            return false;
        }

        if (nodeTemplate.getMetaData() != null) {
            String type = nodeTemplate.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE);
            log.debug("hasTopology - node template {} is a {} type", nodeTemplate.getName(), type);
            return SdcTypes.isComplex(type);
        }

        return false;
    }

    @Override
    public List<NodeTemplate> getNodeTemplateChildren(NodeTemplate nodeTemplate) {
        if (nodeTemplate == null) {
            log.error("getNodeTemplateChildren - nodeTemplate - is null");
            return new ArrayList<>();
        }

        SubstitutionMappings substitutionMappings = nodeTemplate.getSubMappingToscaTemplate();
        if (substitutionMappings != null) {
            List<NodeTemplate> nodeTemplates = substitutionMappings.getNodeTemplates();
            if (nodeTemplates != null && !nodeTemplates.isEmpty()) {

                return nodeTemplates.stream()
                        .filter(x -> !isVNFType(x))
                        .collect(toList());
            }
            else {
                log.debug("getNodeTemplateChildren - SubstitutionMappings' node Templates not exist");
            }
        } else
            log.debug("getNodeTemplateChildren - SubstitutionMappings not exist");

        return new ArrayList<>();
    }

    @Override
    public NodeTemplate getServiceNodeTemplateByNodeName(String nodeName) {
        if (GeneralUtility.isEmptyString(nodeName)) {
            log.error("getServiceNodeTemplateByNodeName - nodeName - is null or empty");
            return null;
        }

        List<NodeTemplate> nodeTemplates = getServiceNodeTemplates();
        Optional<NodeTemplate> findFirst =  nodeTemplates.stream().filter(nt -> nt.getName().equals(nodeName)).findFirst();

        return findFirst.isPresent() ? findFirst.get() : null;
    }

    @Override
    public Metadata getNodeTemplateMetadata(NodeTemplate nt) {
        if (nt == null) {
            log.error("getNodeTemplateMetadata - nt (node template) - is null");
            return null;
        }

        return nt.getMetaData();
    }

    @Override
    public CapabilityAssignments getCapabilitiesOf(NodeTemplate nt) {
        if (nt == null) {
            log.error("getCapabilitiesOf - nt (node template) - is null");
            return null;
        }

        return nt.getCapabilities();
    }

    @Override
    public RequirementAssignments getRequirementsOf(NodeTemplate nt) {
        if (nt == null) {
            log.error("getRequirementsOf - nt (node template) - is null");
            return null;
        }

        return nt.getRequirements();
    }

    @Override
    public String getCapabilityPropertyLeafValue(CapabilityAssignment capability, String pathToPropertyLeafValue) {
        if (capability == null) {
            log.error("getCapabilityPropertyLeafValue - capability is null");
            return null;
        }

        if (GeneralUtility.isEmptyString(pathToPropertyLeafValue)) {
            log.error("getCapabilityPropertyLeafValue - pathToPropertyLeafValue is null or empty");
            return null;
        }

        String[] split = getSplittedPath(pathToPropertyLeafValue);
        LinkedHashMap<String, Property> properties = capability.getProperties();
        Object property = PropertyUtils.processProperties(split, properties);
        return property == null || property instanceof Function ? null : String.valueOf(property);
    }
    
    @Override
    public ArrayList<Group> getGroupsOfOriginOfNodeTemplate(NodeTemplate nodeTemplate) {
    	if(StringUtils.isNotEmpty(nodeTemplate.getName())){
    		return getNodeTemplateByName(nodeTemplate.getName()).getSubMappingToscaTemplate().getGroups();
    	}
    	return new ArrayList<>();
    }

    @Override
    public ArrayList<Group> getGroupsOfTopologyTemplateByToscaGroupType(String groupType) {
       	if(toscaTemplate.getGroups() == null)
       		return new ArrayList<>();
       	return (ArrayList<Group>) toscaTemplate.getGroups()
       	.stream()
       	.filter(g->g.getType().equals(groupType))
       	.sorted(Group::compareTo)
       	.collect(toList());
    }
    
    @Override
    public ArrayList<Group> getGroupsOfTopologyTemplate() {
    	return toscaTemplate.getGroups() == null ? new ArrayList<>() : toscaTemplate.getGroups();
    }
    
    @Override
    public ArrayList<Group> getGroupsOfOriginOfNodeTemplateByToscaGroupType(NodeTemplate nodeTemplate, String groupType) {
    	return (ArrayList<Group>) getGroupsOfOriginOfNodeTemplate(nodeTemplate)
    	.stream()
    	.filter(g->g.getType().equals(groupType))
    	.sorted(Group::compareTo)
    	.collect(toList());
    }

    @Override
    public List<NodeTemplate> getGroupMembersFromTopologyTemplate(String groupName) {
       	if(toscaTemplate.getNodeTemplates() == null){
       		return new ArrayList<>();
       	}
       	List<String> membersNames = getGroupMembers(groupName);
       	return toscaTemplate.getNodeTemplates().stream()
      			.filter(nt->membersNames.contains(nt.getName()))
     			.collect(toList());
    }
    

    @Override
    public List<NodeTemplate> getGroupMembersOfOriginOfNodeTemplate(NodeTemplate nodeTemplate, String groupName) {
		ArrayList<Group> groups = getGroupsOfOriginOfNodeTemplate(nodeTemplate);
		if(!groups.isEmpty()){
			Optional<Group> group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst();
			if(group.isPresent()){
				return nodeTemplate.getSubMappingToscaTemplate().getNodeTemplates().stream()
				.filter(nt -> group.get().getMembers().contains(nt.getName()))
				.collect(toList());
			}
		}
		return new ArrayList<>();
    }
    
    public List<NodeTemplate> getServiceNodeTemplateBySdcType(SdcTypes sdcType) {
    	if (sdcType == null) {
    		log.error("getServiceNodeTemplateBySdcType - sdcType is null or empty");
    		return new ArrayList<>();
    	}
    	
    	TopologyTemplate topologyTemplate = toscaTemplate.getTopologyTemplate();
    	return getNodeTemplateBySdcType(topologyTemplate, sdcType);
    }

	/************************************* helper functions ***********************************/
    private boolean isVNFType(NodeTemplate nt) {
        return nt.getType().endsWith("VnfConfiguration");
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> filterProperties(Object property, String path, FilterType filterType, String pattern, Map<String, String> filterMap) {

        if (property instanceof Map) {
            for (Map.Entry<String, Object> item: ((Map<String, Object>) property).entrySet()) {
                String itemPath = path + PATH_DELIMITER + item.getKey();
                filterProperties(item.getValue(), itemPath, filterType, pattern, filterMap);
            }
        } else if (property instanceof List) {
            for (Object item: (List<Object>)property) {
                filterProperties(item, path, filterType, pattern, filterMap);
            }
        } else {
            if (filterType.isMatch(property.toString(), pattern)) {
                filterMap.put(path, property.toString());
            }
        }

        return filterMap;
    }
 
    /************************************* helper functions ***********************************/
    private List<NodeTemplate> getNodeTemplateBySdcType(TopologyTemplate topologyTemplate, SdcTypes sdcType) {
        if (sdcType == null) {
            log.error("getNodeTemplateBySdcType - sdcType is null or empty");
            return new ArrayList<>();
        }

        if (topologyTemplate == null) {
            log.error("getNodeTemplateBySdcType - topologyTemplate is null");
            return new ArrayList<>();
        }

        List<NodeTemplate> nodeTemplates = topologyTemplate.getNodeTemplates();

        if (nodeTemplates != null && !nodeTemplates.isEmpty())
            return nodeTemplates.stream().filter(x -> (x.getMetaData() != null && sdcType.getValue().equals(x.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_TYPE)))).collect(toList());

        log.debug("getNodeTemplateBySdcType - topologyTemplate's nodeTemplates not exist");
        return new ArrayList<>();
    }

    //Assumed to be unique property for the list
    private NodeTemplate getNodeTemplateByCustomizationUuid(List<NodeTemplate> nodeTemplates, String customizationId) {
       if (customizationId != null) {
            Optional<NodeTemplate> findFirst = nodeTemplates.stream().filter(x -> (x.getMetaData() != null && customizationId.equals(x.getMetaData().getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID)))).findFirst();
            return findFirst.isPresent() ? findFirst.get() : null;
        }
        else {
            log.error("getNodeTemplateByCustomizationUuid - customizationId is null");
            return null;
        }
    }

  @Override
  public Map<String, List<InterfacesDef>> getInterfacesOf(NodeTemplate nt){
    if (nt == null) {
      return null;
    }
    return nt.getAllInterfaceDetailsForNodeType();
  }

  @Override
  public List<String> getInterfaces(NodeTemplate nt){
    Map<String, List<InterfacesDef>> interfaceDetails = nt.getAllInterfaceDetailsForNodeType();
    return new ArrayList<>(interfaceDetails.keySet());
  }

  @Override
  public List<InterfacesDef> getInterfaceDetails(NodeTemplate nt, String interfaceName){
    Map<String, List<InterfacesDef>> interfaceDetails = nt.getAllInterfaceDetailsForNodeType();
    return interfaceDetails.get(interfaceName);
  }

  @Override
  public List<String> getAllInterfaceOperations(NodeTemplate nt, String interfaceName){
    Map<String, List<InterfacesDef>> interfaceDetails = nt.getAllInterfaceDetailsForNodeType();
    return interfaceDetails.values().stream().flatMap(List::stream).map(val -> val.getOperationName()).collect(
        Collectors.toList());
  }

  @Override
  public InterfacesDef getInterfaceOperationDetails(NodeTemplate nt, String interfaceName, String operationName){
    Map<String, List<InterfacesDef>> interfaceDetails = nt.getAllInterfaceDetailsForNodeType();
    if(!interfaceDetails.isEmpty()){
      List<InterfacesDef> interfaceDefs = interfaceDetails.get(interfaceName);
      return interfaceDefs.stream().filter(val -> val.getOperationName().equals(operationName)).findFirst().orElse(null);
    }
    return null;
  }

    @Override
    public List<String> getPropertyLeafValueByPropertyNamePathAndNodeTemplatePath(String propertyNamePath, String nodeTemplatePath) {
        log.info("A new request is received: property path is [{}], node template path is [{}]",
                propertyNamePath, nodeTemplatePath);

        List<String> propertyValuesList;

        if (StringUtils.isEmpty(nodeTemplatePath) || StringUtils.isEmpty(propertyNamePath)) {
            log.error("One of parameters is empty or null: property path is [{}], node template path is [{}]",
                    propertyNamePath, nodeTemplatePath);
            propertyValuesList = Collections.emptyList();
        }
        else {
            String[] nodeTemplates = getSplittedPath(nodeTemplatePath);
            propertyValuesList = getPropertyFromInternalNodeTemplate(getNodeTemplateByName(nodeTemplates[0]), 1, nodeTemplates, propertyNamePath);
            log.info("Found property value {} by path [{}] for node template [{}]",
                    propertyValuesList, propertyNamePath, nodeTemplatePath);
        }
        return propertyValuesList;
    }

    private List<String> getPropertyFromInternalNodeTemplate(NodeTemplate parent, int index,
                                                       String[] nodeTemplatePath, String propertyPath) {
        List<String> propertyValuesList;
        if (parent == null) {
            log.error("Node template {} is not found, the request will be rejected", nodeTemplatePath[index]);
            propertyValuesList = Collections.emptyList();
        }
        else if (nodeTemplatePath.length <= index) {
            log.debug("Stop NODE TEMPLATE searching");
            propertyValuesList = getSimpleOrListPropertyValue(parent, propertyPath);
        }
        else {
            log.debug("Node template {} is found with name {}", nodeTemplatePath[index], parent.getName());
            NodeTemplate childNT = getChildNodeTemplateByName(parent, nodeTemplatePath[index]);

            if (childNT == null || !isNodeTypeSupported(childNT)) {
                log.error("Unsupported or not found node template named {}, the request will be rejected",
                        nodeTemplatePath[index]);
                propertyValuesList = Collections.emptyList();
            }
            else {
                propertyValuesList = getPropertyFromInternalNodeTemplate(childNT, index + 1, nodeTemplatePath,
                        propertyPath);
            }
        }
        return propertyValuesList;
    }



    private List<String> getSimpleOrListPropertyValue(NodeTemplate nodeTemplate, String propertyPath) {
        List<String> propertyValueList;
        String[] path = getSplittedPath(propertyPath);
        Property property = getNodeTemplatePropertyObjectByName(nodeTemplate, path[0]);

        if (PropertyUtils.isPropertyTypeSimpleOrListOfSimpleTypes(nodeTemplate, path, property)) {
            //the requested property type is either simple or list of simple types
            PropertySchemaType propertyType = PropertySchemaType.getEnumByValue(property.getType());
            if (propertyType == PropertySchemaType.LIST &&
                        PropertyUtils.isDataPropertyType((String)property.getEntrySchema()
                                .get(SdcPropertyNames.PROPERTY_NAME_TYPE))) {
                //cover the case when a type of property "path[0]' is list of data types
                // and the requested property is an internal simple property of this data type
                propertyValueList = calculatePropertyValue(getNodeTemplatePropertyValueAsObject(nodeTemplate, path[0]), path, nodeTemplate.getName());
            }
            else {
                //the requested property is simple type or list of simple types
                propertyValueList = calculatePropertyValue(getNodeTemplatePropertyValueAsObject(nodeTemplate, propertyPath), null, nodeTemplate.getName());
            }
        }
        else {
            log.error("The type of property {} on node {} is neither simple nor list of simple objects, the request will be rejected",
                    propertyPath, nodeTemplate.getName());
            propertyValueList = Collections.emptyList();
        }
        return propertyValueList;
    }

    private List<String> calculatePropertyValue(Object valueAsObject, String path[], String nodeName) {
        if (valueAsObject == null || valueAsObject instanceof Map) {
            log.error("The property {} either is not found on node template [{}], or it is data type, or it is not resolved get_input", path, nodeName);
            return Collections.emptyList();
        }
        if (path != null) {
            return PropertyUtils.findSimplePropertyValueInListOfDataTypes((List<Object>)valueAsObject, path);
        }
        return PropertyUtils.buildSimplePropertValueOrList(valueAsObject);
    }




    private Property getNodeTemplatePropertyObjectByName(NodeTemplate nodeTemplate, String propertyName) {
        return nodeTemplate.getPropertiesObjects()
                .stream()
                .filter(p->p.getName().equals(propertyName))
                .findFirst()
                .orElse(null);
    }

    private NodeTemplate getChildNodeTemplateByName(NodeTemplate parent, String nodeTemplateName) {
        return getNodeTemplateChildren(parent)
            .stream()
            .filter(nt->nt.getName().equals(nodeTemplateName))
            .findFirst().orElse(null);
    }

    @Override
    public List<Input> getInputsWithAnnotations() {
        return toscaTemplate.getInputs(true);
    }

    @Override
    public List<IEntityDetails> getEntity(EntityQuery entityQuery, TopologyTemplateQuery topologyTemplateQuery, boolean isRecursive) {
        List<IEntityDetails> foundEntities = new ArrayList<>();
        List<NodeTemplate> vfcList = getVfcListByVf("05e77410-a1d8-44fe-8440-b9410c8f98ee");
        NodeTemplate vfc = getNodeTemplateByCustomizationUuid(vfcList, "1fdc9625-dfec-48e1-aaf8-7b92f78ca854");
        NodeTemplate cp = getChildNodeTemplateByName(vfc, "ssc_ssc_avpn_port_0");
        foundEntities.add(EntityDetailsFactory.createEntityDetails(EntityTemplateType.NODE_TEMPLATE, cp, vfc));
        return foundEntities;
    }

}

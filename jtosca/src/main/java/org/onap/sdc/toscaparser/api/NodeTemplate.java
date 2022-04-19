/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.sdc.toscaparser.api;

import static org.onap.sdc.toscaparser.api.elements.EntityType.TOSCA_DEF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.onap.sdc.toscaparser.api.common.JToscaValidationIssue;
import org.onap.sdc.toscaparser.api.elements.ArtifactDef;
import org.onap.sdc.toscaparser.api.elements.EntityType;
import org.onap.sdc.toscaparser.api.elements.InterfacesDef;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.onap.sdc.toscaparser.api.elements.NodeType;
import org.onap.sdc.toscaparser.api.elements.RelationshipType;
import org.onap.sdc.toscaparser.api.utils.CopyUtils;
import org.onap.sdc.toscaparser.api.utils.ThreadLocalsHolder;

public class NodeTemplate extends EntityTemplate {

    private static final String METADATA = "metadata";
    private LinkedHashMap<String, Object> templates;
    private ArrayList<RelationshipTemplate> availableRelTpls;
    private LinkedHashMap<String, Object> availableRelTypes;
    private LinkedHashMap<NodeTemplate, RelationshipType> related;
    private ArrayList<RelationshipTemplate> relationshipTpl;
    private LinkedHashMap<RelationshipType, NodeTemplate> _relationships;
    @Getter
    @Setter
    private SubstitutionMappings subMappingToscaTemplate;
    @Getter
    @Setter
    private TopologyTemplate originComponentTemplate;
    @Getter
    @Setter
    private Metadata metadata;
    @Getter
    private Map<String, ArtifactDef> artifacts;

    public NodeTemplate(String name,
                        LinkedHashMap<String, Object> ntnodeTemplates,
                        LinkedHashMap<String, Object> ntcustomDef,
                        ArrayList<RelationshipTemplate> ntavailableRelTpls,
                        LinkedHashMap<String, Object> ntavailableRelTypes) {
        this(name, ntnodeTemplates, ntcustomDef, ntavailableRelTpls, ntavailableRelTypes, null);
    }

    @SuppressWarnings("unchecked")
    public NodeTemplate(String name,
                        LinkedHashMap<String, Object> ntnodeTemplates,
                        LinkedHashMap<String, Object> ntcustomDef,
                        ArrayList<RelationshipTemplate> ntavailableRelTpls,
                        LinkedHashMap<String, Object> ntavailableRelTypes,
                        NodeTemplate parentNodeTemplate) {

        super(name, (LinkedHashMap<String, Object>) ntnodeTemplates.get(name), "node_type", ntcustomDef, parentNodeTemplate);

        templates = ntnodeTemplates;
        _validateFields((LinkedHashMap<String, Object>) templates.get(name));
        customDef = ntcustomDef;
        related = new LinkedHashMap<NodeTemplate, RelationshipType>();
        relationshipTpl = new ArrayList<RelationshipTemplate>();
        availableRelTpls = ntavailableRelTpls;
        availableRelTypes = ntavailableRelTypes;
        _relationships = new LinkedHashMap<RelationshipType, NodeTemplate>();
        subMappingToscaTemplate = null;
        metadata = _metaData();
        artifacts = readArtifacts((Map<String, Object>) templates.get(name));
    }

    private Map<String, ArtifactDef> readArtifacts(final Map<String, Object> nodetemplate) {
        if (nodetemplate.get("artifacts") != null) {
            final Map<String, ArtifactDef> artifactsMap = new HashMap<>();
            ((Map<String, Object>) nodetemplate.get("artifacts")).forEach((name, value) -> {
                artifactsMap.put(name, new ArtifactDef((Map<String, Object>) value));
            });
            return artifactsMap;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public LinkedHashMap<RelationshipType, NodeTemplate> getRelationships() {
        if (_relationships.isEmpty()) {
            List<RequirementAssignment> requires = getRequirements().getAll();
            if (requires != null && requires instanceof List) {
                for (RequirementAssignment r : requires) {
                    LinkedHashMap<RelationshipType, NodeTemplate> explicit = _getExplicitRelationship(r);
                    if (explicit != null) {
                        // _relationships.putAll(explicit)...
                        for (Map.Entry<RelationshipType, NodeTemplate> ee : explicit.entrySet()) {
                            _relationships.put(ee.getKey(), ee.getValue());
                        }
                    }
                }
            }
        }
        return _relationships;
    }

    @SuppressWarnings("unchecked")
    private LinkedHashMap<RelationshipType, NodeTemplate> _getExplicitRelationship(RequirementAssignment req) {
        // Handle explicit relationship

        // For example,
        // - req:
        //     node: DBMS
        //     relationship: tosca.relationships.HostedOn

        LinkedHashMap<RelationshipType, NodeTemplate> explicitRelation = new LinkedHashMap<RelationshipType, NodeTemplate>();
        String node = req.getNodeTemplateName();

        if (node != null && !node.isEmpty()) {
            //msg = _('Lookup by TOSCA types is not supported. '
            //        'Requirement for "%s" can not be full-filled.') % self.name
            boolean bFound = false;
            for (String k : EntityType.TOSCA_DEF.keySet()) {
                if (k.equals(node)) {
                    bFound = true;
                    break;
                }
            }
            if (bFound || customDef.get(node) != null) {
                ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE205", String.format(
                    "NotImplementedError: Lookup by TOSCA types is not supported. Requirement for \"%s\" can not be full-filled",
                    getName())));
                return null;
            }
            if (templates.get(node) == null) {
                ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE206", String.format(
                    "KeyError: Node template \"%s\" was not found", node)));
                return null;
            }
            NodeTemplate relatedTpl = new NodeTemplate(node, templates, customDef, null, null);
            Object relationship = req.getRelationship();
            String relationshipString = null;
//			// here relationship can be a string or a LHM with 'type':<relationship>

            // check if its type has relationship defined
            if (relationship == null) {
                ArrayList<Object> parentReqs = ((NodeType) typeDefinition).getAllRequirements();
                if (parentReqs == null) {
                    ThreadLocalsHolder.getCollector()
                        .appendValidationIssue(new JToscaValidationIssue("JE207", "ValidationError: parent_req is null"));
                } else {
//					for(String key: req.keySet()) {
//						boolean bFoundRel = false;
                    for (Object rdo : parentReqs) {
                        LinkedHashMap<String, Object> reqDict = (LinkedHashMap<String, Object>) rdo;
                        LinkedHashMap<String, Object> relDict = (LinkedHashMap<String, Object>) reqDict.get(req.getName());
                        if (relDict != null) {
                            relationship = relDict.get("relationship");
                            //BUG-python??? need to break twice?
//								bFoundRel = true;
                            break;
                        }
                    }
//						if(bFoundRel) {
//							break;
//						}
//					}
                }
            }

            if (relationship != null) {
                // here relationship can be a string or a LHM with 'type':<relationship>
                if (relationship instanceof String) {
                    relationshipString = (String) relationship;
                } else if (relationship instanceof LinkedHashMap) {
                    relationshipString = (String) ((LinkedHashMap<String, Object>) relationship).get("type");
                }

                boolean foundRelationshipTpl = false;
                // apply available relationship templates if found
                if (availableRelTpls != null) {
                    for (RelationshipTemplate tpl : availableRelTpls) {
                        if (tpl.getName().equals(relationshipString)) {
                            RelationshipType rtype = new RelationshipType(tpl.getType(), null, customDef);
                            explicitRelation.put(rtype, relatedTpl);
                            tpl.setTarget(relatedTpl);
                            tpl.setSource(this);
                            relationshipTpl.add(tpl);
                            foundRelationshipTpl = true;
                        }
                    }
                }
                // create relationship template object.
                String relPrfx = EntityType.RELATIONSHIP_PREFIX;
                if (!foundRelationshipTpl) {
                    if (relationship instanceof LinkedHashMap) {
                        relationshipString = (String) ((LinkedHashMap<String, Object>) relationship).get("type");
                        if (relationshipString != null) {
                            if (availableRelTypes != null && !availableRelTypes.isEmpty() &&
                                availableRelTypes.get(relationshipString) != null) {
                                ;
                            } else if (!(relationshipString).startsWith(relPrfx)) {
                                relationshipString = relPrfx + relationshipString;
                            }
                        } else {
                            ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE208", String.format(
                                "MissingRequiredFieldError: \"relationship\" used in template \"%s\" is missing required field \"type\"",
                                relatedTpl.getName())));
                        }
                    }
                    for (RelationshipType rtype : ((NodeType) typeDefinition).getRelationship().keySet()) {
                        if (rtype.getType().equals(relationshipString)) {
                            explicitRelation.put(rtype, relatedTpl);
                            relatedTpl._addRelationshipTemplate(req, rtype.getType(), this);
                        } else if (availableRelTypes != null && !availableRelTypes.isEmpty()) {
                            LinkedHashMap<String, Object> relTypeDef = (LinkedHashMap<String, Object>) availableRelTypes.get(relationshipString);
                            if (relTypeDef != null) {
                                String superType = (String) relTypeDef.get("derived_from");
                                if (superType != null) {
                                    if (!superType.startsWith(relPrfx)) {
                                        superType = relPrfx + superType;
                                    }
                                    if (rtype.getType().equals(superType)) {
                                        explicitRelation.put(rtype, relatedTpl);
                                        relatedTpl._addRelationshipTemplate(req, rtype.getType(), this);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return explicitRelation;
    }

    @SuppressWarnings("unchecked")
    private void _addRelationshipTemplate(RequirementAssignment requirement, String rtype, NodeTemplate source) {
        LinkedHashMap<String, Object> req = new LinkedHashMap<>();
        req.put("relationship", CopyUtils.copyLhmOrAl(requirement.getRelationship()));
        req.put("type", rtype);
        RelationshipTemplate tpl = new RelationshipTemplate(req, rtype, customDef, this, source, getParentNodeTemplate());
        relationshipTpl.add(tpl);
    }

    public ArrayList<RelationshipTemplate> getRelationshipTemplate() {
        return relationshipTpl;
    }

    void _addNext(NodeTemplate nodetpl, RelationshipType relationship) {
        related.put(nodetpl, relationship);
    }

    public ArrayList<NodeTemplate> getRelatedNodes() {
        if (related.isEmpty()) {
            for (Map.Entry<RelationshipType, NodeType> me : ((NodeType) typeDefinition).getRelationship().entrySet()) {
                RelationshipType relation = me.getKey();
                NodeType node = me.getValue();
                for (String tpl : templates.keySet()) {
                    if (tpl.equals(node.getType())) {
                        //BUG.. python has
                        //    self.related[NodeTemplate(tpl)] = relation
                        // but NodeTemplate doesn't have a constructor with just name...
                        //????
                        related.put(new NodeTemplate(tpl, null, null, null, null), relation);
                    }
                }
            }
        }
        return new ArrayList<NodeTemplate>(related.keySet());
    }

    public void validate(/*tosca_tpl=none is not used...*/) {
        _validateCapabilities();
        _validateRequirements();
        _validateProperties(entityTpl, (NodeType) typeDefinition);
        _validateInterfaces();
        for (Property prop : getPropertiesObjects()) {
            prop.validate();
        }
    }

    public Object getPropertyValueFromTemplatesByName(String propertyName) {
        LinkedHashMap<String, Object> nodeObject = (LinkedHashMap<String, Object>) templates.get(name);
        if (nodeObject != null) {
            LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) nodeObject.get(PROPERTIES);
            if (properties != null) {
                return properties.get(propertyName);
            }
        }
        return null;
    }

    private Metadata _metaData() {
        if (entityTpl.get(METADATA) != null) {
            return new Metadata((Map<String, Object>) entityTpl.get(METADATA));
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void _validateRequirements() {
        ArrayList<Object> typeRequires = ((NodeType) typeDefinition).getAllRequirements();
        ArrayList<String> allowedReqs = new ArrayList<>();
        allowedReqs.add("template");
        if (typeRequires != null) {
            for (Object to : typeRequires) {
                LinkedHashMap<String, Object> treq = (LinkedHashMap<String, Object>) to;
                for (Map.Entry<String, Object> me : treq.entrySet()) {
                    String key = me.getKey();
                    Object value = me.getValue();
                    allowedReqs.add(key);
                    if (value instanceof LinkedHashMap) {
                        allowedReqs.addAll(((LinkedHashMap<String, Object>) value).keySet());
                    }
                }

            }
        }

        ArrayList<Object> requires = (ArrayList<Object>) ((NodeType) typeDefinition).getValue(REQUIREMENTS, entityTpl, false);
        if (requires != null) {
            if (!(requires instanceof ArrayList)) {
                ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE209", String.format(
                    "TypeMismatchError: \"requirements\" of template \"%s\" are not of type \"list\"", name)));
            } else {
                for (Object ro : requires) {
                    LinkedHashMap<String, Object> req = (LinkedHashMap<String, Object>) ro;
                    for (Map.Entry<String, Object> me : req.entrySet()) {
                        String rl = me.getKey();
                        Object vo = me.getValue();
                        if (vo instanceof LinkedHashMap) {
                            LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) vo;
                            _validateRequirementsKeys(value);
                            _validateRequirementsProperties(value);
                            allowedReqs.add(rl);
                        }
                    }
                    _commonValidateField(req, allowedReqs, "requirements");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void _validateRequirementsProperties(LinkedHashMap<String, Object> reqs) {
        // TO-DO(anyone): Only occurrences property of the requirements is
        // validated here. Validation of other requirement properties are being
        // validated in different files. Better to keep all the requirements
        // properties validation here.
        for (Map.Entry<String, Object> me : reqs.entrySet()) {
            if (me.getKey().equals("occurrences")) {
                ArrayList<Object> val = (ArrayList<Object>) me.getValue();
                _validateOccurrences(val);
            }

        }
    }

    private void _validateOccurrences(ArrayList<Object> occurrences) {
        DataEntity.validateDatatype("list", occurrences, null, null, null);
        for (Object val : occurrences) {
            DataEntity.validateDatatype("Integer", val, null, null, null);
        }
        if (occurrences.size() != 2 ||
            !(0 <= (int) occurrences.get(0) && (int) occurrences.get(0) <= (int) occurrences.get(1)) ||
            (int) occurrences.get(1) == 0) {
            ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE210", String.format(
                "InvalidPropertyValueError: property has invalid value %s", occurrences.toString())));
        }
    }

    private void _validateRequirementsKeys(LinkedHashMap<String, Object> reqs) {
        for (String key : reqs.keySet()) {
            boolean bFound = false;
            for (int i = 0; i < REQUIREMENTS_SECTION.length; i++) {
                if (key.equals(REQUIREMENTS_SECTION[i])) {
                    bFound = true;
                    break;
                }
            }
            if (!bFound) {
                ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE211", String.format(
                    "UnknownFieldError: \"requirements\" of template \"%s\" contains unknown field \"%s\"", name, key)));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void _validateInterfaces() {
        LinkedHashMap<String, Object> ifaces = (LinkedHashMap<String, Object>) ((NodeType) typeDefinition).getValue(INTERFACES, entityTpl, false);
        if (ifaces != null) {
            for (Map.Entry<String, Object> me : ifaces.entrySet()) {
                String iname = me.getKey();
                LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) me.getValue();
                if (iname.equals(InterfacesDef.LIFECYCLE) || iname.equals(InterfacesDef.LIFECYCLE_SHORTNAME)) {
                    // maybe we should convert [] to arraylist???
                    ArrayList<String> inlo = new ArrayList<>();
                    for (int i = 0; i < InterfacesDef.INTERFACE_NODE_LIFECYCLE_OPERATIONS.length; i++) {
                        inlo.add(InterfacesDef.INTERFACE_NODE_LIFECYCLE_OPERATIONS[i]);
                    }
                    _commonValidateField(value, inlo, "interfaces");
                } else if (iname.equals(InterfacesDef.CONFIGURE) || iname.equals(InterfacesDef.CONFIGURE_SHORTNAME)) {
                    // maybe we should convert [] to arraylist???
                    ArrayList<String> irco = new ArrayList<>();
                    for (int i = 0; i < InterfacesDef.INTERFACE_RELATIONSHIP_CONFIGURE_OPERATIONS.length; i++) {
                        irco.add(InterfacesDef.INTERFACE_RELATIONSHIP_CONFIGURE_OPERATIONS[i]);
                    }
                    _commonValidateField(value, irco, "interfaces");
                } else if (((NodeType) typeDefinition).getInterfaces().keySet().contains(iname)) {
                    _commonValidateField(value, _collectCustomIfaceOperations(iname), "interfaces");
                } else {
                    ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE212", String.format(
                        "UnknownFieldError: \"interfaces\" of template \"%s\" contains unknown field %s", name, iname)));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<String> _collectCustomIfaceOperations(String iname) {
        ArrayList<String> allowedOperations = new ArrayList<>();
        LinkedHashMap<String, Object> nodetypeIfaceDef = (LinkedHashMap<String, Object>) ((NodeType) typeDefinition).getInterfaces().get(iname);
        allowedOperations.addAll(nodetypeIfaceDef.keySet());
        String ifaceType = (String) nodetypeIfaceDef.get("type");
        if (ifaceType != null) {
            LinkedHashMap<String, Object> ifaceTypeDef = null;
            if (((NodeType) typeDefinition).customDef != null) {
                ifaceTypeDef = (LinkedHashMap<String, Object>) ((NodeType) typeDefinition).customDef.get(ifaceType);
            }
            if (ifaceTypeDef == null) {
                ifaceTypeDef = (LinkedHashMap<String, Object>) EntityType.TOSCA_DEF.get(ifaceType);
            }
            allowedOperations.addAll(ifaceTypeDef.keySet());
        }
        // maybe we should convert [] to arraylist???
        ArrayList<String> idrw = new ArrayList<>();
        for (int i = 0; i < InterfacesDef.INTERFACE_DEF_RESERVED_WORDS.length; i++) {
            idrw.add(InterfacesDef.INTERFACE_DEF_RESERVED_WORDS[i]);
        }
        allowedOperations.removeAll(idrw);
        return allowedOperations;
    }

    /**
     * Get all interface details for given node template.<br>
     *
     * @return Map that contains the list of all interfaces and their definitions.
     * If none found, an empty map will be returned.
     */
    public Map<String, List<InterfacesDef>> getAllInterfaceDetailsForNodeType() {
        Map<String, List<InterfacesDef>> interfaceMap = new LinkedHashMap<>();

        // Get custom interface details
        Map<String, Object> customInterfacesDetails = ((NodeType) typeDefinition).getInterfaces();
        // Get native interface details from tosca definitions
        Object nativeInterfaceDetails = TOSCA_DEF.get(InterfacesDef.LIFECYCLE);
        Map<String, Object> allInterfaceDetails = new LinkedHashMap<>();
        allInterfaceDetails.putAll(customInterfacesDetails);
        if (nativeInterfaceDetails != null) {
            allInterfaceDetails.put(InterfacesDef.LIFECYCLE, nativeInterfaceDetails);
        }

        // Process all interface details from combined collection and return an interface Map with
        // interface names and their definitions
        for (Map.Entry<String, Object> me : allInterfaceDetails.entrySet()) {
            ArrayList<InterfacesDef> interfaces = new ArrayList<>();
            String interfaceType = me.getKey();
            Map<String, Object> interfaceValue = (Map<String, Object>) me.getValue();
            if (interfaceValue.containsKey("type")) {
                interfaceType = (String) interfaceValue.get("type");
            }

            for (Map.Entry<String, Object> ve : interfaceValue.entrySet()) {
                // Filter type as this is a reserved key and not an operation
                if (!ve.getKey().equals("type")) {
                    InterfacesDef iface = new InterfacesDef(typeDefinition, interfaceType, this, ve.getKey(), ve.getValue());
                    interfaces.add(iface);
                }
            }
            interfaceMap.put(interfaceType, interfaces);
        }
        return interfaceMap;
    }

    private void _validateFields(LinkedHashMap<String, Object> nodetemplate) {
        for (String ntname : nodetemplate.keySet()) {
            boolean bFound = false;
            for (int i = 0; i < SECTIONS.length; i++) {
                if (ntname.equals(SECTIONS[i])) {
                    bFound = true;
                    break;
                }
            }
            if (!bFound) {
                for (int i = 0; i < SPECIAL_SECTIONS.length; i++) {
                    if (ntname.equals(SPECIAL_SECTIONS[i])) {
                        bFound = true;
                        break;
                    }
                }

            }
            if (!bFound) {
                ThreadLocalsHolder.getCollector().appendValidationIssue(new JToscaValidationIssue("JE213", String.format(
                    "UnknownFieldError: Node template \"%s\" has unknown field \"%s\"", name, ntname)));
            }
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}

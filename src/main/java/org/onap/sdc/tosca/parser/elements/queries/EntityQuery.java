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

import java.util.List;
import java.util.Objects;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.NodeTemplate;
import org.onap.sdc.tosca.parser.api.ToscaTemplate;
import org.onap.sdc.tosca.parser.elements.Metadata;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class describes an entity searched and retrieved by SDC Tosca Parser API It is used as the API input parameter.
 * See the {@link org.onap.sdc.tosca.parser.api.ISdcCsarHelper}
 */
public abstract class EntityQuery {

    private static final Logger logger = LoggerFactory.getLogger(EntityQuery.class.getName());

    private final EntityTemplateType entityType;

    private final SdcTypes nodeTemplateType;

    private final String toscaType;

    private String uUID;

    private String customizationUUID;

    EntityQuery(EntityTemplateType entityType, SdcTypes nodeTemplateType, String toscaType) {
        this.entityType = entityType;
        this.nodeTemplateType = nodeTemplateType;
        this.toscaType = toscaType;
    }

    static boolean isStringMatchingOrNull(String currentUid, String uidInQuery) {
        return uidInQuery == null || uidInQuery.equals(currentUid);
    }

    public static EntityQueryBuilder newBuilder(EntityTemplateType entityTemplateType) {
        return new EntityQueryBuilder(entityTemplateType);
    }

    public static EntityQueryBuilder newBuilder(SdcTypes sdcType) {
        return new EntityQueryBuilder(sdcType);
    }

    public static EntityQueryBuilder newBuilder(String toscaType) {
        return new EntityQueryBuilder(toscaType);
    }

    public abstract List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate);

    public abstract List<IEntityDetails> getEntitiesFromService(ToscaTemplate toscaTemplate);

    public EntityTemplateType getEntityType() {
        return entityType;
    }

    public SdcTypes getNodeTemplateType() {
        return nodeTemplateType;
    }

    public String getToscaType() {
        return toscaType;
    }

    public String getUUID() {
        return uUID;
    }

    void setUUID(String uUID) {
        this.uUID = uUID;
    }

    public String getCustomizationUUID() {
        return customizationUUID;
    }

    void setCustomizationUUID(String customizationUUID) {
        this.customizationUUID = customizationUUID;
    }

    boolean isSearchCriteriaMatched(Metadata metadata, String toscaType, String uuidKeyName, String cuuidKeyName) {
        return Objects.nonNull(metadata)
            && isStringMatchingOrNull(metadata.getValue(uuidKeyName), getUUID())
            && isStringMatchingOrNull(metadata.getValue(cuuidKeyName), getCustomizationUUID())
            && isStringMatchingOrNull(toscaType, getToscaType());
    }

    boolean isSearchCriteriaMatched(Metadata metadata, String toscaType) {
        return isSearchCriteriaMatched(metadata, toscaType, SdcPropertyNames.PROPERTY_NAME_UUID,
            SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID);
    }

    @Override
    public String toString() {
        return String.format("EntityType=%s, nodeTemplateType=%s, toscaType=%s, uUID=%s, customizationUUID=%s",
            entityType, nodeTemplateType, toscaType, uUID, customizationUUID);
    }

    /**
     * Builds instance of EntityQuery object according to provided parameters
     */
    public static class EntityQueryBuilder {

        private static final String GROUPS_NAME_SPACE = ".groups.";
        private static final String POLICIES_NAME_SPACE = ".policies.";

        private EntityQuery entityQuery;

        private EntityQueryBuilder(EntityTemplateType entityTemplateType) {
            switch (entityTemplateType) {
                case NODE_TEMPLATE:
                    entityQuery = new NodeTemplateEntityQuery();
                    break;
                case GROUP:
                    entityQuery = new GroupEntityQuery();
                    break;
                case POLICY:
                    entityQuery = new PolicyEntityQuery();
                    break;
                case ALL:
                    entityQuery = new AllEntitiesQuery();
                    break;
                default:
                    String wrongTypeMsg = (String.format("Wrong entity query type: %s", entityTemplateType));
                    logger.error(wrongTypeMsg);
                    throw new IllegalArgumentException(wrongTypeMsg);
            }
        }

        private EntityQueryBuilder(SdcTypes sdcType) {
            entityQuery = new NodeTemplateEntityQuery(sdcType);
        }

        private EntityQueryBuilder(String toscaType) {
            if (toscaType.contains(GROUPS_NAME_SPACE)) {
                entityQuery = new GroupEntityQuery(toscaType);
            } else if (toscaType.contains(POLICIES_NAME_SPACE)) {
                entityQuery = new PolicyEntityQuery(toscaType);
            } else {
                entityQuery = new NodeTemplateEntityQuery(toscaType);
            }
        }

        public EntityQueryBuilder uUID(String uUID) {
            entityQuery.setUUID(uUID);
            return this;
        }

        public EntityQueryBuilder customizationUUID(String customizationUUID) {
            entityQuery.setCustomizationUUID(customizationUUID);
            return this;
        }

        public EntityQuery build() {
            return entityQuery;
        }
    }


}

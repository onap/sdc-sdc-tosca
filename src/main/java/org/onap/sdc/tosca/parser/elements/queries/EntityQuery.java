package org.onap.sdc.tosca.parser.elements.queries;

import org.apache.commons.lang3.StringUtils;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.toscaparser.api.EntityTemplate;

import java.util.List;

/**
 * This class describes an entity searched and retrieved by SDC Tosca Parser API
 * It is used as the API input parameter. See the {@link org.onap.sdc.tosca.parser.api.ISdcCsarHelper}
 */
public abstract class EntityQuery {

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

    void setUUID(String uUID) {
        this.uUID = uUID;
    }

    void setCustomizationUUID(String customizationUUID) {
        this.customizationUUID = customizationUUID;
    }

    public abstract List<EntityTemplate> searchByTopologyTemplate(TopologyTemplateQuery topologyTemplateQuery);

    public abstract EntityTemplateType getType();

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

    public String getCustomizationUUID() {
        return customizationUUID;
    }

    public boolean searchAllEntities() {
        return StringUtils.isEmpty(toscaType) && nodeTemplateType == null;
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

    /**
     * Builds instance of EntityQuery object according to provided parameters
     */
    public static class EntityQueryBuilder {
        private static final String GROUPS_NAME_SPACE = ".groups.";
        private static final String POLICIES_NAME_SPACE = ".policies.";

        private EntityQuery entityQuery;

        private EntityQueryBuilder(EntityTemplateType entityTemplateType) {
            switch(entityTemplateType) {
                case NODE_TEMPLATE:
                    entityQuery = new NodeTemplateEntityQuery();
                    break;
                case GROUP:
                    entityQuery = new GroupEntityQuery();
                    break;
                case POLICY:
                    entityQuery =  new PolicyEntityQuery();
                    break;
                default:
                    throw new IllegalArgumentException("Wrong entity query type: " + entityTemplateType);
            }
        }

        private EntityQueryBuilder(SdcTypes sdcType) {
            entityQuery = new NodeTemplateEntityQuery(sdcType);
        }

        private EntityQueryBuilder(String toscaType) {
            if (toscaType.contains(GROUPS_NAME_SPACE)) {
                entityQuery = new GroupEntityQuery(toscaType);
            }
            else if (toscaType.contains(POLICIES_NAME_SPACE)) {
                entityQuery = new PolicyEntityQuery(toscaType);
            }
            else {
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

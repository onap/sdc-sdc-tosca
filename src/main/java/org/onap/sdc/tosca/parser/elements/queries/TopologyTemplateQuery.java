package org.onap.sdc.tosca.parser.elements.queries;

import org.onap.sdc.tosca.parser.enums.SdcTypes;

/**
 * This class describes a node template instance containing an entity searched and retrieved by SDC Tosca Parser API
 * It is used as the API input parameter. See the {@link org.onap.sdc.tosca.parser.api.ISdcCsarHelper}
 */
public class TopologyTemplateQuery {

    public void setCustomizationUUID(String customizationUUID) {
        this.customizationUUID = customizationUUID;
    }

    private final SdcTypes sdcType;

    private String customizationUUID;

    private TopologyTemplateQuery(SdcTypes sdcType) {
        this.sdcType = sdcType;
    }

    public static TopologyTemplateQueryBuilder newBuilder(SdcTypes sdcType) {
        return new TopologyTemplateQueryBuilder(sdcType);
    }

    public SdcTypes getNodeTemplateType() {
        return sdcType;
    }

    public String getCustomizationUUID() {
        return customizationUUID;
    }

    public static class TopologyTemplateQueryBuilder {
        private TopologyTemplateQuery topologyTemplateQuery;
        private TopologyTemplateQueryBuilder(SdcTypes sdcType) { topologyTemplateQuery = new TopologyTemplateQuery(sdcType);}

        public TopologyTemplateQueryBuilder customizationUUID(String customizationUUID) {
            topologyTemplateQuery.setCustomizationUUID(customizationUUID);
            return this;
        }

        public TopologyTemplateQuery build() {
            return topologyTemplateQuery;
        }
    }

}

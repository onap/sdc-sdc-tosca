package org.onap.sdc.tosca.parser.elements.queries;

import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class describes a node template instance containing an entity searched and retrieved by SDC Tosca Parser API
 * It is used as the API input parameter. See the {@link org.onap.sdc.tosca.parser.api.ISdcCsarHelper}
 */
public class TopologyTemplateQuery {
    private final static Logger logger = LoggerFactory.getLogger(TopologyTemplateQuery.class.getName());

    void setCustomizationUUID(String customizationUUID) {
        this.customizationUUID = customizationUUID;
    }

    private final SdcTypes sdcType;

    private String customizationUUID;

    private TopologyTemplateQuery(SdcTypes sdcType) {
        this.sdcType = sdcType;
    }

    public static TopologyTemplateQueryBuilder newBuilder(SdcTypes sdcType) {
        if (!SdcTypes.isComplex(sdcType.getValue())) {
            String wrongTypeMsg = (String.format("Given type is not Topology template %s", sdcType));
            logger.error(wrongTypeMsg);
            throw new IllegalArgumentException(wrongTypeMsg);
        }
        return new TopologyTemplateQueryBuilder(sdcType);
    }

    public SdcTypes getNodeTemplateType() {
        return sdcType;
    }

    public String getCustomizationUUID() {
        return customizationUUID;
    }

    public boolean isMatchingSearchCriteria(NodeTemplate nodeTemplate) {
        if (sdcType == SdcTypes.SERVICE) {
            return true;
        }
        Metadata metadata = nodeTemplate.getMetaData();
        return Objects.nonNull(metadata)
                && sdcType.getValue().equals(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE))
                && EntityQuery.isStringMatchingOrNull(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID))
                .test(getCustomizationUUID());
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

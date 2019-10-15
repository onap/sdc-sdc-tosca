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

import org.onap.sdc.tosca.parser.enums.SdcTypes;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This class describes a node template instance containing an entity searched and retrieved by SDC Tosca Parser API
 * It is used as the API input parameter. See the {@link org.onap.sdc.tosca.parser.api.ISdcCsarHelper}
 */
public class TopologyTemplateQuery {
    private static final Logger logger = LoggerFactory.getLogger(TopologyTemplateQuery.class.getName());

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

    public Boolean isMatchingSearchCriteria(NodeTemplate nodeTemplate) {
        boolean isMatched = Objects.nonNull(nodeTemplate.getMetaData()) && isSearchedTemplate(nodeTemplate.getMetaData());
        if(logger.isDebugEnabled()) {
            logger.debug("Node template {} is{} matching search criteria", nodeTemplate.getName(), isMatched ? "" : " not");
        }
        return isMatched;
    }

    public boolean isSameSdcType(Metadata metadata) {
        final String nodeType = metadata.getValue(SdcPropertyNames.PROPERTY_NAME_TYPE);
        return Objects.nonNull(nodeType) && (
                sdcType.getValue().equals(nodeType) || isServiceSearched(nodeType));
    }

    private boolean isSearchedTemplate(Metadata metadata) {
        return isSameSdcType(metadata) &&
                (sdcType == SdcTypes.SERVICE ||
                        //don't check customizationUUID for service
                     EntityQuery.isStringMatchingOrNull(metadata.getValue(SdcPropertyNames.PROPERTY_NAME_CUSTOMIZATIONUUID),
                             getCustomizationUUID()));
    }

    private boolean isServiceSearched(String nodeType) {
        return sdcType == SdcTypes.SERVICE && SdcTypes.isComplex(nodeType);
    }

    @Override
    public String toString() {
        return String.format("sdcType=%s, customizationUUID=%s", sdcType.getValue(), customizationUUID);
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

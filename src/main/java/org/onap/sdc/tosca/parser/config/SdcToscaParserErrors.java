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

package org.onap.sdc.tosca.parser.config;

import java.util.EnumMap;
import java.util.Map;
import org.onap.sdc.tosca.parser.utils.JToscaErrorCodes;

public enum SdcToscaParserErrors {

    BAD_FORMAT, CONFORMANCE_LEVEL_ERROR, FILE_NOT_FOUND, GENERAL_ERROR;

    private static final Map<JToscaErrorCodes, SdcToscaParserErrors> JTOSCA_ERRORS =
        new EnumMap<JToscaErrorCodes, SdcToscaParserErrors>(JToscaErrorCodes.class) {{

            put(JToscaErrorCodes.GENERAL_ERROR, GENERAL_ERROR);

            put(JToscaErrorCodes.PATH_NOT_VALID, FILE_NOT_FOUND);
            //CSAR contents problems
            put(JToscaErrorCodes.MISSING_META_FILE, BAD_FORMAT);
            put(JToscaErrorCodes.INVALID_META_YAML_CONTENT, BAD_FORMAT);
            put(JToscaErrorCodes.ENTRY_DEFINITION_NOT_DEFINED, BAD_FORMAT);
            put(JToscaErrorCodes.MISSING_ENTRY_DEFINITION_FILE, BAD_FORMAT);
            put(JToscaErrorCodes.CSAR_TOSCA_VALIDATION_ERROR, BAD_FORMAT);
            put(JToscaErrorCodes.INVALID_CSAR_FORMAT, BAD_FORMAT);
        }};

    public static SdcToscaParserErrors getSdcErrorByJToscaError(JToscaErrorCodes jToscaErrorCode) {
        return JTOSCA_ERRORS.get(jToscaErrorCode);
    }

}

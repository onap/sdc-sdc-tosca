/*
 * -
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.sdc.toscaparser.api.elements;

import java.util.Map;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ArtifactDef {

    @NonNull
    private final String type;
    @NonNull
    private final String file;
    private final String repository;
    private final String description;
    private final String deploy_path;
    private final String artifact_version;
    private final String checksum;
    private final String checksum_algorithm;
    private final Map<String, Object> properties;

    public ArtifactDef(final Map<String, Object> mapDef) {
        type = (String) mapDef.get("type");
        file = (String) mapDef.get("file");
        repository = (String) mapDef.get("repository");
        description = (String) mapDef.get("description");
        deploy_path = (String) mapDef.get("deploy_path");
        artifact_version = (String) mapDef.get("artifact_version");
        checksum = (String) mapDef.get("checksum");
        checksum_algorithm = (String) mapDef.get("checksum_algorithm");
        properties = (Map<String, Object>) mapDef.get("properties");
    }

}

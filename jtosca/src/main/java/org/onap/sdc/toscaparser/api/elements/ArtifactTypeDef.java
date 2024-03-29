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

package org.onap.sdc.toscaparser.api.elements;

import java.util.LinkedHashMap;
import lombok.Getter;

@Getter
public class ArtifactTypeDef extends StatefulEntityType {

    private String type;
    private LinkedHashMap<String, Object> customDef;
    private LinkedHashMap<String, Object> properties;
    private LinkedHashMap<String, Object> parentArtifacts;

    public ArtifactTypeDef(String type, LinkedHashMap<String, Object> customDef) {
        super(type, ARTIFACT_PREFIX, customDef);

        this.type = type;
        this.customDef = customDef;
        properties = defs != null ? (LinkedHashMap<String, Object>) defs.get(PROPERTIES) : null;
        parentArtifacts = getParentArtifacts();
    }

    private LinkedHashMap<String, Object> getParentArtifacts() {
        LinkedHashMap<String, Object> artifacts = new LinkedHashMap<>();
        String parentArtif = null;
        if (getParentType() != null) {
            parentArtif = getParentType().getType();
        }
        if (parentArtif != null && !parentArtif.isEmpty()) {
            while (!parentArtif.equals("tosca.artifacts.Root")) {
                Object ob = TOSCA_DEF.get(parentArtif);
                artifacts.put(parentArtif, ob);
                parentArtif =
                        (String) ((LinkedHashMap<String, Object>) ob).get("derived_from");
            }
        }
        return artifacts;
    }

    public ArtifactTypeDef getParentType() {
        // Return a artifact entity from which this entity is derived
        if (defs == null) {
            return null;
        }
        String partifactEntity = derivedFrom(defs);
        if (partifactEntity != null) {
            return new ArtifactTypeDef(partifactEntity, customDef);
        }
        return null;
    }

    public Object getArtifact(String name) {
        // Return the definition of an artifact field by name
        if (defs != null) {
            return defs.get(name);
        }
        return null;
    }

}

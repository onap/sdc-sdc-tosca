/*-
 * ============LICENSE_START=======================================================
 * ================================================================================
 * Copyright (C) 2019 Fujitsu Limited. All rights reserved.
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

package org.onap.sdc.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.elements.DataType;
import org.onap.sdc.toscaparser.api.elements.PropertyDef;
import org.onap.sdc.toscaparser.api.elements.constraints.Schema;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ToscaParserDataTypeTest {

    private static ISdcCsarHelper helper = null;
    private static final String TEST_DATATYPE_FILENAME = "csars/dataTypes-test-service.csar";
    private static final String TEST_DATATYPE_TEST1 = "TestType1";
    private static final String TEST_DATATYPE_TEST2 = "TestType2";
    private static final String TEST_DATATYPE_PROPERTY_STR = "strdata";
    private static final String TEST_DATATYPE_PROPERTY_INT = "intdata";
    private static final String TEST_DATATYPE_PROPERTY_LIST = "listdata";
    private static final String TEST_DATATYPE_PROPERTY_TYPE = "type";
    private static final String TEST_DATATYPE_PROPERTY_ENTRY_SCHEMA = "entry_schema";

    @BeforeAll
    public static void setUpClass() {
        try {
            URL resource = GetEntityPortMirroringTest.class.getClassLoader()
                    .getResource(TEST_DATATYPE_FILENAME);
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDataTypes() {
        HashSet<DataType> dataTypes = helper.getDataTypes();
        assertThat(dataTypes, notNullValue());
        assertThat(dataTypes.size(), is(2));

        for(DataType dataType: dataTypes){
            LinkedHashMap<String, PropertyDef> properties;
            PropertyDef property;
            if(dataType.getType().equals(TEST_DATATYPE_TEST1)){
                properties = dataType.getAllProperties();
                property = properties.get(TEST_DATATYPE_PROPERTY_STR);
                assertThat(property,notNullValue());
                assertThat(property.getName(),is(TEST_DATATYPE_PROPERTY_STR));
                assertThat( property.getSchema().get(TEST_DATATYPE_PROPERTY_TYPE),is(Schema.STRING));
            }
            if(dataType.getType().equals(TEST_DATATYPE_TEST2)) {
                properties = dataType.getAllProperties();
                property = properties.get(TEST_DATATYPE_PROPERTY_INT);
                assertThat(property, notNullValue());
                assertThat(property.getName(), is(TEST_DATATYPE_PROPERTY_INT));
                assertThat(property.getSchema().get(TEST_DATATYPE_PROPERTY_TYPE), is(Schema.INTEGER));

                property = properties.get(TEST_DATATYPE_PROPERTY_LIST);
                assertThat(property, notNullValue());
                assertThat(property.getName(), is(TEST_DATATYPE_PROPERTY_LIST));
                assertThat(property.getSchema().get(TEST_DATATYPE_PROPERTY_TYPE), is(Schema.LIST));
                assertThat(property.getSchema().get(TEST_DATATYPE_PROPERTY_ENTRY_SCHEMA), is(TEST_DATATYPE_TEST1));
            }
        }
    }


}

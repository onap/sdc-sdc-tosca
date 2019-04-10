package org.onap.sdc.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Policy;
import org.onap.sdc.toscaparser.api.elements.DataType;
import org.onap.sdc.toscaparser.api.elements.PropertyDef;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ToscaParserDataTypeTest {

    private static ISdcCsarHelper helper = null;

    @BeforeClass
    public static void setUpClass() {
        try {
            URL resource = GetEntityPortMirroringTest.class.getClassLoader()
                    .getResource("csars/service-listed-input-csar.csar");
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDataTypes() {
        List<DataType> dts = helper.getDataTypes();
        assertNotNull(dts);
        assertEquals(2, dts.size());

        LinkedHashMap<String, PropertyDef> props = dts.get(0).getAllProperties();
        PropertyDef prop = props.get("strdata");
        assertNotNull(prop);
        assertEquals("strdata", prop.getName());
        assertEquals("string", prop.getSchema().get("type"));

        props = dts.get(1).getAllProperties();
        prop = props.get("intdata");
        assertNotNull(prop);
        assertEquals("intdata", prop.getName());
        assertEquals("integer", prop.getSchema().get("type"));

        prop = props.get("listdata");
        assertNotNull(prop);
        assertEquals("listdata", prop.getName());
        assertEquals("list", prop.getSchema().get("type"));
        assertEquals("TestType1", prop.getSchema().get("entry_schema"));
    }


}

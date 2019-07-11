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

package org.onap.sdc.impl;

import org.testng.annotations.Test;
import org.onap.sdc.tosca.parser.utils.GeneralUtility;

import static org.testng.Assert.assertTrue;

public class ToscaParserGeneralUtilTest extends SdcToscaParserBasicTest {

    @Test
    public void testVersionCompare() {
        assertTrue(GeneralUtility.conformanceLevelCompare("2", "3.0") < 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("0.5", "0.5") == 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("0.5", "0.6") < 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("1.5", "2.6") < 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("0.2", "0.1") > 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2", "1.15") > 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2", "2.0.0") == 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2.0", "2.0.0.0") == 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2.", "2.0.0.0") == 0);
        assertTrue(GeneralUtility.conformanceLevelCompare("2.0", "2.0.0.2") < 0);
    }
}

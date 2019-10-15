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

import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.testng.annotations.Test;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;

import java.io.File;

import static org.onap.sdc.impl.SdcToscaParserBasicTest.getCsarHelper;

public class MyTest {

	static SdcToscaParserFactory factory;
	static ISdcCsarHelper fdntCsarHelper;

	@Test
	public void testMyCsar() throws SdcToscaParserException {


//		factory = SdcToscaParserFactory.getInstance();
//		fdntCsarHelper = getCsarHelper("csars/service-Oren1-csar-4.csar");
//
//
//		List<NodeTemplate> serviceNodeTemplatesByType = fdntCsarHelper.getServiceNodeTemplatesByType("org.openecomp.nodes.ForwardingPath");
//
//		String target_range = fdntCsarHelper.getNodeTemplatePropertyLeafValue(serviceNodeTemplatesByType.get(0), "target_range");

	}


	protected static ISdcCsarHelper getCsarHelper(String path) throws SdcToscaParserException {
		System.out.println("Parsing CSAR "+path+"...");
		String fileStr1 = SdcToscaParserBasicTest.class.getClassLoader().getResource(path).getFile();
		File file1 = new File(fileStr1);
		ISdcCsarHelper sdcCsarHelper = factory.getSdcCsarHelper(file1.getAbsolutePath());
		return sdcCsarHelper;
	}

}

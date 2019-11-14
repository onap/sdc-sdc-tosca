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
import static org.testng.Assert.*;

import java.io.File;

import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.toscaparser.api.utils.JToscaErrorCodes;



public class ToscaParserErrorHandlingTest extends SdcToscaParserBasicTest {
	
	
	@Test
	public void testMissingMetadata(){
		String csarPath = "csars/service-missing-meta-file.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}
	
	
	@Test
	public void testInvalidYamlContentMeta(){
		String csarPath = "csars/service-invalid-yaml-content-meta.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}
	
	@Test
	public void testEntryDefinitionNotDefined(){
		String csarPath = "csars/service-entry-definition-not-defined.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}

	@Test
	public void testMissingEntryDefinitionFile(){
		String csarPath = "csars/service-missing-entry-definition.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}
	
	//@Test - PA - there are currently no critical erros in JTosca
	public void tesValidationError(){
		String csarPath = "csars/service-invalid-input-args.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}
	
	@Test
	public void testInValidMinConformanceLevelError(){
		String csarPath = "csars/service-invalid-conformence-level.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0003");
	}

	@Test
	public void testIgnoreMaxConformanceLevelNoError(){
		String csarPath = "csars/service-max-conformence-level.csar";
		//TODO: Currently, the conformentce level of the csar for this test is 99 (hard coded). Consider to add ability to replace the configuration in run time.
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
		File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		assertNull(captureThrowable);
	}

	@Test
	public void testVerifyConformanceLevelVersion9(){
		String csarPath = "csars/service-Servicetosca9-csar.csar";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
		File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		assertNull(captureThrowable);
	}
	
	@Test
	public void testFileNotFound(){
		Throwable captureThrowable = captureThrowable("csars/XXX.csar");
		testThrowable(captureThrowable, "TP0001");
	}
	
	@Test
	public void testInvalidCsarFormat(){
		String csarPath = "csars/csar-invalid-zip.zip";
		String fileLocationString = ToscaParserErrorHandlingTest.class.getClassLoader().getResource(csarPath).getFile();
        File file = new File(fileLocationString);
		Throwable captureThrowable = captureThrowable(file.getAbsolutePath());
		testThrowable(captureThrowable, "TP0002");
	}

	private static void testThrowable(Throwable captureThrowable, String expectedCode) {
		assertNotNull(captureThrowable);
		assertTrue(captureThrowable instanceof SdcToscaParserException, "Error thrown is of type "+captureThrowable.getClass().getSimpleName());
		assertEquals(((SdcToscaParserException)captureThrowable).getCode(), expectedCode);
	}
	
	public static Throwable captureThrowable(String csarPath) {
		Throwable result = null;
		try {
			factory.getSdcCsarHelper(csarPath);
		} catch( Throwable throwable ) {
			result = throwable;
		}
		return result;
	}
}

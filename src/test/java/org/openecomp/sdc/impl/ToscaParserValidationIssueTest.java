package org.openecomp.sdc.impl;

import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.toscaparser.api.common.JToscaValidationIssue;
import org.testng.annotations.Test;

public class ToscaParserValidationIssueTest extends SdcToscaParserBasicTest {

	@Test
	public void testNoValidationIssues() throws SdcToscaParserException {
		ISdcCsarHelper rainyCsarHelper = getCsarHelper("csars/service-ServiceFdnt-csar-rainy.csar");//conformance level 3.0

		//List<JToscaValidationIssue> notAnalyzedReport = factory.getNotAnalyzadExceptions();
		//assertEquals( notAnalyzedReport.size(),0);
		List<JToscaValidationIssue> warningsReport = factory.getWarningExceptions();
		assertEquals( warningsReport.size(),0);
		List<JToscaValidationIssue> criticalsReport = factory.getCriticalExceptions();
		assertEquals( criticalsReport.size(),0);
	}
	@Test
	public void testGetLowSinceConformanceLevel() throws SdcToscaParserException {
		ISdcCsarHelper fdntCsarHelperWithInputs = getCsarHelper("csars/service-NfodService-csar.csar");//conformance level 3.0
		//Service level

		List<JToscaValidationIssue> notAnalyzedReport = factory.getNotAnalyzadExceptions();
		assertEquals( notAnalyzedReport.size(),10);
		//JE003 high CL 4.0
		assertEquals( notAnalyzedReport.stream().filter(n->n.getCode().equals("JE003")).collect(Collectors.toList()).size(), 2);
		assertEquals( notAnalyzedReport.stream().filter(n->n.getCode().equals("JE235")).collect(Collectors.toList()).size(), 7);
		assertEquals( notAnalyzedReport.stream().filter(n->n.getCode().equals("JE236")).collect(Collectors.toList()).size(), 1);
		List<JToscaValidationIssue> warningsReport = factory.getWarningExceptions();
		assertEquals( warningsReport.size(),14);
		assertEquals( warningsReport.stream().filter(w->w.getCode().equals("JE006")).collect(Collectors.toList()).size(), 13);
		//JE004 low CL 2.0
		assertEquals( warningsReport.stream().filter(w->w.getCode().equals("JE004")).collect(Collectors.toList()).size(), 1);
		List<JToscaValidationIssue> criticalsReport = factory.getCriticalExceptions();
		assertEquals( criticalsReport.size(),0);
	}
	@Test
	public void testMultiSinceConformanceLevelIssues() throws SdcToscaParserException {
		ISdcCsarHelper Nfod2images = getCsarHelper("csars/service-Nfod2images-csar.csar");//conformance level 4.0
		List<JToscaValidationIssue> notAnalyzedReport = factory.getNotAnalyzadExceptions();
		assertEquals( notAnalyzedReport.size(),17);
		List<JToscaValidationIssue> warningsReport = factory.getWarningExceptions();
		assertEquals( warningsReport.size(),0);
		List<JToscaValidationIssue> criticalsReport = factory.getCriticalExceptions();
		assertEquals( criticalsReport.size(),24);
		//JE006 multy values sinceCsarConformanceLevel
		assertEquals( criticalsReport.stream().filter(c->c.getCode().equals("JE006")).collect(Collectors.toList()).size(), 18);
		assertEquals( criticalsReport.stream().filter(c->c.getCode().equals("JE003")).collect(Collectors.toList()).size(), 6);
	}


}

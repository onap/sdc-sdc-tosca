package org.openecomp.sdc.impl;

import org.openecomp.sdc.toscaparser.api.NodeTemplate;
import org.testng.annotations.Test;
import org.openecomp.sdc.tosca.parser.api.ISdcCsarHelper;
import org.openecomp.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.openecomp.sdc.tosca.parser.impl.SdcToscaParserFactory;

import java.io.File;
import java.util.List;

import static org.openecomp.sdc.impl.SdcToscaParserBasicTest.getCsarHelper;

public class myTest  {

	static SdcToscaParserFactory factory;
	static ISdcCsarHelper fdntCsarHelper;

	@Test
	public void testNoValidationIssues() throws SdcToscaParserException {


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

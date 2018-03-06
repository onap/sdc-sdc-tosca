package org.onap.sdc.tosca.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.onap.sdc.tosca.parser.api.ConformanceLevel;
import org.onap.sdc.tosca.parser.config.ConfigurationManager;
import org.onap.sdc.tosca.parser.config.ErrorInfo;
import org.onap.sdc.tosca.parser.config.JToscaValidationIssueInfo;
import org.onap.sdc.tosca.parser.config.SdcToscaParserErrors;
import org.onap.sdc.tosca.parser.utils.GeneralUtility;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.config.*;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.toscaparser.api.ToscaTemplate;
import org.onap.sdc.toscaparser.api.common.JToscaValidationIssue;
import org.onap.sdc.toscaparser.api.common.JToscaException;
import org.onap.sdc.toscaparser.api.utils.JToscaErrorCodes;
import org.onap.sdc.toscaparser.api.utils.ThreadLocalsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdcToscaParserFactory {
	private static Logger log = LoggerFactory.getLogger(SdcToscaParserFactory.class.getName());

    private static ConfigurationManager configurationManager;
    private static volatile SdcToscaParserFactory instance;
    private List<JToscaValidationIssue> criticalExceptions = new ArrayList<>();
    private List<JToscaValidationIssue> warningExceptions = new ArrayList<>();
    private List<JToscaValidationIssue> notAnalyzadExceptions = new ArrayList<>();
    private SdcToscaParserFactory() {}

    /**
     * Get an SdcToscaParserFactory instance.
     * @return SdcToscaParserFactory instance.
     */
    public static SdcToscaParserFactory getInstance() {
        if (instance == null) {
            synchronized (SdcToscaParserFactory.class) {
                if (instance == null) {
                    instance = new SdcToscaParserFactory();
                    configurationManager = ConfigurationManager.getInstance();
                }
            }
        }
        return instance;
    }

    public static void setConfigurationManager(ConfigurationManager configurationManager) {
        SdcToscaParserFactory.configurationManager = configurationManager;
    }

    /**
     * Get an ISdcCsarHelper object for this CSAR file.
     *
     * @param csarPath - the absolute path to CSAR file.
     * @return ISdcCsarHelper object.
     * @throws SdcToscaParserException - in case the path or CSAR are invalid.
     */
    public ISdcCsarHelper getSdcCsarHelper(String csarPath) throws SdcToscaParserException {
        return init(csarPath, true);
    }

    /**
     * Get an ISdcCsarHelper object for this CSAR file.
     *
     * @param csarPath - the absolute path to CSAR file.
     * @param resolveGetInput - resolve get_input properties
     * @return ISdcCsarHelper object.
     * @throws SdcToscaParserException - in case the path or CSAR are invalid.
     */
    public ISdcCsarHelper getSdcCsarHelper(String csarPath, boolean resolveGetInput) throws SdcToscaParserException {
        return init(csarPath, resolveGetInput);
    }

    private ISdcCsarHelper init(String csarPath, boolean resolveGetInput) throws SdcToscaParserException {
        synchronized (SdcToscaParserFactory.class) {
            ToscaTemplate tosca = null;
            try {
                tosca = new ToscaTemplate(csarPath, null, true, null, resolveGetInput);
            } catch (JToscaException e) {
                throwSdcToscaParserException(e);
            }
            SdcCsarHelperImpl sdcCsarHelperImpl = new SdcCsarHelperImpl(tosca, configurationManager);
            String cSarConformanceLevel = sdcCsarHelperImpl.getConformanceLevel();
            validateCsarVersion(cSarConformanceLevel);
            try {
                handleErrorsByTypes(csarPath, cSarConformanceLevel);
			} catch (JToscaException e) {
                throwSdcToscaParserException(e);
			}
            return sdcCsarHelperImpl;
        }
    }

    private void handleErrorsByTypes(String csarPath, String cSarConformanceLevel) throws JToscaException {
        clearValidationIssuesLists();
    	for(JToscaValidationIssue toscaValidationIssue : ThreadLocalsHolder.getCollector().getValidationIssues().values()){
            List<JToscaValidationIssueInfo> issueInfos = configurationManager.getJtoscaValidationIssueConfiguration().getValidationIssues().get(toscaValidationIssue.getCode());
    		if(issueInfos != null && !issueInfos.isEmpty()){
                JToscaValidationIssueInfo issueInfo = null;
    			issueInfo = issueInfos.stream()
                    .filter(i-> isMatchConformanceLevel(cSarConformanceLevel,i.getSinceCsarConformanceLevel()))
                    .max((i1,i2) -> GeneralUtility.conformanceLevelCompare(i1.getSinceCsarConformanceLevel(), i2.getSinceCsarConformanceLevel()) )
                    .orElse(null);

    			if(issueInfo != null){
                    switch (JToscaValidationIssueType.valueOf(issueInfo.getIssueType())) {
                        case CRITICAL:
                            criticalExceptions.add(toscaValidationIssue);
                            break;
                        case WARNING:
                            warningExceptions.add(toscaValidationIssue);
                            break;
                        default:
                            break;
                    }
                }else{
                    notAnalyzadExceptions.add(toscaValidationIssue);
                }
            }else{//notAnalyzed
                notAnalyzadExceptions.add(toscaValidationIssue);
            }
    	}
    	logErrors(csarPath);
    }

    private void clearValidationIssuesLists(){
        notAnalyzadExceptions.clear();
        criticalExceptions.clear();
        warningExceptions.clear();
    }

    private void logErrors(String inputPath) throws JToscaException{
		//Warnings
		int warningsCount = warningExceptions.size();
		if (warningsCount > 0) {
			log.warn("####################################################################################################");
			log.warn("CSAR Warnings found! CSAR name - {}", inputPath);
			log.warn("ToscaTemplate - verifyTemplate - {} Parsing Warning{} occurred...", warningsCount, (warningsCount > 1 ? "s" : ""));
			for (JToscaValidationIssue info : warningExceptions) {
				log.warn("JTosca Exception [{}]: {}. CSAR name - {}", info.getCode(),info.getMessage(), inputPath);
			}
			log.warn("####################################################################################################");
		}
		//Criticals
		int criticalsCount = criticalExceptions.size();
		if (criticalsCount > 0) {
			log.error("####################################################################################################");
			log.error("ToscaTemplate - verifyTemplate - {} Parsing Critical{} occurred...", criticalsCount, (criticalsCount > 1 ? "s" : ""));
			for (JToscaValidationIssue info : criticalExceptions) {
				log.error("JTosca Exception [{}]: {}. CSAR name - {}", info.getCode(),info.getMessage(), inputPath);
			}
			throw new JToscaException(String.format("CSAR Validation Failed. CSAR name - {}. Please check logs for details.", inputPath), JToscaErrorCodes.CSAR_TOSCA_VALIDATION_ERROR.getValue());
		}
    }
    public List<JToscaValidationIssue> getCriticalExceptions() {
		return criticalExceptions;
	}

	public List<JToscaValidationIssue> getWarningExceptions() {
		return warningExceptions;
	}

	public List<JToscaValidationIssue> getNotAnalyzadExceptions() {
		return notAnalyzadExceptions;
	}


	private void validateCsarVersion(String cSarVersion) throws SdcToscaParserException {
        ConformanceLevel level = configurationManager.getConfiguration().getConformanceLevel();
        String minVersion = level.getMinVersion();
        String maxVersion = level.getMaxVersion();
        if (cSarVersion != null) {
            if ((GeneralUtility.conformanceLevelCompare(cSarVersion, minVersion) < 0) || (GeneralUtility.conformanceLevelCompare(cSarVersion, maxVersion) > 0)) {
                throwConformanceLevelException(minVersion, maxVersion);
            }
        } else {
            throwConformanceLevelException(minVersion, maxVersion);
        }
    }

    private boolean isMatchConformanceLevel(String ValidationIssueVersion, String cSarVersion){
        if (ValidationIssueVersion != null && cSarVersion != null) {
            if ((GeneralUtility.conformanceLevelCompare(ValidationIssueVersion, cSarVersion) >= 0)) {
                return true;
            }
        }
        return false;
    }
    private void throwConformanceLevelException(String minVersion, String maxVersion) throws SdcToscaParserException {
        ErrorInfo errorInfo = configurationManager.getErrorConfiguration().getErrorInfo(SdcToscaParserErrors.CONFORMANCE_LEVEL_ERROR.toString());
        throw new SdcToscaParserException(String.format(errorInfo.getMessage(), minVersion, maxVersion), errorInfo.getCode());
    }

    private void throwSdcToscaParserException(JToscaException e) throws SdcToscaParserException {
        ErrorInfo errorInfo = configurationManager.getErrorConfiguration().getErrorInfo(SdcToscaParserErrors.getSdcErrorByJToscaError(JToscaErrorCodes.getByCode(e.getCode())).toString());
        throw new SdcToscaParserException(errorInfo.getMessage(), errorInfo.getCode());
    }



}
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

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;


public class SdcToscaParserBasicTest extends BaseSetupExtension {

    public static final String VF_CUSTOMIZATION_UUID = "56179cd8-de4a-4c38-919b-bbc4452d2d73";
    static SdcToscaParserFactory factory;
    static ISdcCsarHelper rainyCsarHelperSingleVf;
    static ISdcCsarHelper rainyCsarHelperMultiVfs;
    static ISdcCsarHelper fdntCsarHelper;
    static ISdcCsarHelper fdntCsarHelperWithInputs;
    static ISdcCsarHelper nfodCsarHlper;
    static ISdcCsarHelper ipAssignCsarHelper;
    static ISdcCsarHelper nestedVfcCsarHlper;
    static ISdcCsarHelper nfodNEWCsarHlper;
    static ISdcCsarHelper QAServiceForToscaParserTests;
    static ISdcCsarHelper resolveGetInputCsar;
    static ISdcCsarHelper resolveGetInputCsarFalse;
    static ISdcCsarHelper resolveGetInputCsarQA;
    static ISdcCsarHelper resolveReqsCapsCsarQA;
    static ISdcCsarHelper portMirroring;
    static ISdcCsarHelper csarHelperServiceWithCrs;
    static ISdcCsarHelper csarHelperServicePolicy;
    static ISdcCsarHelper csarHelperVfPolicy;
    static ISdcCsarHelper csarHelperServiceGroups;
    static ISdcCsarHelper csarHelperServiceGroupsInputs;
    static ISdcCsarHelper csarHelperServiceGroupsCapabilities;
    static ISdcCsarHelper csarHelperVfGroupsPolicies;
    static ISdcCsarHelper csarHelperServiceGroupsPolicies;
    static ISdcCsarHelper csarHelperVfInterfaces;
    static ISdcCsarHelper csarHelperServiceAnnotations;
    static ISdcCsarHelper csarHelperServiceAdiodAnnotations;
    static ISdcCsarHelper csarHelperServiceNetworkCloud;
    static Map<String, HashMap<String, List<String>>> fdntCsarHelper_Data;

    @Override
    void setup() throws SdcToscaParserException {
        factory = SdcToscaParserFactory.getInstance();
        fdntCsarHelper = getCsarHelper("csars/service-sunny-flow.csar", false);
        rainyCsarHelperMultiVfs = getCsarHelper("csars/service-ServiceFdnt-csar-rainy.csar", false);
        rainyCsarHelperSingleVf = getCsarHelper("csars/service-ServiceFdnt-csar.csar", false);
        fdntCsarHelperWithInputs = getCsarHelper("csars/service-ServiceFdnt-with-get-input.csar", false);
        nfodCsarHlper = getCsarHelper("csars/service-NfodService-csar.csar", false);
        ipAssignCsarHelper = getCsarHelper("csars/service-Ipassignservice-csar.csar", false);
        nestedVfcCsarHlper = getCsarHelper("csars/service-nested-vfc-csar.csar", false);
        nfodNEWCsarHlper = getCsarHelper("csars/service-Nfod2images-csar.csar", false);
        resolveGetInputCsar = getCsarHelper("csars/service-resolve-get-input-csar.csar");
        resolveGetInputCsarFalse = getCsarHelper("csars/service-resolve-get-input-csar.csar", false);
        resolveGetInputCsarQA = getCsarHelper("csars/service-resolve-get-input-csar_QA.csar");
        QAServiceForToscaParserTests = getCsarHelper("csars/service-ServiceForToscaParserTests-csar.csar");
        resolveReqsCapsCsarQA = getCsarHelper("csars/service-sunny-flow2.csar");
        portMirroring = getCsarHelper("csars/service-PortMirroring.csar");
        csarHelperServiceWithCrs = getCsarHelper("csars/service-CrTestService-csar.csar");
        csarHelperVfPolicy = getCsarHelper("csars/resource-Policy-csar.csar");
        csarHelperServicePolicy = getCsarHelper("csars/service-AlService-csar.csar");
        csarHelperServiceGroups = getCsarHelper("csars/service-Groupstest-csar.csar");
        csarHelperServiceGroupsInputs = getCsarHelper("csars/service-VdbeSrv-csar.csar");
        csarHelperServiceGroupsCapabilities = getCsarHelper("csars/service-VdbePx-csar.csar");
        csarHelperVfGroupsPolicies = getCsarHelper("csars/resource-Vdbe-csar.csar");
        csarHelperServiceGroupsPolicies = getCsarHelper("csars/service-VlanD2dSrv-csar.csar");
        csarHelperServiceAnnotations = getCsarHelper("csars/resource-Sirovinputannotation-csar.csar");
        csarHelperVfInterfaces = getCsarHelper("csars/service-CxSvc-csar.csar");
        csarHelperServiceAdiodAnnotations = getCsarHelper("csars/service-AdiodVmxVpeBvService-csar.csar");
        csarHelperServiceNetworkCloud = getCsarHelper("csars/service-NetworkCloudVnfServiceMock-csar.csar");

        fdntCsarHelper_Data = new HashMap<>() {
            {
                HashMap<String, List<String>> FDNT;

                FDNT = new HashMap<>();
                FDNT.put("VF Name", Arrays.asList("FDNT 1"));
                FDNT.put("capabilities", Arrays.asList(
                    "dnt_fw_rhrg.binding_DNT_FW_INT_DNS_TRUSTED_RVMI",
                    "dnt_fw_rhrg.host_DNT_FW_SERVER",
                    "dnt_fw_rhrg.binding_DNT_FW_CORE_DIRECT_RVMI",
                    "dnt_fw_rhrg.scalable_DNT_FW_SERVER",
                    "dnt_fw_rhrg.endpoint_DNT_FW_SERVER",
                    "dnt_fw_rhrg.binding_DNT_FW_INTERNET_DNS_DIRECT_RVMI",
                    "dnt_fw_rhrg.os_DNT_FW_SERVER",
                    "dnt_fw_rhrg.feature",
                    "dnt_fw_rhrg.binding_DNT_FW_OAM_PROTECTED_RVMI",
                    "dnt_fw_rhrg.binding_DNT_FW_SERVER",
                    "dnt_fw_rhrg.binding_DNT_FW_NIMBUS_HSL_RVMI",
                    "dnt_fw_rsg_si_1.feature"));
                FDNT.put("requirements", Arrays.asList(
                    "DNT_FW_RSG_SI_1.dependency",
                    "DNT_FW_RHRG.dependency",
                    "DNT_FW_RHRG.link_DNT_FW_INTERNET_DNS_DIRECT_RVMI",
                    "DNT_FW_RHRG.link_DNT_FW_CORE_DIRECT_RVMI",
                    "DNT_FW_RHRG.link_DNT_FW_OAM_PROTECTED_RVMI",
                    "DNT_FW_RHRG.link_DNT_FW_INT_DNS_TRUSTED_RVMI",
                    "DNT_FW_RHRG.link_DNT_FW_NIMBUS_HSL_RVMI",
                    "DNT_FW_RSG_SI_1.port",
                    "DNT_FW_RHRG.local_storage_DNT_FW_SERVER"));
                FDNT.put("capabilitiesTypes", Arrays.asList(
                    "tosca.capabilities.network.Bindable",
                    "tosca.capabilities.OperatingSystem",
                    "tosca.capabilities.network.Bindable",
                    "tosca.capabilities.Scalable",
                    "tosca.capabilities.Endpoint.Admin",
                    "tosca.capabilities.network.Bindable",
                    "tosca.capabilities.network.Bindable",
                    "tosca.capabilities.network.Bindable",
                    "tosca.capabilities.Node",
                    "tosca.capabilities.Container",
                    "tosca.nodes.SoftwareComponent",
                    "tosca.capabilities.network.Bindable"));
                FDNT.put("capabilityProperties", Arrays.asList(
                    "dnt_fw_rhrg.binding_DNT_FW_INT_DNS_TRUSTED_RVMI:none",
                    "dnt_fw_rhrg.host_DNT_FW_SERVER:num_cpus,integer,false;",
                    "dnt_fw_rhrg.binding_DNT_FW_CORE_DIRECT_RVMI",
                    "dnt_fw_rhrg.scalable_DNT_FW_SERVER",
                    "dnt_fw_rhrg.endpoint_DNT_FW_SERVER",
                    "dnt_fw_rhrg.binding_DNT_FW_INTERNET_DNS_DIRECT_RVMI",
                    "dnt_fw_rhrg.os_DNT_FW_SERVER",
                    "dnt_fw_rhrg.feature",
                    "dnt_fw_rhrg.binding_DNT_FW_OAM_PROTECTED_RVMI",
                    "dnt_fw_rhrg.binding_DNT_FW_SERVER",
                    "dnt_fw_rhrg.binding_DNT_FW_NIMBUS_HSL_RVMI",
                    "dnt_fw_rsg_si_1.feature"));

                put("FDNT", FDNT);
            }
        };

    }

    protected ISdcCsarHelper getCsarHelper(String path) throws SdcToscaParserException {
        System.out.println("Parsing CSAR " + path + "...");
        String fileStr1 = SdcToscaParserBasicTest.class.getClassLoader().getResource(path).getFile();
        File file1 = new File(fileStr1);
        ISdcCsarHelper sdcCsarHelper = factory.getSdcCsarHelper(file1.getAbsolutePath());
        return sdcCsarHelper;
    }

    protected ISdcCsarHelper getCsarHelper(String path, boolean resolveGetInput) throws SdcToscaParserException {
        System.out.println("Parsing CSAR " + path + "...");
        String fileStr1 = SdcToscaParserBasicTest.class.getClassLoader().getResource(path).getFile();
        File file1 = new File(fileStr1);
        ISdcCsarHelper sdcCsarHelper = factory.getSdcCsarHelper(file1.getAbsolutePath(), resolveGetInput);
        return sdcCsarHelper;
    }

    @Override
    public void close() {
        // Your "after all tests" logic goes here
    }
}

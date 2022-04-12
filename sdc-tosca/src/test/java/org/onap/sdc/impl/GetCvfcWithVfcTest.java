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
import org.junit.jupiter.api.Test;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;


public class GetCvfcWithVfcTest {
    private static final String SERVICE_WITH_SINGLE_VF_CSAR = "csars/service-JennyVtsbcVlanSvc-csar.csar";
    private static final String SERVICE_WITH_DOUBLE_VF_CSAR = "csars/service-Metaswitch1-csar.csar";
    private static final String ABSTRACT_RTP_MSC = "abstract_rtp_msc";
    private static final String ABSTRACT_SSC = "abstract_ssc";
    private static final String ABSTRACT_RTP_MSC_A = "abstract_rtp_msc_a";
    private static final String ABSTRACT_RTP_MSC_B = "abstract_rtp_msc_b";
    private static final String ABSTRACT_SSC_A = "abstract_ssc_a";
    private static final String ABSTRACT_SSC_B = "abstract_ssc_b";
    private ISdcCsarHelper helper;
    private static Logger log = LoggerFactory.getLogger(GetCvfcWithVfcTest.class.getName());

    public  void setUp(String path) {
        try {
            URL resource = GetCvfcWithVfcTest.class.getClassLoader().getResource(path);
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            log.error("Failed to create ISdcCsarHelper object from {}.", path, e);
        }
    }

    @Test
    public void getCvfcsWithVfcsFromServiceWithSingleVfTest(){
        setUp(SERVICE_WITH_SINGLE_VF_CSAR);
        List<IEntityDetails> entities = helper.getVFModule();
        List<String> actualMembersList = newArrayList();
        verifyCvfcWithVfcMembers(entities, actualMembersList, 0, Collections.singletonList(ABSTRACT_RTP_MSC));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 1, Collections.singletonList(ABSTRACT_SSC));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 2, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 3, Collections.singletonList(ABSTRACT_SSC));
    }

    @Test
    public void getCvfcsWithVfcsFromVfiSingleVfTest(){
        setUp(SERVICE_WITH_SINGLE_VF_CSAR);
        List<IEntityDetails> entities = helper.getVFModule("05e77410-a1d8-44fe-8440-b9410c8f98ee");
        List<String> actualMembersList = newArrayList();
        verifyCvfcWithVfcMembers(entities, actualMembersList, 0, Collections.singletonList(ABSTRACT_RTP_MSC));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 1, Collections.singletonList(ABSTRACT_SSC));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 2, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 3, Collections.singletonList(ABSTRACT_SSC));
    }

    @Test
    public void getCvfcsWithVfcsFromServiceWithDuplicateVfTest(){
        setUp(SERVICE_WITH_DOUBLE_VF_CSAR);
        List<IEntityDetails> entities = helper.getVFModule();
        List<String> actualMembersList = newArrayList();
        verifyCvfcWithVfcMembers(entities, actualMembersList, 0, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 1, Arrays.asList(ABSTRACT_RTP_MSC_A, ABSTRACT_RTP_MSC_B));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 2, Arrays.asList(ABSTRACT_RTP_MSC_A, ABSTRACT_RTP_MSC_B));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 3, Collections.singletonList(ABSTRACT_SSC_A));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 4, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 5, Collections.singletonList(ABSTRACT_SSC_A));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 6, Collections.singletonList(ABSTRACT_SSC_B));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 7, Collections.singletonList(ABSTRACT_SSC_B));
    }


    @Test
    public void getCvfcsWithVfcsFromVfiOnServiceWithDuplicateVfTest(){
        setUp(SERVICE_WITH_DOUBLE_VF_CSAR);
        List<IEntityDetails> entities = helper.getVFModule("2b5f00de-8816-465c-b7bc-c36e26775e1e");
        List<String> actualMembersList = newArrayList();
        verifyCvfcWithVfcMembers(entities, actualMembersList, 0, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 1, Arrays.asList(ABSTRACT_RTP_MSC_A, ABSTRACT_RTP_MSC_B));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 2, Collections.singletonList(ABSTRACT_SSC_A));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 3, Collections.singletonList(ABSTRACT_SSC_B));
    }


    private void verifyCvfcWithVfcMembers(List<IEntityDetails> entities, List<String> actualMembersList, int index, List<String> values) {
        List<IEntityDetails> list = entities.get(index).getMemberNodesCVFCWithVFC();
        list.forEach(e -> actualMembersList.add(e.getName()));
        assertThat(actualMembersList).containsOnlyElementsOf(values);
        actualMembersList.clear();
    }


}

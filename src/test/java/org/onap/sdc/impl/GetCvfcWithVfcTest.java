package org.onap.sdc.impl;
import org.junit.Test;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;

import java.net.URL;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;


public class GetCvfcWithVfcTest {
    private static ISdcCsarHelper helper = null;

    public  void setUp(String path) {
        try {
            URL resource = GetEntityTest.class.getClassLoader().getResource(path);
            if (resource != null) {
                helper = SdcToscaParserFactory.getInstance().getSdcCsarHelper(resource.getFile());
            }

        } catch (SdcToscaParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCvfcsWithVfcsFromServiceWithSingleVfTest(){
        setUp("csars/service-JennyVtsbcVlanSvc-csar.csar");
        List<IEntityDetails> entities = helper.getVFModule();
        List<String> actualMembersList = newArrayList();
        verifyCvfcWithVfcMembers(entities, actualMembersList, 0, Collections.singletonList("abstract_rtp_msc"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 1, Collections.singletonList("abstract_ssc"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 2, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 3, Collections.singletonList("abstract_ssc"));
    }

    @Test
    public void getCvfcsWithVfcsFromVfiSingleVfTest(){
        setUp("csars/service-JennyVtsbcVlanSvc-csar.csar");
        List<IEntityDetails> entities = helper.getVFModule("05e77410-a1d8-44fe-8440-b9410c8f98ee");
        List<String> actualMembersList = newArrayList();
        verifyCvfcWithVfcMembers(entities, actualMembersList, 0, Collections.singletonList("abstract_rtp_msc"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 1, Collections.singletonList("abstract_ssc"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 2, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 3, Collections.singletonList("abstract_ssc"));
    }

    @Test
    public void getCvfcsWithVfcsFromServiceWithDuplicateVfTest(){
        setUp("csars/service-Metaswitch1-csar.csar");
        List<IEntityDetails> entities = helper.getVFModule();
        List<String> actualMembersList = newArrayList();
        verifyCvfcWithVfcMembers(entities, actualMembersList, 0, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 1, Arrays.asList("abstract_rtp_msc_a","abstract_rtp_msc_b"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 2, Arrays.asList("abstract_rtp_msc_a","abstract_rtp_msc_b"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 3, Collections.singletonList("abstract_ssc_a"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 4, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 5, Collections.singletonList("abstract_ssc_a"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 6, Collections.singletonList("abstract_ssc_b"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 7, Collections.singletonList("abstract_ssc_b"));
    }


    @Test
    public void getCvfcsWithVfcsFromVfiOnServiceWithDuplicateVfTest(){
        setUp("csars/service-Metaswitch1-csar.csar");
        List<IEntityDetails> entities = helper.getVFModule("2b5f00de-8816-465c-b7bc-c36e26775e1e");
        List<String> actualMembersList = newArrayList();
        verifyCvfcWithVfcMembers(entities, actualMembersList, 0, Collections.emptyList());
        verifyCvfcWithVfcMembers(entities, actualMembersList, 1, Arrays.asList("abstract_rtp_msc_a","abstract_rtp_msc_b"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 2, Collections.singletonList("abstract_ssc_a"));
        verifyCvfcWithVfcMembers(entities, actualMembersList, 3, Collections.singletonList("abstract_ssc_b"));
    }


    private void verifyCvfcWithVfcMembers(List<IEntityDetails> entities, List<String> actualMembersList, int index, List<String> values) {
        List<IEntityDetails> list = entities.get(index).getMemberNodesCVFCWithVFC();
        list.forEach(e -> actualMembersList.add(e.getName()));
        assertThat(actualMembersList).containsOnlyElementsOf(values);
        actualMembersList.clear();
    }


}

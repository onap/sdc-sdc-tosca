package org.onap.sdc.tosca.parser.elements.queries;

import com.google.common.collect.Lists;
import org.onap.sdc.tosca.parser.api.IEntityDetails;
import org.onap.sdc.tosca.parser.elements.EntityDetailsFactory;
import org.onap.sdc.tosca.parser.enums.EntityTemplateType;
import org.onap.sdc.toscaparser.api.Group;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.ToscaTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements EntityQuery object for Groups
 */
public class GroupEntityQuery extends EntityQuery {

    private static final String VF_MODULE_UUID = "vfModuleModelUUID";
    private static final String VF_MODULE_CUSTOMIZATION_UUID = "vfModuleModelCustomizationUUID";

    GroupEntityQuery() {
        super(EntityTemplateType.GROUP, null, null);
    }

    @Override
    public List<IEntityDetails> getEntitiesFromTopologyTemplate(NodeTemplate nodeTemplate) {
        if (nodeTemplate.getSubMappingToscaTemplate() != null) {
            return convertGroupLisToEntityDetailsList(filter(nodeTemplate.getSubMappingToscaTemplate().getGroups()));
        }
        return Lists.newArrayList();
    }

    @Override
    public List<IEntityDetails> getEntitiesFromService(ToscaTemplate toscaTemplate) {
        return convertGroupLisToEntityDetailsList(filter(toscaTemplate.getGroups()));
    }

    GroupEntityQuery(String toscaType) {
        super(EntityTemplateType.GROUP, null, toscaType);
    }

    static List<IEntityDetails> convertGroupLisToEntityDetailsList(Stream<Group> groups) {
        return groups.map(gr->EntityDetailsFactory.createEntityDetails(EntityTemplateType.GROUP, gr))
            .collect(Collectors.toList());
    }

    private Stream<Group> filter(List<Group> groupList) {
        return groupList.stream()
            .filter(gr->isSearchCriteriaMatched(gr.getMetadata(), gr.getType()) ||
                    isSearchCriteriaMatched(gr.getMetadata(), gr.getType(), VF_MODULE_UUID, VF_MODULE_CUSTOMIZATION_UUID));
    }

}

package org.k8loud.executor.actions.openstack;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ParamNotFoundException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.model.Params;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class DeleteInstanceAction extends OpenstackAction {
    private String region;
    private String namePattern;
    private List<String> serverIds;

    public DeleteInstanceAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public DeleteInstanceAction(OpenstackService openstackService, String region, String namePattern,
                                List<String> serverIds) {
        super(openstackService);
        this.region = region;
        this.namePattern = namePattern;
        this.serverIds = serverIds;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        namePattern = params.getOptionalParam("namePattern", null);
        serverIds = params.getOptionalParamAsListOfStrings("serverIds", Collections.emptyList());
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException, ValidationException {
        final boolean serverIdsIsEmpty = CollectionUtils.isEmpty(serverIds);
        if (namePattern == null && serverIdsIsEmpty) {
            throw new ParamNotFoundException("Either namePattern or serverIds should be provided");
        }
        if (serverIdsIsEmpty) {
            return openstackService.deleteServers(region, namePattern);
        }
        return openstackService.deleteServers(region, serverIds);
    }
}

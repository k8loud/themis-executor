package org.k8loud.executor.actions.openstack;

import lombok.EqualsAndHashCode;
import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class DeleteInstanceAction extends OpenstackAction {
    private String region;
    private String name;
    private List<String> serverIds;

    public DeleteInstanceAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public DeleteInstanceAction(OpenstackService openstackService, String region, String name, List<String> serverIds) {
        super(openstackService);
        this.region = region;
        this.name = name;
        this.serverIds = serverIds;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        name = params.getRequiredParam("name");
        serverIds = params.getOptionalParamAsListOfStrings("serverIds", Collections.emptyList());
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException, ValidationException {
        if (serverIds.isEmpty()){
            return openstackService.deleteServers(region, name);
        }

        return openstackService.deleteServers(region, serverIds);
    }
}

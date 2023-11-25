package org.k8loud.executor.action.openstack;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

public class DeleteInstanceAction extends OpenstackAction {
    private String region;
    private String name;

    public DeleteInstanceAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public DeleteInstanceAction(OpenstackService openstackService, String region, String name) {
        super(openstackService);
        this.region = region;
        this.name = name;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        name = params.getRequiredParam("name");
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException, ValidationException {
        return openstackService.deleteServers(region, name);
    }

}

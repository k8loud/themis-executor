package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class CreateSecurityGroupAction extends OpenstackAction{
    private String region;
    private String name;
    private String description;

    public CreateSecurityGroupAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        name = params.getRequiredParam("name");
        description = params.getRequiredParam("description");
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return openstackService.createSecurityGroup(region, name, description);
    }
}

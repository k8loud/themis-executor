package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class DeleteServerSnapshotAction extends OpenstackAction {
    private String region;
    private String serverId;
    private boolean keepOneSnapshot;

    public DeleteServerSnapshotAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        keepOneSnapshot = params.getOptionalParamAsBoolean("keepOneSnapshot", "true");
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return openstackService.deleteTheOldestServerSnapshot(region, serverId, keepOneSnapshot);
    }
}

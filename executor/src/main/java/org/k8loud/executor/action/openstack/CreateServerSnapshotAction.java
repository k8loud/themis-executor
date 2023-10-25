package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class CreateServerSnapshotAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String snapshotName;
    private boolean stopInstance;

    public CreateServerSnapshotAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        snapshotName = params.getOptionalParam("snapshotName", null);
        stopInstance = Boolean.parseBoolean(params.getOptionalParam("stopInstance", "false"));
    }

    @Override
    protected void performOpenstackAction() throws OpenstackException {
        openstackService.createServerSnapshot(region, serverId, snapshotName, stopInstance);
    }
}

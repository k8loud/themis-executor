package org.k8loud.executor.actions.openstack;

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
        stopInstance = params.getOptionalParamAsBoolean("stopInstance", "false");
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return openstackService.createServerSnapshot(region, serverId, snapshotName, stopInstance);
    }
}

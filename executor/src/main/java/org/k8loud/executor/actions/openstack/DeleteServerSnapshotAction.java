package org.k8loud.executor.actions.openstack;

import data.Params;
import lombok.Builder;
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

    @Builder
    public DeleteServerSnapshotAction(OpenstackService openstackService,
                                      String region, String serverId, boolean keepOneSnapshot) {
        super(openstackService);
        this.region = region;
        this.serverId = serverId;
        this.keepOneSnapshot = keepOneSnapshot;
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

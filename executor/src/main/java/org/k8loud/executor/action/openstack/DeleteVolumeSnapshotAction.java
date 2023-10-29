package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class DeleteVolumeSnapshotAction extends OpenstackAction{
    private String region;
    private String volumeId;
    private boolean keepOneSnapshot;

    public DeleteVolumeSnapshotAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        volumeId = params.getRequiredParam("volumeId");
        keepOneSnapshot = params.getOptionalParamAsBoolean("keepOneSnapshot", "true");
    }

    @Override
    protected void performOpenstackAction() throws OpenstackException {
        openstackService.deleteTheOldestVolumeSnapshot(region, volumeId, keepOneSnapshot);
    }
}

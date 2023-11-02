package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class CreateVolumeSnapshotAction extends OpenstackAction{
    private String region;
    private String volumeId;
    private String snapshotName;

    public CreateVolumeSnapshotAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        volumeId = params.getRequiredParam("volumeId");
        snapshotName = params.getOptionalParam("snapshotName", null);
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return openstackService.createVolumeSnapshot(region, volumeId, snapshotName);
    }
}

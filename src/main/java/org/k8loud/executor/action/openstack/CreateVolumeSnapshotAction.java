package org.k8loud.executor.action.openstack;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

public class CreateVolumeSnapshotAction extends OpenstackAction{
    private String region;
    private String volumeId;
    private String snapshotName;

    public CreateVolumeSnapshotAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public CreateVolumeSnapshotAction(OpenstackService openstackService,
                                      String region, String volumeId, String snapshotName) {
        super(openstackService);
        this.region = region;
        this.volumeId = volumeId;
        this.snapshotName = snapshotName;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        volumeId = params.getRequiredParam("volumeId");
        snapshotName = params.getOptionalParam("snapshotName", null);
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException {
        return openstackService.createVolumeSnapshot(region, volumeId, snapshotName);
    }
}

package org.k8loud.executor.actions.openstack;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.model.Params;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

@EqualsAndHashCode
public class CreateServerSnapshotAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String snapshotName;
    private boolean stopInstance;

    public CreateServerSnapshotAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public CreateServerSnapshotAction(OpenstackService openstackService,
                                      String region, String serverId, String snapshotName, boolean stopInstance) {
        super(openstackService);
        this.region = region;
        this.serverId = serverId;
        this.snapshotName = snapshotName;
        this.stopInstance = stopInstance;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        snapshotName = params.getOptionalParam("snapshotName", null);
        stopInstance = params.getOptionalParamAsBoolean("stopInstance", "false");
    }

    @Override
    protected Map<String, Object> executeBody() throws OpenstackException, ValidationException {
        return openstackService.createServerSnapshot(region, serverId, snapshotName, stopInstance);
    }
}

package org.k8loud.executor.action.openstack;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;

import java.util.Map;

public class VerticalScalingAction extends Action {
    private String region;
    private String serverId;
    private double diskResizeValue;
    private double ramResizeValue;
    private double vcpusResizeValue;
    private final NovaApi novaApi;

    public VerticalScalingAction(Params params) throws ActionException {
        super(params);
        novaApi = null;
// FIXME: 'java.lang.IllegalStateException: Unable to load cache item' when this object is instantiated in tests
//        novaApi = OpenstackHelper.getNovaApi("openstack-nova", "demo:demo", "devstack", "http://xxx.xxx.xxx.xxx:5000/v2.0/");
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        diskResizeValue = Double.parseDouble(params.getOptionalParam("diskResizeValue", "1"));
        ramResizeValue = Double.parseDouble(params.getOptionalParam("ramResizeValue", "1"));
        vcpusResizeValue = Double.parseDouble(params.getOptionalParam("vcpusResizeValue", "1"));
    }

    @Override
    public ExecutionRS perform() {
        ServerApi serverApi = novaApi.getServerApi(region);
        Server server = serverApi.get(serverId);

        Flavor currentFlavor = (Flavor) server.getFlavor();
        Flavor newFlavor = Flavor.builder()
                .fromFlavor(currentFlavor)
                .disk((int) (currentFlavor.getDisk() * diskResizeValue))
                .ram((int) (currentFlavor.getRam() * ramResizeValue))
                .vcpus((int) (currentFlavor.getVcpus() * vcpusResizeValue))
                .build();

        serverApi.resize(serverId, newFlavor.getId());
        serverApi.confirmResize(serverId);
        return ExecutionRS.builder()
                .result("Success")
                .exitCode(ExecutionExitCode.OK)
                .build();
    }
}

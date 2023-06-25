package org.k8loud.executor.action.openstack;

import data.ExecutionExitCode;
import data.ExecutionRS;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.k8loud.executor.action.Action;

import java.util.Map;

public class VertivalScalingAction extends Action {
    private final String region;
    public final String serverId;
    private final NovaApi novaApi;

    public VertivalScalingAction(Map<String, String> params) {
        super(params);
        region = params.get("region");
        serverId = params.get("serverId");
        novaApi = OpenstackHelper.getNovaApi("openstack-nova", "demo:demo", "devstack", "http://xxx.xxx.xxx.xxx:5000/v2.0/");
    }

    @Override
    public ExecutionRS perform() {
        ServerApi serverApi = novaApi.getServerApi(region);
        Server server = serverApi.get(serverId);

        Flavor currentFlavor = (Flavor) server.getFlavor();
        Flavor newFlavor = Flavor.builder()
                .fromFlavor(currentFlavor)
                .disk((int) (currentFlavor.getDisk() * Double.parseDouble(params.get("diskResizeValue"))))
                .ram((int) (currentFlavor.getRam() * Double.parseDouble(params.get("ramResizeValue"))))
                .vcpus((int) (currentFlavor.getVcpus() * Double.parseDouble(params.get("vcpusResizeValue"))))
                .build();


        serverApi.resize(serverId, newFlavor.getId());
        serverApi.confirmResize(serverId);
        return ExecutionRS.builder()
                .result("Success")
                .exitCode(ExecutionExitCode.OK)
                .build();
    }
}
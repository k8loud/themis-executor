package org.k8loud.executor.action.openstack;

import data.ExecutionExitCode;
import data.ExecutionRS;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.k8loud.executor.action.Action;

import java.util.Map;

public class HorizontalScalingAction extends Action {
    private String region;
    private String serverId;
    private final NovaApi novaApi;

    public HorizontalScalingAction(Map<String, String> params) {
        super(params);
        novaApi = OpenstackHelper.getNovaApi("openstack-nova", "demo:demo", "devstack", "http://xxx.xxx.xxx.xxx:5000/v2.0/");
    }

    @Override
    public void unpackParams(Map<String, String> params) {
        region = params.get("region");
        serverId = params.get("serverId");
    }

    @Override
    public ExecutionRS perform() {
        ServerApi serverApi = novaApi.getServerApi(region);
        FlavorApi flavorApi = novaApi.getFlavorApi(region);

        Server server = serverApi.get(serverId);
        Resource resource = server.getFlavor();
        Flavor newFlavor = flavorApi.create((Flavor) resource);

        String newOrUpdatedImageName = serverApi.createImageFromServer("newImageName", serverId);
        serverApi.create("ServerNameToCreate", newOrUpdatedImageName, newFlavor.getId());
        return ExecutionRS.builder()
                .result("Success")
                .exitCode(ExecutionExitCode.OK)
                .build();
    }
}

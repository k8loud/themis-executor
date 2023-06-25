package org.k8loud.executor.action.openstack;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import data.ExecutionExitCode;
import data.ExecutionRS;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.k8loud.executor.action.Action;

import java.util.Map;

public class HorizontalScalingAction extends Action {
    private static final String REGION = "region";
    public static final String SERVER_ID = "id";
    private final NovaApi novaApi;

    public HorizontalScalingAction(Map<String, String> params) {
        super(params);
        novaApi = createNovaApi();
    }

    @Override
    public ExecutionRS perform() {
        ServerApi serverApi = novaApi.getServerApi(REGION);
        FlavorApi flavorApi = novaApi.getFlavorApi(REGION);

        Server server = serverApi.get(SERVER_ID);
        Resource resource = server.getFlavor();
        Flavor newFlavor = flavorApi.create((Flavor) resource);

        String newOrUpdatedImageName = serverApi.createImageFromServer("newImageName", SERVER_ID);
        serverApi.create("ServerNameToCreate", newOrUpdatedImageName, newFlavor.getId());
        return ExecutionRS.builder()
                .result("Success")
                .exitCode(ExecutionExitCode.OK)
                .build();
    }


    private NovaApi createNovaApi() {
        ImmutableSet<Module> modules = ImmutableSet.of(new SLF4JLoggingModule());

        // Please refer to 'Keystone v2-v3 authentication' section for complete authentication use case
        String provider = "openstack-nova";
        String identity = "demo:demo"; // tenantName:userName
        String credential = "devstack";

        return ContextBuilder.newBuilder(provider)
                .endpoint("http://xxx.xxx.xxx.xxx:5000/v2.0/")
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(NovaApi.class);
    }
}

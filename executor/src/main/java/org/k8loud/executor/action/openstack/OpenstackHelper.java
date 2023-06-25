package org.k8loud.executor.action.openstack;

import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;

import java.util.Set;

public class OpenstackHelper {
    private OpenstackHelper() {
    }

    public static NovaApi getNovaApi(String provider, String identity, String credential, String endpoint) {
        Set<Module> modules = Set.of(new SLF4JLoggingModule());

        return ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(NovaApi.class);
    }
}

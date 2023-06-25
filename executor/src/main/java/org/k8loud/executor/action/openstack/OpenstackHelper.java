package org.k8loud.executor.action.openstack;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;

public class OpenstackHelper {
    public static NovaApi getNovaApi(String provider, String identity, String credential, String endpoint) {
        ImmutableSet<Module> modules = ImmutableSet.of(new SLF4JLoggingModule());

        return ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(NovaApi.class);
    }

}

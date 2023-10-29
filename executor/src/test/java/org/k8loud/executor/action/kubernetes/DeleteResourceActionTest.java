package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteResourceActionTest extends BaseTest {
    public static Stream<Params> provideDeletePodParams() {
        return Stream.of(
                new Params(Map.of("resourceType", "Pod", "resourceName", "pod1", "namespace", "test")),
                new Params(Map.of("resourceType", "Pod", "resourceName", "pod1", "namespace", "test", "gracePeriodSeconds", "60")));
    }

    @ParameterizedTest
    @MethodSource("provideDeletePodParams")
    void testDeletingPod(Params params) throws ActionException {
        // given
        final String resourceName = params.getRequiredParam("resourceName");
        final String namespace = params.getRequiredParam("namespace");
        Pod pod = new PodBuilder().withNewMetadata().withName(resourceName).endMetadata()
                .withNewSpec()
                    .addNewContainer()
                        .withName(resourceName)
                        .withImage("nginx:1.7.9")
                        .addNewPort().withContainerPort(80).endPort()
                    .endContainer()
                .endSpec()
                .build();
        client.pods().inNamespace(namespace).resource(pod).create();
        Assertions.assertNotNull(client.pods().inNamespace(namespace).withName(resourceName).get());

        // when
        Action action = new DeleteResourceAction(params, kubernetesService);
        ExecutionRS rs = action.perform();
        Pod pod1 = client.pods().inNamespace(namespace).withName(resourceName).get();

        // then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        Assertions.assertNull(pod1);
    }

    public static Stream<Params> provideDeleteDeploymentParams() {
        return Stream.of(
                new Params(Map.of("resourceType", "Deployment", "resourceName", "cm1", "namespace", "test")),
                new Params(Map.of("resourceType", "Deployment", "resourceName", "cm1", "namespace", "test", "gracePeriodSeconds", "60")));
    }

    @ParameterizedTest
    @MethodSource("provideDeleteDeploymentParams")
    void testDeletingDeployment(Params params) throws ActionException {
        // given
        final String resourceName = params.getRequiredParam("resourceName");
        final String namespace = params.getRequiredParam("namespace");
        Deployment deployment = new DeploymentBuilder().withNewMetadata().withName(resourceName).endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewTemplate()
                        .withNewMetadata().addToLabels("app", "httpd").endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName("busybox")
                                .withImage("busybox")
                                .withCommand("sleep","36000")
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();
        client.apps().deployments().inNamespace(namespace).resource(deployment).create();
        Assertions.assertNotNull(client.apps().deployments().inNamespace(namespace).withName(resourceName).get());

        // when
        Action action = new DeleteResourceAction(params, kubernetesService);
        ExecutionRS rs = action.perform();
        Deployment deployment1 = client.apps().deployments().inNamespace(namespace).withName(resourceName).get();

        // then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        Assertions.assertNull(deployment1);
    }

    public static Stream<Params> provideDeleteConfigMapParams() {
        return Stream.of(
                new Params(Map.of("resourceType", "ConfigMap", "resourceName", "cm1", "namespace", "test")),
                new Params(Map.of("resourceType", "ConfigMap", "resourceName", "cm1", "namespace", "test", "gracePeriodSeconds", "60")));
    }

    @ParameterizedTest
    @MethodSource("provideDeleteConfigMapParams")
    void testDeletingConfigMap(Params params) throws ActionException {
        // given
        final String resourceName = params.getRequiredParam("resourceName");
        final String namespace = params.getRequiredParam("namespace");
        ConfigMap cm = new ConfigMapBuilder().withNewMetadata().withName(resourceName).endMetadata()
                .addToData("1", "one")
                .addToData("2", "two")
                .addToData("3", "three")
                .build();
        client.configMaps().inNamespace(namespace).resource(cm).create();
        Assertions.assertNotNull(client.configMaps().inNamespace(namespace).withName(resourceName).get());

        // when
        Action action = new DeleteResourceAction(params, kubernetesService);
        ExecutionRS rs = action.perform();
        ConfigMap cm1 = client.configMaps().inNamespace(namespace).withName(resourceName).get();

        // then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        Assertions.assertNull(cm1);
    }
}

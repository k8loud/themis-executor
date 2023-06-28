package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import io.fabric8.kubernetes.api.model.apps.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@EnableKubernetesMockClient(crud = true)
class HorizontalScalingActionTest {
    KubernetesClient client;

    public static Stream<Map<String, String>> provideStatefulSetScalingParams() {
        return Stream.of(
                Map.of("resourceType", "StatefulSet", "resourceName", "depl1", "namespace", "test", "replicas", "3"),
                Map.of("resourceType", "StatefulSet", "resourceName", "depl1", "namespace", "test", "replicas", "1"),
                Map.of("resourceType", "StatefulSet", "resourceName", "depl1", "namespace", "test", "replicas", "2")
        );
    }

    @ParameterizedTest
    @MethodSource("provideStatefulSetScalingParams")
    void testScalingStatefulSet(Map<String, String> params) {
        // given
        StatefulSet sts = new StatefulSetBuilder()
                .withNewMetadata()
                .withName("depl1")
                .withNamespace("test")
                .withResourceVersion("1")
                .endMetadata()
                .withNewSpec()
                .withReplicas(3)
                .endSpec()
                .withNewStatus()
                .withReplicas(2)
                .endStatus()
                .build();
        client.resource(sts).create();

        //when
        Action action = new HorizontalScalingAction(params, client);
        ExecutionRS rs = action.perform();
        StatefulSet sts1 = client.apps().statefulSets().withName(params.get("resourceName")).get();

        //then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertNotNull(sts1);
        assertNotNull(sts1.getSpec());
        assertNotNull(sts1.getStatus());
        assertEquals(Integer.parseInt(params.get("replicas")), sts1.getSpec().getReplicas().intValue());
    }

    public static Stream<Map<String, String>> provideDeploymentScalingParams() {
        return Stream.of(
                Map.of("resourceType", "Deployment", "resourceName", "depl1", "namespace", "test", "replicas", "3"),
                Map.of("resourceType", "Deployment", "resourceName", "depl1", "namespace", "test", "replicas", "1"),
                Map.of("resourceType", "Deployment", "resourceName", "depl1", "namespace", "test", "replicas", "2")
        );
    }

    @ParameterizedTest
    @MethodSource("provideDeploymentScalingParams")
    void testScalingDeployment(Map<String, String> params) {
        // given
        Deployment depl = new DeploymentBuilder()
                .withNewMetadata()
                .withName("depl1")
                .withNamespace("test")
                .withResourceVersion("1")
                .endMetadata()
                .withNewSpec()
                .withReplicas(3)
                .endSpec()
                .withNewStatus()
                .withReplicas(2)
                .endStatus()
                .build();
        client.resource(depl).create();

        //when
        Action action = new HorizontalScalingAction(params, client);
        ExecutionRS rs = action.perform();
        Deployment depl1 = client.apps().deployments().withName(params.get("resourceName")).get();

        //then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertNotNull(depl1);
        assertNotNull(depl1.getSpec());
        assertNotNull(depl1.getStatus());
        assertEquals(Integer.parseInt(params.get("replicas")), depl1.getSpec().getReplicas().intValue());
    }

    public static Stream<Map<String, String>> provideReplicasetScalingParams() {
        return Stream.of(
                Map.of("resourceType", "ReplicaSet", "resourceName", "repl1", "namespace", "test", "replicas", "3"),
                Map.of("resourceType", "ReplicaSet", "resourceName", "repl1", "namespace", "test", "replicas", "1"),
                Map.of("resourceType", "ReplicaSet", "resourceName", "repl1", "namespace", "test", "replicas", "2")
        );
    }

    @ParameterizedTest
    @MethodSource("provideReplicasetScalingParams")
    void testScalingReplicaSet(Map<String, String> params) {

        // given
        ReplicaSet rss = new ReplicaSetBuilder()
                .withNewMetadata()
                .withName("repl1")
                .withNamespace("test")
                .withResourceVersion("1")
                .endMetadata()
                .withNewSpec()
                .withReplicas(2)
                .endSpec()
                .withNewStatus()
                .withReplicas(2)
                .endStatus()
                .build();
        client.resource(rss).create();

        // when
        Action action = new HorizontalScalingAction(params, client);
        ExecutionRS rs = action.perform();
        ReplicaSet repl = client.apps().replicaSets().withName(params.get("resourceName")).get();

        // then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertNotNull(repl);
        assertNotNull(repl.getSpec());
        assertNotNull(repl.getStatus());
        assertEquals(Integer.parseInt(params.get("replicas")), repl.getSpec().getReplicas().intValue());
    }
}
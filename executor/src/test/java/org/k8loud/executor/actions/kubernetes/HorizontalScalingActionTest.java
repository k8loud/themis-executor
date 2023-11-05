package org.k8loud.executor.actions.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import io.fabric8.kubernetes.api.model.apps.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@EnableKubernetesMockClient(crud = true)
class HorizontalScalingActionTest {
    KubernetesClient client;

    public static Stream<Params> provideStatefulSetScalingParams() {
        return Stream.of(
                new Params(Map.of("resourceType", "StatefulSet", "resourceName", "depl1", "namespace", "test", "replicas", "3")),
                new Params(Map.of("resourceType", "StatefulSet", "resourceName", "depl1", "namespace", "test", "replicas", "1")),
                new Params(Map.of("resourceType", "StatefulSet", "resourceName", "depl1", "namespace", "test", "replicas", "2")));
    }

    @ParameterizedTest
    @MethodSource("provideStatefulSetScalingParams")
    void testScalingStatefulSet(Params params) throws ActionException {
        // given
        StatefulSet sts = new StatefulSetBuilder().withNewMetadata()
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
        StatefulSet sts1 = client.apps().statefulSets().withName(params.getRequiredParam("resourceName")).get();

        //then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertNotNull(sts1);
        assertNotNull(sts1.getSpec());
        assertNotNull(sts1.getStatus());
        assertEquals(Integer.parseInt(params.getRequiredParam("replicas")), sts1.getSpec().getReplicas().intValue());
    }

    public static Stream<Params> provideDeploymentScalingParams() {
        return Stream.of(new Params(
                        Map.of("resourceType", "Deployment", "resourceName", "depl1", "namespace", "test", "replicas"
                                , "3")),
                new Params(
                        Map.of("resourceType", "Deployment", "resourceName", "depl1", "namespace", "test", "replicas",
                                "1")), new Params(
                        Map.of("resourceType", "Deployment", "resourceName", "depl1", "namespace", "test", "replicas",
                                "2")));
    }

    @ParameterizedTest
    @MethodSource("provideDeploymentScalingParams")
    void testScalingDeployment(Params params) throws ActionException {
        // given
        Deployment depl = new DeploymentBuilder().withNewMetadata()
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
        client.resource(depl)
                .create();

        //when
        Action action = new HorizontalScalingAction(params, client);
        ExecutionRS rs = action.perform();
        Deployment depl1 = client.apps()
                .deployments()
                .withName(params.getRequiredParam("resourceName"))
                .get();

        //then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertNotNull(depl1);
        assertNotNull(depl1.getSpec());
        assertNotNull(depl1.getStatus());
        assertEquals(Integer.parseInt(params.getRequiredParam("replicas")), depl1.getSpec()
                .getReplicas()
                .intValue());
    }

    public static Stream<Params> provideReplicasetScalingParams() {
        return Stream.of(new Params(
                        Map.of("resourceType", "ReplicaSet", "resourceName", "repl1", "namespace", "test", "replicas"
                                , "3")),
                new Params(
                        Map.of("resourceType", "ReplicaSet", "resourceName", "repl1", "namespace", "test", "replicas",
                                "1")), new Params(
                        Map.of("resourceType", "ReplicaSet", "resourceName", "repl1", "namespace", "test", "replicas",
                                "2")));
    }

    @ParameterizedTest
    @MethodSource("provideReplicasetScalingParams")
    void testScalingReplicaSet(Params params) throws ActionException {

        // given
        ReplicaSet rss = new ReplicaSetBuilder().withNewMetadata()
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
        client.resource(rss)
                .create();

        // when
        Action action = new HorizontalScalingAction(params, client);
        ExecutionRS rs = action.perform();
        ReplicaSet repl = client.apps()
                .replicaSets()
                .withName(params.getRequiredParam("resourceName"))
                .get();

        // then
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertNotNull(repl);
        assertNotNull(repl.getSpec());
        assertNotNull(repl.getStatus());
        assertEquals(Integer.parseInt(params.getRequiredParam("replicas")), repl.getSpec()
                .getReplicas()
                .intValue());
    }
}
package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableKubernetesMockClient(crud = true)
class UpdateConfigMapActionTest {

    KubernetesClient client;

    public static Stream<Arguments> provideUpdateValuesConfigMapParams() {
        return Stream.of(
                Arguments.of(new Params(Map.of("resourceName", "cm1", "namespace", "test", "k1", "k1", "v1", "v2")),
                        Map.of("k1", "v1"), Map.of("k1", "v2"), 1),
                Arguments.of(new Params(Map.of("resourceName", "cm1", "namespace", "test", "k1", "k1", "v1", "v2")),
                        Map.of("k1", "v1", "k2", "v2", "k3", "v3"), Map.of("k1", "v2", "k2", "v2", "k3", "v3"), 3),
                Arguments.of(new Params(Map.of("resourceName", "cm1", "namespace", "test", "k1", "k1", "v1", "v1")),
                        Map.of("k2", "v2", "k3", "v3"), Map.of("k1", "v1", "k2", "v2", "k3", "v3"), 3));

    }

    @ParameterizedTest
    @MethodSource("provideUpdateValuesConfigMapParams")
    void testUpdateValuesConfigMap(Params params, Map<String, String> data, Map<String, String> newData,
                                   int finalLength) throws ActionException {
        // given
        ConfigMap cm = new ConfigMapBuilder().withNewMetadata()
                .withName("cm1")
                .withNamespace("test")
                .withResourceVersion("1")
                .endMetadata()
                .withData(data)
                .build();

        client.resource(cm).create();

        // when
        Action action = new UpdateConfigMapAction(params, client);
        ExecutionRS rs = action.perform();
        ConfigMap cm1 = client.configMaps().inNamespace("test").withName("cm1").get();

        //then
        assertNotNull(rs);
        assertEquals(ExecutionExitCode.OK, rs.getExitCode());
        assertNotNull(cm1);
        assertNotNull(cm1.getMetadata());
        assertNotNull(cm1.getData());
        assertEquals(finalLength, cm1.getData().size());
        assertEquals(newData, cm1.getData());
    }
}
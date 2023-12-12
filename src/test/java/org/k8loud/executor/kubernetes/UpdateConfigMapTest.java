package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UpdateConfigMapTest extends KubernetesBaseTest {
    @ParameterizedTest
    @MethodSource
    void testUpdateValuesConfigMap(String namespace, String resourceName, Map<String, String> replacements,
                                   Map<String, String> data, Map<String, String> newData, int finalLength)
            throws KubernetesException, ValidationException {
        // given
        ConfigMap cm = new ConfigMapBuilder().withNewMetadata()
                .withName(resourceName)
                .withNamespace(namespace)
                .withResourceVersion("1")
                .endMetadata()
                .withData(data)
                .build();

        client.resource(cm).create();

        // when
        Map<String, Object> res = kubernetesService.updateConfigMap(namespace, resourceName, replacements);
        ConfigMap cm1 = client.configMaps().inNamespace(namespace).withName(resourceName).get();

        //then
        assertNotNull(cm1);
        assertNotNull(cm1.getMetadata());
        assertNotNull(cm1.getData());
        assertEquals(finalLength, cm1.getData().size());
        assertEquals(newData, cm1.getData());
        assertResult(String.format("Update of %s/%s successful", "ConfigMap", resourceName), res);
    }

    private static Stream<Arguments> testUpdateValuesConfigMap() {
        return Stream.of(
                Arguments.of(NAMESPACE, RESOURCE_NAME, Map.of("k1", "v2"), Map.of("k1", "v1"),
                        Map.of("k1", "v2"), 1),
                Arguments.of(NAMESPACE, RESOURCE_NAME, Map.of("k1", "v2"),
                        Map.of("k1", "v1", "k2", "v2", "k3", "v3"),
                        Map.of("k1", "v2", "k2", "v2", "k3", "v3"), 3),
                Arguments.of(NAMESPACE, RESOURCE_NAME, Map.of("k9", "v4"),
                        Map.of("k2", "v2", "k3", "v3"),
                        Map.of("k2", "v2", "k3", "v3", "k9", "v4"), 3));

    }
}

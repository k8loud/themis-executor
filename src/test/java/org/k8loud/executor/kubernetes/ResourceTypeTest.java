package org.k8loud.executor.kubernetes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.KubernetesException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.k8loud.executor.exception.code.KubernetesExceptionCode.INVALID_RESOURCE_TYPE;
import static org.k8loud.executor.kubernetes.KubernetesResourceType.*;

@ExtendWith(MockitoExtension.class)
class ResourceTypeTest {
    @ParameterizedTest
    @MethodSource
    void testValidResourceTypeParsing(String s, KubernetesResourceType expected) throws KubernetesException {
        // when
        KubernetesResourceType resourceType = KubernetesResourceType.fromString(s);

        // then
        assertEquals(expected, resourceType);
    }

    @Test
    void testInvalidResourceTypeParsing() {
        // given
        String s = "MyResourceType";

        // when
        Throwable e = catchThrowable(() -> KubernetesResourceType.fromString(s));

        // then
        assertThat(e).isExactlyInstanceOf(KubernetesException.class)
                .hasMessage("Invalid resource type 'MyResourceType', valid values: " +
                "[ReplicaSet, Deployment, StatefulSet, ControllerRevision, ConfigMap, Pod]");
        assertEquals(INVALID_RESOURCE_TYPE, ((CustomException) e).getExceptionCode());
    }

    // TODO: KubernetesResourceType::toString
    private static Stream<Arguments> testValidResourceTypeParsing() {
        return Stream.of(Arguments.of("ReplicaSet", REPLICA_SET),
                Arguments.of("Deployment", DEPLOYMENT),
                Arguments.of("StatefulSet", STATEFUL_SET),
                Arguments.of("ControllerRevision", CONTROLLER_REVISION),
                Arguments.of("ConfigMap", CONFIG_MAP),
                Arguments.of("Pod", POD)
        );
    }
}

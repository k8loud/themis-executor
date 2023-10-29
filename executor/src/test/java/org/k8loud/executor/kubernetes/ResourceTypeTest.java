package org.k8loud.executor.kubernetes;

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
import static org.k8loud.executor.kubernetes.ResourceType.*;

@ExtendWith(MockitoExtension.class)
class ResourceTypeTest {
    @ParameterizedTest
    @MethodSource
    void testValidResourceTypeParsing(String s, ResourceType expected) throws KubernetesException {
        // when
        ResourceType resourceType = ResourceType.fromString(s);

        // then
        assertEquals(expected, resourceType);
    }

    @ParameterizedTest
    @MethodSource
    void testInvalidResourceTypeParsing(String s) {
        // when
        Throwable e = catchThrowable(() -> ResourceType.fromString(s));

        // then
        assertThat(e).isExactlyInstanceOf(KubernetesException.class);
        assertEquals(INVALID_RESOURCE_TYPE, ((CustomException) e).getExceptionCode());
    }

    private static Stream<Arguments> testValidResourceTypeParsing() {
        return Stream.of(Arguments.of("ReplicaSet", REPLICA_SET),
                Arguments.of("Deployment", DEPLOYMENT),
                Arguments.of("StatefulSet", STATEFUL_SET),
                Arguments.of("ControllerRevision", CONTROLLER_REVISION),
                Arguments.of("ConfigMap", CONFIG_MAP),
                Arguments.of("Pod", POD)
        );
    }

    private static Stream<String> testInvalidResourceTypeParsing() {
        return Stream.of("VM",
                "MyResourceType");
    }
}

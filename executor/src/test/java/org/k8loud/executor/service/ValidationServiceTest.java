package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.exception.code.ValidationExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static data.ExecutionRQ.createExecutionRQ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@SpringBootTest
public class ValidationServiceTest {
    private static final Map<String, String> VALID_PARAMS = Map.of("param1", "val1");
    @Autowired
    private ValidationService validationService;

    @ParameterizedTest
    @MethodSource("provideValidExecutionRQs")
    void testValidMapping(String collectionName, String actionName, Map<String, String> params) throws ValidationException {
        // given
        ExecutionRQ executionRQ = createExecutionRQ(collectionName, actionName, params);

        // when
        validationService.validate(executionRQ);

        // then
        // No exception should be thrown
    }

    @ParameterizedTest
    @MethodSource("provideInvalidExecutionRQs")
    void testInvalidMapping(String collectionName, String actionName, Map<String, String> params,
                            ValidationExceptionCode expectedExceptionCode) {
        // given
        ExecutionRQ executionRQ = createExecutionRQ(collectionName, actionName, params);

        // when
        Throwable e = catchThrowable(() -> validationService.validate(executionRQ));

        // then
        assertThat(e).isExactlyInstanceOf(ValidationException.class);
        assertThat(((ValidationException) e).getExceptionCode()).isEqualTo(expectedExceptionCode);
    }

    private static Stream<Arguments> provideValidExecutionRQs() {
        return Stream.of(
                Arguments.of("kubernetes", "DeletePodAction", VALID_PARAMS),
                Arguments.of("kubernetes", "DeletePodAction", Collections.emptyMap())
        );
    }

    private static Stream<Arguments> provideInvalidExecutionRQs() {
        return Stream.of(
                Arguments.of(null, "DeletePodAction", VALID_PARAMS, ValidationExceptionCode.MISSING_COLLECTION_NAME),
                Arguments.of("kubernetes", null, VALID_PARAMS, ValidationExceptionCode.MISSING_ACTION_NAME),
                Arguments.of("kubernetes", "DeletePodAction", null, ValidationExceptionCode.MISSING_PARAMS)
        );
    }
}

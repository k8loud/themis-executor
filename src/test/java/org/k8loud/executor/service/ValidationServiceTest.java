package org.k8loud.executor.service;

import org.k8loud.executor.model.ExecutionRQ;
import org.k8loud.executor.model.Params;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.exception.code.ValidationExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.k8loud.executor.model.ExecutionRQ.createExecutionRQ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@SpringBootTest
@AutoConfigureObservability
public class ValidationServiceTest {
    private static final Params VALID_PARAMS = new Params(Map.of("param1", "val1"));
    private static final Params EMPTY_PARAMS = new Params(Collections.emptyMap());
    @Autowired
    private ValidationService validationService;

    @ParameterizedTest
    @MethodSource("provideValidExecutionRQs")
    void testValidMapping(String collectionName, String actionName, Params params) throws ValidationException {
        // given
        ExecutionRQ executionRQ = createExecutionRQ(collectionName, actionName, params);

        // when
        validationService.validate(executionRQ);

        // then
        // No exception should be thrown
    }

    @ParameterizedTest
    @MethodSource("provideInvalidExecutionRQs")
    void testInvalidMapping(String collectionName, String actionName, Params params,
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
        return Stream.of(Arguments.of("kubernetes", "DeleteResourceAction", VALID_PARAMS),
                Arguments.of("kubernetes", "DeleteResourceAction", EMPTY_PARAMS));
    }

    private static Stream<Arguments> provideInvalidExecutionRQs() {
        return Stream.of(
                Arguments.of(null, "DeleteResourceAction", VALID_PARAMS, ValidationExceptionCode.MISSING_COLLECTION_NAME),
                Arguments.of("kubernetes", null, VALID_PARAMS, ValidationExceptionCode.MISSING_ACTION_NAME),
                Arguments.of("kubernetes", "DeleteResourceAction", null, ValidationExceptionCode.MISSING_PARAMS));
    }
}

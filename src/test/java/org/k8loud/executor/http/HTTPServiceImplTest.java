package org.k8loud.executor.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.k8loud.executor.cnapp.http.HTTPServiceImpl;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class HTTPServiceImplTest {
    HTTPServiceImpl httpService;

    @BeforeEach
    void setUp() {
        httpService = new HTTPServiceImpl();
    }

    @ParameterizedTest
    @MethodSource
    void testSuccessfulStatusCode(int statusCode) {
        assertTrue(httpService.isStatusCodeSuccessful(statusCode));
    }

    @ParameterizedTest
    @MethodSource
    void testUnsuccessfulStatusCode(int statusCode) {
        assertFalse(httpService.isStatusCodeSuccessful(statusCode));
    }

    private static Stream<Integer> testSuccessfulStatusCode() {
        return Stream.of(200, 201, 202);
    }


    private static Stream<Integer> testUnsuccessfulStatusCode() {
        return Stream.of(500, 404, 401, 403);
    }
}

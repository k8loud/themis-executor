package org.k8loud.executor.util;

import com.google.gson.Gson;
import io.micrometer.core.instrument.util.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.cnapp.sockshop.params.RegisterUserParams;
import org.k8loud.executor.exception.HTTPException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HTTPSessionTest {
    private static final String URL_BASE = "http://localhost:8082";
    private static final String URL_SUPPLEMENT = "resource";
    private static final HttpResponse RESPONSE = new BasicHttpResponse(new BasicStatusLine(
            new ProtocolVersion("HTTP", 1, 1), 200, "OK"));

    @Captor
    ArgumentCaptor<HttpRequestBase> requestCaptor;

    @Spy
    HTTPSession httpSession;

    @BeforeEach
    void setUp() throws HTTPException {
        doReturn(RESPONSE).when(httpSession).sendRequest(any(HttpRequestBase.class));
    }

    @Test
    void testDoGet() throws HTTPException {
        // given
        final Map<String, String> headers = Map.of(
                "Authorization", "Basic 57a98d98e4b00679b4a830b0",
                "content-type", "application/json");

        // when
        httpSession.doGet(URL_BASE, URL_SUPPLEMENT, headers);

        // then
        verify(httpSession).sendRequest(requestCaptor.capture());
        final HttpGet request = (HttpGet) requestCaptor.getValue();

        assertEquals(URL_BASE + "/" + URL_SUPPLEMENT, request.getURI().toString());
        for (var header : headers.entrySet()) {
            assertHeader(request, header.getKey(), header.getValue());
        }
    }

    @Test
    void testDoGetWithoutHeaders() throws HTTPException {
        // when
        httpSession.doGet(URL_BASE, URL_SUPPLEMENT);

        // then
        verify(httpSession).sendRequest(requestCaptor.capture());
        final HttpGet request = (HttpGet) requestCaptor.getValue();

        assertEquals(URL_BASE + "/" + URL_SUPPLEMENT, request.getURI().toString());
        assertEquals(0, request.getAllHeaders().length);
    }

    @Test
    void testDoPost() throws HTTPException, IOException {
        // given
        final RegisterUserParams paramsObj = RegisterUserParams.builder().username("userVal").password("passVal")
                .email("em@il.com").build();

        // when
        httpSession.doPost(URL_BASE, URL_SUPPLEMENT, paramsObj);

        // then
        verify(httpSession).sendRequest(requestCaptor.capture());
        final HttpPost request = (HttpPost) requestCaptor.getValue();

        assertEquals(URL_BASE + "/" + URL_SUPPLEMENT, request.getURI().toString());
        assertHeader(request, "content-type", "application/json");
        assertEquals(new Gson().toJson(paramsObj),
                IOUtils.toString(request.getEntity().getContent(), StandardCharsets.UTF_8));
    }

    @Test
    void testDoDelete() throws HTTPException {
        // when
        httpSession.doDelete(URL_BASE, URL_SUPPLEMENT);

        // then
        verify(httpSession).sendRequest(requestCaptor.capture());
        final HttpDelete request = (HttpDelete) requestCaptor.getValue();

        assertEquals(URL_BASE + "/" + URL_SUPPLEMENT, request.getURI().toString());
    }

    private void assertHeader(HttpRequestBase request, String headerName, String expectedValue) {
        assertTrue(request.containsHeader(headerName));
        assertEquals(expectedValue, request.getFirstHeader(headerName).getValue());
    }
}

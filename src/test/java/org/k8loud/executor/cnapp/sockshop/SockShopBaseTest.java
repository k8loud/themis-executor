package org.k8loud.executor.cnapp.sockshop;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.http.HTTPService;
import org.k8loud.executor.http.HTTPSession;
import org.k8loud.executor.mail.MailService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SockShopBaseTest {
    protected static final String APPLICATION_URL = "http://localhost:8082";
    protected static final String SOCKSHOP_LOGIN_URL_SUPPLEMENT = "login";
    protected static final String REGISTER_USER_URL_SUPPLEMENT = "register";
    protected static final String SOCKSHOP_CUSTOMERS_URL_SUPPLEMENT = "customers";
    protected static final String SOCKSHOP_ADDRESSES_URL_SUPPLEMENT = "addresses";

    protected static final String USERNAME = "user994";
    protected static final String PASSWORD = "pass994";
    protected static final String ID = "655b6dfacb8de600019db115";
    protected static final int UNSUCCESSFUL_RESPONSE_STATUS_CODE = 500;
    protected static final String RESPONSE_CONTENT = "responseContentVal";

    @Mock
    SockShopProperties sockShopPropertiesMock;
    @Mock
    HTTPService httpServiceMock;
    @Mock
    HTTPSession httpSessionMock;
    @Mock
    HttpResponse successfulResponseMock;
    @Mock
    HttpResponse unsuccessfulResponseMock;
    @Mock
    MailService mailServiceMock;

    SockShopServiceImpl sockShopService;

    @BeforeEach
    public void setUp() throws IOException, HTTPException {
        sockShopService = new SockShopServiceImpl(sockShopPropertiesMock, httpServiceMock, mailServiceMock);
        doAnswer(i -> {
            int statusCode = i.getArgument(0);
            return 200 <= statusCode && statusCode < 300;
        }).when(httpServiceMock).isStatusCodeSuccessful(anyInt());
        when(httpServiceMock.createSession()).thenReturn(httpSessionMock);
        additionalSetUp();
    }

    protected void additionalSetUp() throws HTTPException, IOException {
        // empty
    }

    protected void assertResponseContent(Map<String, String> resultMap) {
        assertEquals(RESPONSE_CONTENT, resultMap.get("responseContent"));
    }

    protected void mockAuth() throws HTTPException, IOException {
        when(sockShopPropertiesMock.getLoginUserUrlSupplement()).thenReturn(SOCKSHOP_LOGIN_URL_SUPPLEMENT);
        when(httpSessionMock.doGet(eq(APPLICATION_URL), eq(SOCKSHOP_LOGIN_URL_SUPPLEMENT), any(Map.class)))
                .thenReturn(successfulResponseMock);
        mockSuccessfulResponse();
    }

    protected void mockSuccessfulResponse() throws IOException {
        mockResponse(successfulResponseMock, 200);
        doAnswer(i -> {
            HttpResponse response = i.getArgument(0);
            return EntityUtils.toString(response.getEntity());
        }).when(httpServiceMock).getResponseEntityAsString(any(HttpResponse.class));
    }

    protected void mockUnsuccessfulResponse() {
        mockResponse(unsuccessfulResponseMock, UNSUCCESSFUL_RESPONSE_STATUS_CODE);
    }

    private void mockResponse(HttpResponse response, int statusCode) {
        StatusLine statusLineMock = mock(StatusLine.class);
        when(statusLineMock.getStatusCode()).thenReturn(statusCode);
        when(response.getStatusLine()).thenReturn(statusLineMock);
        mockResponseContent(response);
    }

    // Recreating the input stream each time getEntity() is called is required; the input stream is consumed in our usage
    private void mockResponseContent(HttpResponse response) {
        lenient().doAnswer(i -> {
            BasicHttpEntity httpEntity = new BasicHttpEntity();
            InputStream is = new ByteArrayInputStream(RESPONSE_CONTENT.getBytes());
            httpEntity.setContent(is);
            return httpEntity;
        }).when(response).getEntity();
    }
}

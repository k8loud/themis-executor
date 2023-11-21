package org.k8loud.executor.cnapp.sockshop;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.service.HTTPService;
import org.k8loud.executor.util.HTTPSession;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SockShopBaseTest {
    protected static final String APPLICATION_URL = "http://localhost:8082";
    protected static final HttpResponse SUCCESSFUL_RESPONSE = new BasicHttpResponse(new BasicStatusLine(
            new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
    protected static final int UNSUCCESSFUL_RESPONSE_STATUS_CODE = 500;
    protected static final HttpResponse UNSUCCESSFUL_RESPONSE = new BasicHttpResponse(new BasicStatusLine(
            new ProtocolVersion("HTTP", 1, 1), 500, "Internal Server Error"));
    protected static final String RESPONSE_CONTENT = "responseContentVal";

    @Mock
    SockShopProperties sockShopProperties;
    @Mock
    HTTPService httpService;
    @Mock
    HTTPSession httpSession;

    SockShopServiceImpl sockShopService;

    @BeforeEach
    public void setUp() throws IOException {
        sockShopService = new SockShopServiceImpl(sockShopProperties, httpService);
        doAnswer(i -> {
            int statusCode = i.getArgument(0);
            return 200 <= statusCode && statusCode < 300;
        }).when(httpService).isStatusCodeSuccessful(anyInt());
        when(httpService.createSession()).thenReturn(httpSession);

        final BasicHttpEntity httpEntity = new BasicHttpEntity();
        try (InputStream is = new ByteArrayInputStream(RESPONSE_CONTENT.getBytes())) {
            httpEntity.setContent(is);
        }
        SUCCESSFUL_RESPONSE.setEntity(httpEntity);
        UNSUCCESSFUL_RESPONSE.setEntity(httpEntity);
    }

    protected void assertResponseContent(Map<String, String> resultMap) {
        assertEquals(RESPONSE_CONTENT, resultMap.get("responseContent"));
    }
}

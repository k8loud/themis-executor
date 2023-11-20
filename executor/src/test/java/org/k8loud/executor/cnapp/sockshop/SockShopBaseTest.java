package org.k8loud.executor.cnapp.sockshop;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.service.HTTPService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
public class SockShopBaseTest {
    protected static final String APPLICATION_URL = "http://localhost:8082";
    protected static final HttpResponse SUCCESSFUL_RESPONSE = new BasicHttpResponse(new BasicStatusLine(
            new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
    protected static final int UNSUCCESSFUL_RESPONSE_STATUS_CODE = 500;
    protected static final HttpResponse UNSUCCESSFUL_RESPONSE = new BasicHttpResponse(new BasicStatusLine(
            new ProtocolVersion("HTTP", 1, 1), 500, "Internal Server Error"));
    @Mock
    SockShopProperties sockShopProperties;
    @Mock
    HTTPService httpService;

    SockShopServiceImpl sockShopService;

    @BeforeEach
    public void setUp() {
        sockShopService = new SockShopServiceImpl(sockShopProperties, httpService);
        doAnswer(i -> {
            int statusCode = i.getArgument(0);
            return 200 <= statusCode && statusCode < 300;
        }).when(httpService).isResponseStatusCodeSuccessful(anyInt());
    }
}

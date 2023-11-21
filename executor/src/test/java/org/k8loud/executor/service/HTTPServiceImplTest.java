package org.k8loud.executor.service;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HTTPServiceImplTest {
    private static final String URL_BASE = "http://localhost:8082";
    private static final String URL_SUPPLEMENT = "resource";
    private static final String ENCODING = "UTF-8";
    private static final HttpResponse RESPONSE = new BasicHttpResponse(new BasicStatusLine(
            new ProtocolVersion("HTTP", 1, 1), 200, "OK"));

    @Captor
    ArgumentCaptor<HttpRequestBase> requestCaptor;

    @Spy
    HTTPServiceImpl httpService;

//    @Test
//    void testDoPost() throws HTTPException, IOException {
//        // given
//        final RegisterUserParams paramsObj = RegisterUserParams.builder().username("userVal").password("passVal")
//                .email("em@il.com").build();
//        doReturn(RESPONSE).when(httpService).sendRequest(any(HttpRequestBase.class));
//
//        // when
//        httpService.doPost(URL_BASE, URL_SUPPLEMENT, paramsObj);
//
//        // then
//        verify(httpService).sendRequest(requestCaptor.capture());
//        final HttpPost request = (HttpPost) requestCaptor.getValue();
//
//        assertEquals(URL_BASE + "/" + URL_SUPPLEMENT, request.getURI().toString());
//        assertHeader(request, "content-type", "application/json");
//        assertHeader(request, "charset", ENCODING);
//        assertEquals(new Gson().toJson(paramsObj),
//                IOUtils.toString(request.getEntity().getContent(), StandardCharsets.UTF_8));
//    }
//
//    @Test
//    void testDoDelete() throws HTTPException {
//        // given
//        doReturn(RESPONSE).when(httpService).sendRequest(any(HttpRequestBase.class));
//
//        // when
//        httpService.doDelete(URL_BASE, URL_SUPPLEMENT);
//
//        // then
//        verify(httpService).sendRequest(requestCaptor.capture());
//        final HttpDelete request = (HttpDelete) requestCaptor.getValue();
//
//        assertEquals(URL_BASE + "/" + URL_SUPPLEMENT, request.getURI().toString());
//        assertHeader(request, "charset", ENCODING);
//    }
//
//    private void assertHeader(HttpRequestBase request, String headerName, String expectedValue) {
//        assertTrue(request.containsHeader(headerName));
//        assertEquals(expectedValue, request.getFirstHeader(headerName).getValue());
//    }
}

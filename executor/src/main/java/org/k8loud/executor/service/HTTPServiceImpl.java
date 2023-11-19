package org.k8loud.executor.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.k8loud.executor.exception.HTTPException;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_REQUEST_FAILED_TO_COMPLETE;

@Slf4j
@Service
public class HTTPServiceImpl implements HTTPService {
    private static final Gson GSON = new Gson();

    public HttpResponse doPost(String urlBase, String urlSupplement, Object paramsObj) throws HTTPException {
        final String url = getUrl(urlBase, urlSupplement);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        try {
            StringEntity params = new StringEntity(GSON.toJson(paramsObj));
            request.addHeader("content-type", "application/json");
            request.addHeader("charset", "utf-8");
            request.setEntity(params);
            log.info("Sending {}", request);
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new HTTPException(e, HTTP_REQUEST_FAILED_TO_COMPLETE);
        }
    }

    @Override
    public boolean isResponseSuccessful(HttpResponse response) {
        final int statusCode = response.getStatusLine().getStatusCode();
        return 200 <= statusCode && statusCode < 300;
    }

    private String getUrl(String urlBase, String urlSupplement) {
        return String.format("%s/%s", urlBase, urlSupplement);
    }
}

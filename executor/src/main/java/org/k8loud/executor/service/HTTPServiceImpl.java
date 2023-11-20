package org.k8loud.executor.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
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
    private static final String ENCODING = "UTF-8";
    private static final Gson GSON = new Gson();

    public HttpResponse doPost(String urlBase, String urlSupplement, Object paramsObj) throws HTTPException {
        final String url = getUrl(urlBase, urlSupplement);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        try {
            StringEntity params = new StringEntity(GSON.toJson(paramsObj));
            request.addHeader("content-type", "application/json");
            request.addHeader("charset", ENCODING);
            request.setEntity(params);
            log.info("Sending {}", request);
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new HTTPException(e, HTTP_REQUEST_FAILED_TO_COMPLETE);
        }
    }

    @Override
    public HttpResponse doDelete(String urlBase, String urlSupplement) throws HTTPException {
        final String url = getUrl(urlBase, urlSupplement);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpDelete request = new HttpDelete(url);
        try {
            request.addHeader("charset", ENCODING);
            log.info("Sending {}", request);
            return httpClient.execute(request);
        } catch (IOException e) {
            throw new HTTPException(e, HTTP_REQUEST_FAILED_TO_COMPLETE);
        }
    }

    @Override
    public boolean isResponseSuccessful(HttpResponse response) {
        return isResponseStatusCodeSuccessful(response.getStatusLine().getStatusCode());
    }

    @Override
    public boolean isResponseStatusCodeSuccessful(int statusCode) {
        return 200 <= statusCode && statusCode < 300;
    }

//    private String convertObjectToUrlParams(Object paramsObj) throws HTTPException {
//        Class<?> clazz = paramsObj.getClass();
//        StringBuilder params = new StringBuilder();
//        for (Field field : clazz.getDeclaredFields()) {
//            if (!Modifier.isStatic(field.getModifiers())) {
//                final boolean isFieldAccessible = field.isAccessible();
//                field.setAccessible(true);
//                try {
//                    Object value = field.get(paramsObj);
//                    if (value != null) {
//                        if (params.length() > 0) {
//                            params.append("&");
//                        }
//                        params.append(URLEncoder.encode(field.getName(), ENCODING))
//                                .append("=")
//                                .append(URLEncoder.encode(value.toString(), ENCODING));
//                    }
//                } catch (IllegalAccessException | UnsupportedEncodingException e) {
//                    throw new HTTPException(e, CONVERT_PARAMS_OBJECT_TO_URL_PARAMS_FAILURE);
//                }
//                field.setAccessible(isFieldAccessible);
//            }
//        }
//        return params.toString();
//    }

    private String getUrl(String urlBase, String urlSupplement) {
        return String.format("%s/%s", urlBase, urlSupplement);
    }
}

package org.k8loud.executor.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.k8loud.executor.exception.HTTPException;

public interface HTTPService {
    HttpResponse doPost(String urlBase, String urlSupplement, Object paramsObj) throws HTTPException;

    HttpResponse doDelete(String urlBase, String urlSupplement) throws HTTPException;

    HttpResponse sendRequest(HttpRequestBase request) throws HTTPException;

    boolean isResponseSuccessful(HttpResponse response);

    boolean isResponseStatusCodeSuccessful(int statusCode);
}

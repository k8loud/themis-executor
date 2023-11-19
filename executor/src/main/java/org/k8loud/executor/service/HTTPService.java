package org.k8loud.executor.service;

import org.apache.http.HttpResponse;
import org.k8loud.executor.exception.HTTPException;

public interface HTTPService {
    HttpResponse doPost(String urlBase, String urlSupplement, Object paramsObj) throws HTTPException;

    boolean isResponseSuccessful(HttpResponse response);
}

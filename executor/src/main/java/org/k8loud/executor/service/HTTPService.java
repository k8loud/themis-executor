package org.k8loud.executor.service;

import org.apache.http.HttpResponse;
import org.k8loud.executor.util.HTTPSession;

public interface HTTPService {
    HTTPSession createSession();

    boolean isResponseSuccessful(HttpResponse response);

    boolean isStatusCodeSuccessful(int statusCode);
}

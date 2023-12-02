package org.k8loud.executor.http;

import org.apache.http.HttpResponse;

import java.io.IOException;

public interface HTTPService {
    HTTPSession createSession();

    boolean isStatusCodeSuccessful(int statusCode);

    String getResponseEntityAsString(HttpResponse response) throws IOException;
}

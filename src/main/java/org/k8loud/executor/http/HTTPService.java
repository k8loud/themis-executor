package org.k8loud.executor.http;

public interface HTTPService {
    HTTPSession createSession();

    boolean isStatusCodeSuccessful(int statusCode);
}

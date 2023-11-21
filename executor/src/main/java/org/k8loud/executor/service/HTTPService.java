package org.k8loud.executor.service;

import org.k8loud.executor.util.HTTPSession;

public interface HTTPService {
    HTTPSession createSession();

    boolean isStatusCodeSuccessful(int statusCode);
}

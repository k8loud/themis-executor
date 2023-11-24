package org.k8loud.executor.service;

import org.k8loud.executor.util.HTTPSession;
import org.springframework.stereotype.Service;

@Service
public class HTTPServiceImpl implements HTTPService {
    @Override
    public HTTPSession createSession() {
        return new HTTPSession();
    }

    @Override
    public boolean isStatusCodeSuccessful(int statusCode) {
        return 200 <= statusCode && statusCode < 300;
    }
}

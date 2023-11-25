package org.k8loud.executor.http;

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

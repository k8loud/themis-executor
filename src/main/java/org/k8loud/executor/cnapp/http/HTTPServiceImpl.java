package org.k8loud.executor.cnapp.http;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    @Override
    public String getResponseEntityAsString(HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }
}

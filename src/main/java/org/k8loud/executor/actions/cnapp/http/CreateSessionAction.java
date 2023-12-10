package org.k8loud.executor.actions.cnapp.http;

import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.http.HTTPService;
import org.k8loud.executor.http.HTTPSession;
import org.k8loud.executor.model.Params;

import java.util.Map;

import static org.k8loud.executor.util.Util.resultMap;

public class CreateSessionAction extends HTTPAction {
    public CreateSessionAction(Params params, HTTPService httpService) throws ActionException {
        super(params, httpService);
    }

    @Builder
    public CreateSessionAction(HTTPService httpService) throws ActionException {
        super(httpService);
    }

    @Override
    protected Map<String, Object> executeBody() throws CustomException {
        HTTPSession session = httpService.createSession();
        return resultMap("Created a session", Map.of("session", session));
    }
}

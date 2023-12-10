package org.k8loud.executor.actions.cnapp.http;

import lombok.AllArgsConstructor;
import org.k8loud.executor.actions.cnapp.CNAppAction;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.http.HTTPService;
import org.k8loud.executor.model.Params;

@AllArgsConstructor
public abstract class HTTPAction extends CNAppAction {
    protected HTTPService httpService;

    public HTTPAction(Params params, HTTPService httpService) throws ActionException {
        super(params);
        this.httpService = httpService;
    }

    @Override
    public void unpackParams(Params params) {
        // empty
    }
}

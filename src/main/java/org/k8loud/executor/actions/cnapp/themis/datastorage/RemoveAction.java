package org.k8loud.executor.actions.cnapp.themis.datastorage;

import lombok.Builder;
import org.k8loud.executor.datastorage.DataStorageService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.model.Params;

import java.util.Map;

import static org.k8loud.executor.util.Util.resultMap;

public class RemoveAction extends DataStorageAction {
    private String path;

    public RemoveAction(Params params, DataStorageService dataStorageService) throws ActionException {
        super(params, dataStorageService);
    }

    @Builder
    public RemoveAction(Params params, DataStorageService dataStorageService,
                        String path, String content) throws ActionException {
        super(params, dataStorageService);
        this.path = path;
    }

    @Override
    public void unpackParams(Params params) throws ActionException {
        this.path = params.getRequiredParam("path");
    }

    @Override
    protected Map<String, String> executeBody() {
        return resultMap(String.valueOf(dataStorageService.remove(path)));
    }
}

package org.k8loud.executor.actions.cnapp.themis.datastorage;

import lombok.Builder;
import org.k8loud.executor.datastorage.DataStorageService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.DataStorageException;
import org.k8loud.executor.model.Params;

import java.util.Map;

import static org.k8loud.executor.util.Util.resultMap;

public class StoreImageAction extends DataStorageAction {
    private String fileName;
    private String sourceUrl;

    public StoreImageAction(Params params, DataStorageService dataStorageService) throws ActionException {
        super(params, dataStorageService);
    }

    @Builder
    public StoreImageAction(Params params, DataStorageService dataStorageService,
                            String fileName, String sourceUrl) throws ActionException {
        super(params, dataStorageService);
        this.fileName = fileName;
        this.sourceUrl = sourceUrl;
    }

    @Override
    public void unpackParams(Params params) throws ActionException {
        this.fileName = params.getRequiredParam("fileName");
        this.sourceUrl = params.getRequiredParam("sourceUrl");
    }

    @Override
    protected Map<String, Object> executeBody() throws DataStorageException {
        return resultMap(dataStorageService.storeImage(fileName, sourceUrl));
    }
}

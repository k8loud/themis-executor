package org.k8loud.executor.actions.cnapp.themis.datastorage;

import org.k8loud.executor.actions.cnapp.CNAppAction;
import org.k8loud.executor.datastorage.DataStorageService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.model.Params;

public abstract class DataStorageAction extends CNAppAction {
    protected DataStorageService dataStorageService;

    public DataStorageAction(Params params, DataStorageService dataStorageService) throws ActionException {
        super(params);
        this.dataStorageService = dataStorageService;
    }
}

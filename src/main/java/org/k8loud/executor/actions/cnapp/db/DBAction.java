package org.k8loud.executor.actions.cnapp.db;

import org.k8loud.executor.actions.cnapp.CNAppAction;
import org.k8loud.executor.db.DBService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.model.Params;

public abstract class DBAction<T> extends CNAppAction {

    protected String connString;
    protected String query;

    protected final DBService<T> dbService;

    protected DBAction(Params params, DBService<T> dbService) throws ActionException {
        super(params);
        this.dbService = dbService;
    }

    protected DBAction(DBService<T> dbService, String query, String connString) {
        this.dbService = dbService;
        this.query = query;
        this.connString = connString;
    }

    @Override
    public void unpackParams(Params params) throws ActionException {
        query = params.getRequiredParam("query");
        connString = params.getRequiredParam("connString");
    }
}

package org.k8loud.executor.actions.cnapp.db.mysql;

import org.k8loud.executor.actions.cnapp.db.DBAction;
import org.k8loud.executor.cnapp.db.DBService;
import org.k8loud.executor.cnapp.db.SuperConnection;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.DBException;
import org.k8loud.executor.exception.code.DBExceptionCode;
import org.k8loud.executor.model.Params;

import java.sql.Connection;
import java.util.Map;

public class MySqlQueryAction extends DBAction<Connection> {

    public MySqlQueryAction(Params params, DBService<Connection> dbService) throws ActionException {
        super(params, dbService);
    }

    public MySqlQueryAction(DBService<Connection> dbService, String query, String connString) {
        super(dbService, query, connString);
    }

    @Override
    protected Map<String, Object> executeBody() throws CustomException {
        try (SuperConnection<Connection> connection = dbService.getConnection(connString)){
            return dbService.runQuery(query, connection);
        } catch (Exception e) {
            throw new DBException(e, DBExceptionCode.QUERY_FAILED);
        }
    }
}

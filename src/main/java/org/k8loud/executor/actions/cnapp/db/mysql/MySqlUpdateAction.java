package org.k8loud.executor.actions.cnapp.db.mysql;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import org.k8loud.executor.actions.cnapp.db.DBAction;
import org.k8loud.executor.db.DBService;
import org.k8loud.executor.db.SuperConnection;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.DBException;
import org.k8loud.executor.exception.code.DBExceptionCode;
import org.k8loud.executor.model.Params;

import java.sql.Connection;
import java.util.Map;

@EqualsAndHashCode
public class MySqlUpdateAction extends DBAction<Connection> {

    public MySqlUpdateAction(Params params, DBService<Connection> dbService) throws ActionException {
        super(params, dbService);
    }

    @Builder
    public MySqlUpdateAction(DBService<Connection> dbService, String query, String connString) {
        super(dbService, query, connString);
    }



    @Override
    protected Map<String, String> executeBody() throws CustomException {
        try (SuperConnection<Connection> connection = dbService.getConnection(connString)) {
            return dbService.runUpdate(query, connection);
        } catch (Exception e) {
            throw new DBException(e, DBExceptionCode.CONNECTION_FAILED);
        }
    }
}

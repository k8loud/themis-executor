package org.k8loud.executor.actions.cnapp.db.mongo;


import com.mongodb.client.MongoDatabase;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.k8loud.executor.actions.cnapp.db.DBAction;
import org.k8loud.executor.cnapp.db.DBService;
import org.k8loud.executor.cnapp.db.SuperConnection;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.DBException;
import org.k8loud.executor.exception.code.DBExceptionCode;
import org.k8loud.executor.model.Params;

import java.util.Map;

@EqualsAndHashCode
public class MongoQueryAction extends DBAction<MongoDatabase> {

    public MongoQueryAction(Params params, DBService<MongoDatabase> dbService) throws ActionException {
        super(params, dbService);
    }

    @Builder
    public MongoQueryAction(DBService<MongoDatabase> dbService, String query, String connString){
        super(dbService, query, connString);
    }


    @Override
    protected Map<String, Object> executeBody() throws CustomException {
        try(SuperConnection<MongoDatabase> connection = dbService.getConnection(connString)) {
            return dbService.runQuery(query, connection);
        } catch (Exception e) {
            throw new DBException(e, DBExceptionCode.QUERY_FAILED);
        }
    }
}

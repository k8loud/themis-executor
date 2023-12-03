package org.k8loud.executor.db;

import com.github.vincentrussell.query.mongodb.sql.converter.MongoDBQueryHolder;
import com.github.vincentrussell.query.mongodb.sql.converter.ParseException;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import com.mongodb.client.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.k8loud.executor.exception.DBException;
import org.k8loud.executor.exception.code.DBExceptionCode;
import org.springframework.stereotype.Service;

import static org.k8loud.executor.util.Util.resultMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MongoService implements DBService<MongoClient> {

    private String database;
    @Override
    public SuperConnection<MongoClient> getConnection(String connString) throws DBException {
        String[] split = connString.split(";");
        String uri = split[0];
        database = split[1];
        MongoClient client = MongoClients.create(uri);
        return new SuperConnection<>(client);
    }

    @Override
    public Map<String, String> runUpdate(String query, SuperConnection<MongoClient> conn) throws DBException {
        return null;
    }

    @Override
    public Map<String, String> runQuery(String query, SuperConnection<MongoClient> connection) throws DBException {
        try {
            QueryConverter queryConverter = new QueryConverter
                    .Builder()
                    .sqlString(query)
                    .build();

            MongoDBQueryHolder holder = queryConverter.getMongoQuery();
            String collection = holder.getCollection();
            Document mongoQuery = holder.getQuery();
            List<Document> res = new LinkedList<>();
            connection.getConnection()
                    .getDatabase(database)
                    .getCollection(collection)
                    .find(mongoQuery)
                    .forEach(res::add);

            return resultMap(String.format("Result: %s", res));
        } catch (ParseException e) {
            throw new DBException(e, DBExceptionCode.QUERY_NOT_VALID);
        }
    }
}

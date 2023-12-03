package org.k8loud.executor.db;

import org.k8loud.executor.exception.DBException;

import java.util.Map;

public interface DBService<T> {

    SuperConnection<T> getConnection(String connString) throws DBException;
    Map<String, String> runUpdate(String query, SuperConnection<T> connection) throws DBException;

    Map<String, String> runQuery(String query, SuperConnection<T> connection) throws DBException;
}

package org.k8loud.executor.cnapp.db;

import org.k8loud.executor.exception.DBException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;

public interface DBService<T> {

    SuperConnection<T> getConnection(String connString) throws DBException;
    Map<String, Object> runUpdate(String query, SuperConnection<T> connection) throws DBException, ValidationException;

    Map<String, Object> runQuery(String query, SuperConnection<T> connection) throws DBException, ValidationException;
}

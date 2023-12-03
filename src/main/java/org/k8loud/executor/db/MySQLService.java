package org.k8loud.executor.db;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.DBException;
import org.k8loud.executor.exception.code.DBExceptionCode;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Map;
import static org.k8loud.executor.util.Util.resultMap;

@Service
@Slf4j
public class MySQLService implements DBService<Connection> {

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "DBException",exceptionCode = "CONNECTION_FAILED")
    public SuperConnection<Connection> getConnection(String connString) throws DBException {
        try {
            return new SuperConnection<>(DriverManager.getConnection(connString));
        } catch (SQLException e) {
            throw new DBException(e, DBExceptionCode.CONNECTION_FAILED);
        }
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "DBException",exceptionCode = "QUERY_FAILED")
    public Map<String, String> runUpdate(String query, SuperConnection<Connection> conn) throws DBException {

        int rowsAffected;
        try(Statement statement = conn.getConnection().createStatement()) {
            rowsAffected = statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new DBException(e, DBExceptionCode.QUERY_FAILED);
        }

        return resultMap(String.format("Rows affected: %d", rowsAffected));
    }

    @Override
    public Map<String, String> runQuery(String query, SuperConnection<Connection> connection) throws DBException {

        ResultSet result;
        try(Statement statement = connection.getConnection().createStatement()) {
            result = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new DBException(e, DBExceptionCode.QUERY_FAILED);
        }
        
        return resultMap(String.format("Query result: %s", result.toString()));
    }
}

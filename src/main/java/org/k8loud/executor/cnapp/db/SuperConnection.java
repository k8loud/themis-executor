package org.k8loud.executor.cnapp.db;

import com.mongodb.client.MongoClient;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Connection;

@Data
@AllArgsConstructor
public class SuperConnection<T> implements AutoCloseable {
    private T connection;


    @Override
    public void close() throws Exception {
        if (connection instanceof Connection) {
            ((Connection) connection).close();
        }
        if (connection instanceof MongoClient) {
            ((MongoClient) connection).close();
        }
    }
}

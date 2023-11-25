package org.k8loud.executor.datastorage;

public interface DataStorageService {
    String store(String fileName, String content);
    boolean remove(String path);
}

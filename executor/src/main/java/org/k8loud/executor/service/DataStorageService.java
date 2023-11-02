package org.k8loud.executor.service;

public interface DataStorageService {
    String store(String fileName, String content);
    boolean remove(String path);
    boolean forceRemove(String path);
}

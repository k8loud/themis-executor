package org.k8loud.executor.datastorage;

public interface DataStorageService {
    String store(String fileName, String content);

    String storeImage(String fileName, String sourceUrl);

    boolean remove(String path);
}

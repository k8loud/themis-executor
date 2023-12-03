package org.k8loud.executor.datastorage;

import org.k8loud.executor.exception.DataStorageException;

public interface DataStorageService {
    String store(String fileName, String content);

    String storeImage(String fileName, String sourceUrl) throws DataStorageException;

    boolean remove(String path);
}

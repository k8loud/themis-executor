package org.k8loud.executor.common.testutil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataStorageTestUtil {
    public static String store(String path, String content) throws IOException {
        File file = new File(path);
        file.createNewFile();
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(content);
        }
        return path;
    }

    public static boolean remove(String path) throws IOException {
        Files.delete(Paths.get(path));
        return true;
    }

    public static void safeRemove(String path) {
        try {
            remove(path);
        } catch (IOException ignored) {
        }
    }
}

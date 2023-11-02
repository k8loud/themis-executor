package org.k8loud.executor.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataStorageServiceImpl implements DataStorageService {
    private static final DateTimeFormatter UNIQUE_PATH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final DataStorageProperties dataStorageProperties;

    @Nullable
    @Override
    public String storeAsFile(String fileName, String content) {
        log.info("Storing as file the passed fileName: '{}'", fileName);
        fileName = assureFileName(fileName);
        String uniqueFullPath = getUniqueFullPath(fileName);
        log.info("Actual path: '{}'", uniqueFullPath);
        File file = new File(uniqueFullPath);
        return safelyCreateFile(file) && writeToFile(file, content) ? uniqueFullPath : null;
    }

    private boolean fileExists(String path) {
        Path pathObj = Paths.get(path);
        boolean result = Files.isRegularFile(pathObj);
        if (result) {
            log.debug("File in path '{}' exists", path);
        } else {
            log.debug("File in path '{}' doesn't exist", path);
        }
        return result;
    }

    private String assureFileName(String fileName) {
        String assuredFileName = Paths.get(fileName).getFileName().toString();
        if (!fileName.equals(assuredFileName)) {
            log.warn("The passed fileName '{}' is a path, keeping only the file name part - '{}'",
                    fileName, assuredFileName);
        }
        return assuredFileName;
    }

    private String getUniqueFullPath(String fileName) {
        String uniqueFullPath;
        do {
            String uniquePath = getUniquePath(fileName);
            uniqueFullPath = Paths.get(dataStorageProperties.getRootPath(), uniquePath).normalize().toString();
        } while(fileExists(uniqueFullPath));
        return uniqueFullPath;
    }

    private String getUniquePath(String fileName) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String timestamp = currentDateTime.format(UNIQUE_PATH_DATE_FORMATTER);
        return String.format("%s_%s", fileName, timestamp);
    }

    private boolean safelyCreateFile(File file) {
        boolean result = false;
        try {
            result = file.createNewFile();
        } catch (IOException ignored) {
            // Fail by default
        }
        if (result) {
            log.debug("Created file '{}'", file.getPath());
        } else {
            log.error("Failed to create file '{}'", file.getPath());
        }
        return result;
    }

    private boolean writeToFile(File file, String content) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
            log.debug("Written content to file '{}'", file.getPath());
            return true;
        } catch (IOException e) {
            log.error("Failed to write to '{}', ex message: '{}'", file.getPath(), e.getMessage());
        }
        return false;
    }
}

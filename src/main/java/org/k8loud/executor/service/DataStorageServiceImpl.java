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
    private static final DateTimeFormatter UNIQUE_FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final DataStorageProperties dataStorageProperties;

    @Nullable
    @Override
    public String store(String fileName, String content) {
        log.debug("Storing in file, the passed fileName is '{}'", fileName);
        fileName = assureFileName(fileName);
        String uniqueFullPath = getUniqueFullPath(fileName);
        log.debug("Actual path '{}'", uniqueFullPath);
        File file = new File(uniqueFullPath);
        return safelyCreateFile(file) && writeToFile(file, content) ? uniqueFullPath : null;
    }

    @Override
    public boolean remove(String path) {
        return remove(path, dataStorageProperties.isRemovalPermitted());
    }

    private boolean remove(String path, boolean permit) {
        boolean result = false;
        if (permit) {
            log.debug("Removing '{}'", path);
            // Rebuild path to disallow path that resolves to a file outside rootPath
            String fileName = assureFileName(path);
            path = getFullPath(fileName);
            log.trace("Actual path '{}'", path);
            if (fileExists(path)) {
                try {
                    Files.delete(Paths.get(path));
                    result = true;
                    log.debug("Removed '{}'", path);
                } catch (IOException e) {
                    log.error("Failed to remove '{}'", path);
                }
            }
        } else {
            log.debug("Removal of '{}' is not permitted", path);
        }
        return result;
    }

    private boolean fileExists(String path) {
        Path pathObj = Paths.get(path);
        boolean result = Files.isRegularFile(pathObj);
        if (result) {
            log.trace("File in path '{}' exists", path);
        } else {
            log.trace("File in path '{}' doesn't exist", path);
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
            uniqueFullPath = getFullPath(getUniqueFileName(fileName));
        } while(fileExists(uniqueFullPath));
        return uniqueFullPath;
    }

    private String getFullPath(String fileName) {
        return Paths.get(dataStorageProperties.getRootPath(), fileName).normalize().toString();
    }

    private String getUniqueFileName(String fileName) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String timestamp = currentDateTime.format(UNIQUE_FILE_NAME_FORMATTER);
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
            log.trace("Created file '{}'", file.getPath());
        } else {
            log.error("Failed to create file '{}'", file.getPath());
        }
        return result;
    }

    private boolean writeToFile(File file, String content) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
            log.trace("Written content to file '{}'", file.getPath());
            return true;
        } catch (IOException e) {
            log.error("Failed to write to '{}', ex message: '{}'", file.getPath(), e.getMessage());
        }
        return false;
    }
}

package org.k8loud.executor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.common.testutil.DataStorageTestUtil;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataStorageServiceTest {
    private static final String ROOT_PATH = "./";
    private static final String FILE_NAME = "test_file.txt";
    private static final String FILE_PATH = Paths.get(ROOT_PATH, FILE_NAME).toString();
    private static final String CONTENT = "hello world";
    @Mock
    DataStorageProperties dataStoragePropertiesMock;
    private DataStorageService dataStorageService;

    @BeforeEach
    void setUp() {
        lenient().when(dataStoragePropertiesMock.getRootPath()).thenReturn(ROOT_PATH);
        dataStorageService = new DataStorageServiceImpl(dataStoragePropertiesMock);
    }

    @Test
    void testStore() throws IOException {
        // when
        String filePath = dataStorageService.store(FILE_NAME, CONTENT);

        // then
        assertFileExists(filePath);

        // cleanup
        DataStorageTestUtil.remove(filePath);
    }

    @Test
    void testStoreExistentFile() throws IOException {
        // given
        String filePath = dataStorageService.store(FILE_NAME, CONTENT);
        assertFileExists(filePath);

        // when
        String filePath1 = dataStorageService.store(FILE_NAME, CONTENT);

        // then
        assertFileExists(filePath1);

        // cleanup
        DataStorageTestUtil.remove(filePath);
        DataStorageTestUtil.remove(filePath1);
    }

    @Test
    void testRemove() throws IOException {
        // given
        when(dataStoragePropertiesMock.isRemovalPermitted()).thenReturn(true);
        DataStorageTestUtil.store(FILE_PATH, CONTENT);
        assertFileExists(FILE_PATH);

        // when
        boolean result = dataStorageService.remove(FILE_NAME);

        // then
        assertTrue(result);
        assertFileDoesntExist(FILE_PATH);

        // cleanup
        DataStorageTestUtil.safeRemove(FILE_PATH);
    }

    @Test
    void testRemoveNotPermitted() throws IOException {
        // given
        when(dataStoragePropertiesMock.isRemovalPermitted()).thenReturn(false);
        DataStorageTestUtil.store(FILE_PATH, CONTENT);
        assertFileExists(FILE_PATH);

        // when
        boolean result = dataStorageService.remove(FILE_NAME);

        // then
        assertFalse(result);
        assertFileExists(FILE_PATH);

        // cleanup
        DataStorageTestUtil.safeRemove(FILE_PATH);
    }

    @Test
    void testRemoveNonExistentFile() {
        // given
        when(dataStoragePropertiesMock.isRemovalPermitted()).thenReturn(true);
        assertFileDoesntExist(FILE_PATH);

        // when
        boolean result = dataStorageService.remove(FILE_PATH);

        // then
        assertFalse(result);
    }

    private void assertFileExists(String filePath) throws IOException {
        File file = new File(filePath);
        assertTrue(file.isFile());
        assertArrayEquals(Files.readAllBytes(Paths.get(file.getPath())), CONTENT.getBytes());
    }

    private void assertFileDoesntExist(String filePath) {
        File file = new File(filePath);
        assertFalse(file.isFile());
    }
}

package com.isuzumahub.diagnostic.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    /**
     * Stores a file and returns the path where it was stored
     * @param file The file to store
     * @param subDirectory Optional subdirectory within the upload directory
     * @return The path where the file was stored
     * @throws IOException If there's an error storing the file
     */
    String storeFile(MultipartFile file, String subDirectory) throws IOException;
    
    /**
     * Stores a file with optional compression and returns the path where it was stored
     * @param file The file to store
     * @param subDirectory Optional subdirectory within the upload directory
     * @param compress Whether to compress the file before storing
     * @return The path where the file was stored
     * @throws IOException If there's an error storing the file
     */
    String storeFile(MultipartFile file, String subDirectory, boolean compress) throws IOException;
    
    /**
     * Retrieves a file from storage
     * @param fileName The name of the file to retrieve
     * @param subDirectory Optional subdirectory within the upload directory
     * @return The path to the file
     */
    Path getFilePath(String fileName, String subDirectory);
    
    /**
     * Retrieves a file as bytes from storage
     * @param fileName The name of the file to retrieve
     * @param subDirectory Optional subdirectory within the upload directory
     * @param decompress Whether to decompress the file before returning
     * @return The file contents as bytes
     * @throws IOException If there's an error reading the file
     */
    byte[] getFileAsBytes(String fileName, String subDirectory, boolean decompress) throws IOException;
    
    /**
     * Deletes a file from storage
     * @param fileName The name of the file to delete
     * @param subDirectory Optional subdirectory within the upload directory
     * @return true if the file was deleted, false otherwise
     */
    boolean deleteFile(String fileName, String subDirectory);
    
    /**
     * Checks if a file exists in storage
     * @param fileName The name of the file to check
     * @param subDirectory Optional subdirectory within the upload directory
     * @return true if the file exists, false otherwise
     */
    boolean fileExists(String fileName, String subDirectory);
} 
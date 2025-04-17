package com.isuzumahub.diagnostic.service.impl;

import com.isuzumahub.diagnostic.config.FileStorageConfig;
import com.isuzumahub.diagnostic.exception.BadRequestException;
import com.isuzumahub.diagnostic.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageConfig fileStorageConfig;
    
    // Allowed file types for reports
    private static final List<String> ALLOWED_REPORT_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/jpeg",
            "image/png"
    );
    
    // Maximum file size in bytes (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        return storeFile(file, subDirectory, false);
    }
    
    @Override
    public String storeFile(MultipartFile file, String subDirectory, boolean compress) throws IOException {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Check if the file is empty
        if (file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file " + originalFileName);
        }
        
        // Check if the file name contains invalid characters
        if (originalFileName.contains("..")) {
            throw new BadRequestException("Filename contains invalid path sequence " + originalFileName);
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds the maximum allowed size of 10MB");
        }
        
        // Validate file type for reports
        if (subDirectory != null && subDirectory.equals("reports")) {
            validateReportFileType(file);
        }
        
        // Generate a unique file name
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        // Create the target directory if it doesn't exist
        Path targetLocation = getUploadPath(subDirectory, fileName);
        Files.createDirectories(targetLocation.getParent());
        
        // Copy the file to the target location
        if (compress) {
            // Compress the file before storing
            byte[] compressedData = compressFile(file.getBytes());
            try (InputStream inputStream = new ByteArrayInputStream(compressedData)) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Stored compressed file {} at {}", originalFileName, targetLocation);
        } else {
            // Store the file as is
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file {} at {}", originalFileName, targetLocation);
        }
        
        // Return the relative path for database storage
        return subDirectory != null ? subDirectory + "/" + fileName : fileName;
    }

    @Override
    public Path getFilePath(String fileName, String subDirectory) {
        return getUploadPath(subDirectory, fileName);
    }

    @Override
    public boolean deleteFile(String fileName, String subDirectory) {
        try {
            Path filePath = getFilePath(fileName, subDirectory);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", fileName, e);
            return false;
        }
    }

    @Override
    public boolean fileExists(String fileName, String subDirectory) {
        Path filePath = getFilePath(fileName, subDirectory);
        return Files.exists(filePath);
    }
    
    @Override
    public byte[] getFileAsBytes(String fileName, String subDirectory, boolean decompress) throws IOException {
        Path filePath = getFilePath(fileName, subDirectory);
        byte[] fileData = Files.readAllBytes(filePath);
        
        if (decompress) {
            return decompressFile(fileData);
        }
        
        return fileData;
    }
    
    private Path getUploadPath(String subDirectory, String fileName) {
        String uploadDir = fileStorageConfig.getUploadDir();
        if (subDirectory != null && !subDirectory.isEmpty()) {
            return Paths.get(uploadDir, subDirectory, fileName).toAbsolutePath().normalize();
        } else {
            return Paths.get(uploadDir, fileName).toAbsolutePath().normalize();
        }
    }
    
    private void validateReportFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_REPORT_TYPES.contains(contentType)) {
            throw new BadRequestException("Invalid file type. Allowed types are: PDF, DOC, DOCX, JPEG, PNG");
        }
    }
    
    private byte[] compressFile(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        
        outputStream.close();
        return outputStream.toByteArray();
    }
    
    private byte[] decompressFile(byte[] data) throws IOException {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(data);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[1024];
            
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            
            outputStream.close();
            return outputStream.toByteArray();
        } catch (DataFormatException e) {
            log.error("Error decompressing file", e);
            throw new BadRequestException("Failed to decompress file: " + e.getMessage());
        }
    }
} 
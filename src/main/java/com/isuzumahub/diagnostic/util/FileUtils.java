package com.isuzumahub.diagnostic.util;

import org.springframework.stereotype.Component;

@Component
public class FileUtils {
    
    /**
     * Determines the content type based on the file extension
     * @param fileName The name of the file
     * @return The MIME type of the file
     */
    public static String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
} 
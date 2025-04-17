package com.isuzumahub.diagnostic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LabTestDto {
    private Long id;
    
    @NotBlank(message = "Test name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String preparationInstructions;
    
    @NotNull(message = "Report delivery hours is required")
    @Positive(message = "Report delivery hours must be positive")
    private Integer reportDeliveryHours;
    
    private String sampleType;
    private boolean homeCollection = true;
    private boolean active = true;
} 
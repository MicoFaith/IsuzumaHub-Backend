package com.isuzumahub.diagnostic.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lab_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTest extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    private String preparationInstructions;

    @Column(nullable = false)
    private Integer reportDeliveryHours;

    private String sampleType;

    @Column(nullable = false)
    private boolean homeCollection = true;
} 
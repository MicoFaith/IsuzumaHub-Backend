package com.isuzumahub.diagnostic.service;

import com.isuzumahub.diagnostic.dto.LabTestDto;

import java.util.List;

public interface LabTestService {
    LabTestDto createTest(LabTestDto labTestDto);
    LabTestDto getTestById(Long id);
    LabTestDto getTestByName(String name);
    List<LabTestDto> getAllTests();
    List<LabTestDto> getTestsByCategory(String category);
    List<LabTestDto> getActiveTests();
    LabTestDto updateTest(Long id, LabTestDto labTestDto);
    void deleteTest(Long id);
    void toggleTestStatus(Long id);
} 
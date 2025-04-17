package com.isuzumahub.diagnostic.service.impl;

import com.isuzumahub.diagnostic.dto.LabTestDto;
import com.isuzumahub.diagnostic.exception.BadRequestException;
import com.isuzumahub.diagnostic.exception.ResourceNotFoundException;
import com.isuzumahub.diagnostic.model.LabTest;
import com.isuzumahub.diagnostic.repository.LabTestRepository;
import com.isuzumahub.diagnostic.service.LabTestService;
import com.isuzumahub.diagnostic.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabTestServiceImpl implements LabTestService {

    private final LabTestRepository labTestRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LabTestDto createTest(LabTestDto labTestDto) {
        if (labTestRepository.existsByName(labTestDto.getName())) {
            throw new BadRequestException("Test with this name already exists");
        }

        LabTest labTest = modelMapper.toLabTest(labTestDto);
        LabTest savedTest = labTestRepository.save(labTest);
        return modelMapper.toLabTestDto(savedTest);
    }

    @Override
    public LabTestDto getTestById(Long id) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "id", id));
        return modelMapper.toLabTestDto(labTest);
    }

    @Override
    public LabTestDto getTestByName(String name) {
        LabTest labTest = labTestRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "name", name));
        return modelMapper.toLabTestDto(labTest);
    }

    @Override
    public List<LabTestDto> getAllTests() {
        return labTestRepository.findAll().stream()
                .map(modelMapper::toLabTestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabTestDto> getTestsByCategory(String category) {
        return labTestRepository.findByCategory(category).stream()
                .map(modelMapper::toLabTestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabTestDto> getActiveTests() {
        return labTestRepository.findByActiveTrue().stream()
                .map(modelMapper::toLabTestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LabTestDto updateTest(Long id, LabTestDto labTestDto) {
        LabTest existingTest = labTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "id", id));
        
        // Check if name is being changed and if it's already in use
        if (!existingTest.getName().equals(labTestDto.getName()) && 
            labTestRepository.existsByName(labTestDto.getName())) {
            throw new BadRequestException("Test with this name already exists");
        }
        
        LabTest updatedTest = modelMapper.toLabTest(labTestDto);
        updatedTest.setId(existingTest.getId());
        
        LabTest savedTest = labTestRepository.save(updatedTest);
        return modelMapper.toLabTestDto(savedTest);
    }

    @Override
    @Transactional
    public void deleteTest(Long id) {
        if (!labTestRepository.existsById(id)) {
            throw new ResourceNotFoundException("LabTest", "id", id);
        }
        labTestRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleTestStatus(Long id) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "id", id));
        
        labTest.setActive(!labTest.isActive());
        labTestRepository.save(labTest);
    }
} 
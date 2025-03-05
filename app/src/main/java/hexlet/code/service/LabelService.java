package hexlet.code.service;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper mapper;

    public List<LabelDTO> findAll() {
        return labelRepository.findAll().stream()
                .map(mapper::map)
                .toList();
    }

    public LabelDTO findById(long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label id " + id + " not found"));
        return mapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO labelData) {
        var label = mapper.map(labelData);
        labelRepository.save(label);
        return mapper.map(label);
    }

    public LabelDTO update(long id, LabelUpdateDTO labelData) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label id " + id + " not found"));
        mapper.update(labelData, label);
        labelRepository.save(label);
        return mapper.map(label);
    }

    public void delete(long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label id " + id + " not found"));
        labelRepository.delete(label);
    }
}

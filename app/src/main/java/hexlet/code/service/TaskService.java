package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskFilterDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskSpecification specBuilder;

    @Autowired
    private TaskMapper mapper;

    public List<TaskDTO> findAll(TaskFilterDTO params) {
        var spec = specBuilder.build(params);
        return taskRepository.findAll(spec).stream()
                .map(mapper::map)
                .toList();
    }

    public TaskDTO findById(long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task id " + id + " not found"));
        return mapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO taskData) {
        var task = mapper.map(taskData);
        taskRepository.save(task);
        return mapper.map(task);
    }

    public TaskDTO update(long id, TaskUpdateDTO taskData) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task id " + id + " not found"));
        mapper.update(taskData, task);
        taskRepository.save(task);
        return mapper.map(task);
    }

    public void delete(long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task id " + id + " not found"));
        taskRepository.delete(task);
    }
}

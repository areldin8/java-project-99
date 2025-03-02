package hexlet.code.service;

import hexlet.code.dto.taslstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taslstatus.TaskStatusDTO;
import hexlet.code.dto.taslstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper mapper;

    public List<TaskStatusDTO> findAll() {
        return taskStatusRepository.findAll().stream()
                .map(mapper::map)
                .toList();
    }

    public TaskStatusDTO findById(long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status id " + id + " not found"));
        return mapper.map(taskStatus);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        TaskStatus taskStatus = mapper.map(taskStatusData);
        taskStatusRepository.save(taskStatus);
        return mapper.map(taskStatus);
    }

    public TaskStatusDTO update(long id, TaskStatusUpdateDTO taskStatusData) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status id " + id + " not found"));
        mapper.update(taskStatusData, taskStatus);
        taskStatusRepository.save(taskStatus);
        return mapper.map(taskStatus);
    }

    public void delete(long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status id " + id + " not found"));
        taskStatusRepository.delete(taskStatus);
    }
}

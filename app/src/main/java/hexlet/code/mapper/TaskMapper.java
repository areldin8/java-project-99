package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "labelIds", source = "labels", qualifiedByName = "modelToLabelId")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "statusToModel")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "labelIdToModel")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusToModel")
    @Mapping(source = "labelIds", target = "labels", qualifiedByName = "labelIdToModel")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Named("statusToModel")
    public final TaskStatus statusToModel(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow();
    }

    @Named("labelIdToModel")
    public final Set<Label> labelIdsToModel(Set<Long> labelIds) {
        return new HashSet<>(labelRepository.findByIdIn(labelIds));
    }

    @Named("modelToLabelId")
    public final Set<Long> modelToLabelIds(Set<Label> labels) {
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}


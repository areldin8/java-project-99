package hexlet.code.util;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;

import java.util.HashSet;


@Getter
@Component
public class ModelGenerator {

    private static final int PASS_MIN = 3;
    private static final int PASS_MAX = 100;

    private Model<User> userModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<TaskCreateDTO> taskCreateDTOModel;
    private Model<Label> labelModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password(PASS_MIN, PASS_MAX))
                .toModel();
        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.internet().domainName())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
                .toModel();
        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getTaskStatus))
                .supply(Select.field(Task::getIndex), () -> faker.number().positive())
                .supply(Select.field(Task::getName), () -> faker.name().title())
                .supply(Select.field(Task::getDescription), () -> faker.lorem().paragraph())
                .supply(Select.field(Task::getLabels), () -> new HashSet<>())
                .toModel();
        taskCreateDTOModel = Instancio.of(TaskCreateDTO.class)
                .ignore(Select.field(TaskCreateDTO::getAssigneeId))
                .ignore(Select.field(TaskCreateDTO::getStatus))
                .supply(Select.field(TaskCreateDTO::getIndex), () -> faker.number().positive())
                .supply(Select.field(TaskCreateDTO::getName), () -> faker.name().title())
                .supply(Select.field(TaskCreateDTO::getDescription), () -> faker.lorem().paragraph())
                .supply(Select.field(TaskCreateDTO::getLabelIds), () -> new HashSet<>())
                .toModel();
        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .supply(Select.field(Label::getName), () -> faker.lorem().sentence())
                .toModel();
    }
}


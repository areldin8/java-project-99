package hexlet.code.components;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final TaskStatusRepository taskStatusRepository; // Используем репозиторий
    private final LabelRepository labelRepository; // Используем репозиторий

    @Override

    public void run(ApplicationArguments args) throws Exception {
        // Инициализация администратора
        var admin = new UserCreateDTO();
        admin.setEmail("hexlet@example.com");
        admin.setPassword("qwerty");
        admin.setFirstName("admin");
        admin.setLastName("admin");
        userService.create(admin);

        // Инициализация статусов задач
        Map<String, TaskStatus> taskStatusMap = new HashMap<>();
        taskStatusMap.put("draft", new TaskStatus("Draft", "draft"));
        taskStatusMap.put("to_review", new TaskStatus("To Review", "to_review"));
        taskStatusMap.put("to_be_fixed", new TaskStatus("To Be Fixed", "to_be_fixed"));
        taskStatusMap.put("to_publish", new TaskStatus("To Publish", "to_publish"));
        taskStatusMap.put("published", new TaskStatus("Published", "published"));

        // Сохраняем статусы, если они еще не существуют
        for (Map.Entry<String, TaskStatus> entry : taskStatusMap.entrySet()) {
            String slug = entry.getKey();
            TaskStatus taskStatus = entry.getValue();
            if (taskStatusRepository.findBySlug(slug).isEmpty()) {
                taskStatusRepository.save(taskStatus);
                System.out.println("Создан новый статус задачи: " + taskStatus.getName());
            } else {
                System.out.println("Статус задачи с слагом " + slug + " уже существует.");
            }
        }

        // Инициализация меток
        Map<String, Label> labelMap = new HashMap<>();
        labelMap.put("feature", new Label("feature"));
        labelMap.put("bug", new Label("bug"));

        // Сохраняем метки, если они еще не существуют
        for (Map.Entry<String, Label> entry : labelMap.entrySet()) {
            String name = entry.getKey();
            Label label = entry.getValue();
            if (labelRepository.findByName(name).isEmpty()) {
                labelRepository.save(label);
                System.out.println("Создана новая метка: " + label.getName());
            } else {
                System.out.println("Метка с названием " + label.getName() + " уже существует.");
            }
        }
    }
}




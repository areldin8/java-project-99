package hexlet.code.components;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createAdmin();
        createTaskStatus("Draft", "draft");
        createTaskStatus("ToReview", "to_review");
        createTaskStatus("ToBeFixed", "to_be_fixed");
        createTaskStatus("ToPublish", "to_publish");
        createTaskStatus("Published", "published");
        createLabel("feature");
        createLabel("bug");
    }

    private void createAdmin() {
        var email = "hexlet@example.com";
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        var user = new User();
        user.setEmail(email);
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setRole("ADMIN");
        var passwordDigest = passwordEncoder.encode("qwerty");
        user.setPasswordDigest(passwordDigest);

        userRepository.save(user);
    }

    private void createTaskStatus(String name, String slug) {
        if (taskStatusRepository.findBySlug(slug).isPresent()) {
            return;
        }
        var taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        taskStatusRepository.save(taskStatus);
    }

    private void createLabel(String name) {
        if (labelRepository.findByName(name).isPresent()) {
            return;
        }
        var label = new Label();
        label.setName(name);
        labelRepository.save(label);
    }
}



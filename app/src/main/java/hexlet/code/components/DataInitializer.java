package hexlet.code.components;

import hexlet.code.dto.user.UserCreateDto;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var admin = new UserCreateDto();

        admin.setEmail("hexlet@example.com");
        admin.setPassword("qwerty");
        userService.createUser(admin);
    }
}

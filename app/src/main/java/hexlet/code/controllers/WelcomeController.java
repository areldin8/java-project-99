package hexlet.code.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public String welcome() {
        return "Welcome to Spring";
    }
}


package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateDTO {

    private static final int PASS_MIN = 3;
    private static final int PASS_MAX = 100;

    @Email(regexp = "^\\w+(\\.\\w+)*@(\\w+\\.){1}\\w{2,4}$")
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @Size(min = PASS_MIN, max = PASS_MAX)
    private String password;
}

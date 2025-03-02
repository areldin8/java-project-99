package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    private static final int PASS_MIN = 3;
    private static final int PASS_MAX = 100;

    @Email(regexp = "^\\w+(\\.\\w+)*@(\\w+\\.){1}\\w{2,4}$")
    private JsonNullable<String> email;

    @NotBlank
    private JsonNullable<String> firstName;

    @NotBlank
    private JsonNullable<String> lastName;

    @Size(min = PASS_MIN, max = PASS_MAX)
    private JsonNullable<String> password;
}

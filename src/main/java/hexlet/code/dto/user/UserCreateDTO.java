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

    private String firstName;
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Size(min = 3)
    private String password;

}

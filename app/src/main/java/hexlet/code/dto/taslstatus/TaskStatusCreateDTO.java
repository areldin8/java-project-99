package hexlet.code.dto.taslstatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+(?:_[a-zA-Z]+)*$")
    private String slug;

}

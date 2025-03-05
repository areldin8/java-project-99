package hexlet.code.dto.taslstatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class TaskStatusUpdateDTO {

    @NotBlank
    private JsonNullable<String> name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+(?:_[a-zA-Z]+)*$")
    private JsonNullable<String> slug;
}

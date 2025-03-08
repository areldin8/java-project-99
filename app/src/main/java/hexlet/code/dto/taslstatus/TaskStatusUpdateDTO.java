package hexlet.code.dto.taslstatus;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class TaskStatusUpdateDTO {

    @NotBlank
    @Size(min = 1)
    private JsonNullable<String> name;
    @NotBlank
    @Column(unique = true)
    private JsonNullable<String> slug;
}

package hexlet.code.dto.labels;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabelUpdateDTO {

    @NotBlank
    @Size(min = 3, max = 1000)
    private JsonNullable<String> name;
}

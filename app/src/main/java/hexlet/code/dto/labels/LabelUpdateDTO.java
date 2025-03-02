package hexlet.code.dto.labels;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class LabelUpdateDTO {

    private static final int MIN = 3;
    private static final int MAX = 1000;

    @Size(min = MIN, max = MAX)
    private JsonNullable<String> name;
}

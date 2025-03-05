package hexlet.code.dto.labels;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelCreateDTO {
    private static final int MIN = 3;
    private static final int MAX = 1000;

    @Size(min = MIN, max = MAX)
    private String name;
}

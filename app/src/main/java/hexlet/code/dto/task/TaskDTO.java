package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
public class TaskDTO {

    private long id;

    private Integer index;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String createdAt;

    @JsonProperty("assignee_id")
    private long assigneeId;

    private String title;

    private String content;

    private String status;

    private Set<Long> taskLabelIds = new HashSet<>();
}

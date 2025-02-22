package hexlet.code.specification;

import hexlet.code.dto.task.TaskFilterDTO;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskFilterDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssignee(Long.valueOf(params.getAssigneeId())))
                .and(withStatus(params.getStatus())
                        .and(withLabel(params.getLabelId())));
    }

    private Specification<Task> withTitleCont(String substring) {
        return (root, query, cb) -> substring == null
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + substring.toLowerCase() + "%");
    }

    private Specification<Task> withAssignee(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null
                ? cb.conjunction()
                : cb.equal(root.join("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.join("taskStatus", JoinType.INNER).get("slug"), status);
    }

    private Specification<Task> withLabel(Long labelId) {
        return (root, query, cb) -> labelId == null
                ? cb.conjunction()
                : cb.equal(root.join("labels", JoinType.INNER).get("id"), labelId);
    }
}



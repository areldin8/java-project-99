package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "statuses")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "slug"})
public class TaskStatus implements BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @NotBlank
    @Size(min = 1)
    @Column(unique = true)
    private String name;

    @NotBlank
    @Size(min = 1)
    @Column(unique = true)
    private String slug;

    @CreatedDate
    private LocalDate createdAt;

    public TaskStatus(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public TaskStatus() {
    }

}

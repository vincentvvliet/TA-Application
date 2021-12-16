package nl.tudelft.sem.Application.entities;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;




@Entity
@Data
@Table(name = "application", schema = "applicationschema")
public class Application {
    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @Column(name = "studentid")
    @JsonProperty(value = "studentId")
    private UUID studentId;

    @Column(name = "courseid")
    @JsonProperty(value = "courseId")
    private UUID courseId;

    @Column(name = "accepted")
    @JsonProperty(value = "accepted")
    private boolean accepted;

    public Application() {
        this.id = UUID.randomUUID();
    }

    /** the constructor of the Application class.
     *
     * @param courseId the id of the linked course
     * @param studentId the id of the linked student
     */
    public Application(UUID courseId, UUID studentId) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.studentId = studentId;
    }

}


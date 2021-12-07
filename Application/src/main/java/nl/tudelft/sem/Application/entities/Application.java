package nl.tudelft.sem.Application.entities;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "application" , schema = "applicationschema")
public class Application {
    @Id
    @Column(name = "id" , nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @Column(name = "studentid" )
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

    public Application(UUID courseId , UUID studentId) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.studentId = studentId;
    }

}


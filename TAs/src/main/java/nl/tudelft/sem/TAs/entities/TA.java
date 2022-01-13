package nl.tudelft.sem.TAs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "ta", schema = "taschema")
public class TA {
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

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "ta_detail_ID")
    private TADetail taDetail;

    @OneToOne(targetEntity = Contract.class, cascade = CascadeType.ALL)
    private Contract contract;

    public TA() {
        this.id = UUID.randomUUID();
        this.taDetail = new TADetail();
    }

    public TA(UUID courseId, UUID studentId) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.studentId = studentId;
        this.taDetail = new TADetail();
    }

}

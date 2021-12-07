package nl.tudelft.sem.TAs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "ta" , schema = "taschema")
public class TA {
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

    @Column(name = "rating")
    @JsonProperty(value = "rating")
    private int rating;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable( name = "contract", joinColumns = @JoinColumn ( name = "id"))
    private Contract contract;

    public TA() {
        this.id = UUID.randomUUID();
    }

    public TA(UUID courseId , UUID studentId) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.studentId = studentId;
    }

    public void setContract(Contract c){
        this.contract  = c;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}

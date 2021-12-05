package nl.tudelft.sem.Course.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "grade" , schema = "courseschema")
@Data
public class Grade {
    @Id
    @Column(name = "id" , nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @ManyToOne
    @JsonProperty(value = "courseid")
    @JoinTable( name = "course", joinColumns = @JoinColumn ( name = "id"))
    private Course course;

    @Column
    @JsonProperty(value = "studentid")
    private UUID studentId;

    @Column
    @JsonProperty(value = "grade")
    private double grade;

    public Grade(UUID studentId , Course course , double grade){
        this.id = UUID.randomUUID();
        this.studentId = studentId;
        this.course = course;
        this.grade = grade;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}

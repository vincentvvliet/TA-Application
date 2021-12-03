package nl.tudelft.sem.Course.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "course" , schema = "courseschema")
@Data
public class Course {
    @Id
    @Column(name = "id" , nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @Column(name = "course_code")
    @JsonProperty(value = "course_code")
    private String course_code;

    @Column(name = "nr_participants")
    @JsonProperty(value = "nr_participants")
    private int nr_participants;

    @Column(name = "start_date")
    @JsonProperty(value = "start_date")
    private Date start_date;

    @Column(name = "end_date")
    @JsonProperty(value = "end_date")
    private Date end_date;

    public Course() {
        this.id = UUID.randomUUID();
    }
}

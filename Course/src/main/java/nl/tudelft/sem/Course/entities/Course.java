package nl.tudelft.sem.Course.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
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

    @Column(name = "startDate")
    @JsonProperty(value = "startDate")
    private LocalDate startDate;

    @Column(name = "end_date")
    @JsonProperty(value = "end_date")
    private LocalDate end_date;

    public Course() {
        this.id = UUID.randomUUID();
    }

    public Course(String course_code, int nr_participants, LocalDate startDate, LocalDate end_date) {
        this.id = UUID.randomUUID();
        this.course_code = course_code;
        this.nr_participants = nr_participants;
        this.startDate = startDate;
        this.end_date = end_date;
    }

    public String getCourseCode() {
        return course_code;
    }

    public int getNrParticipants() {
        return nr_participants;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return end_date;
    }

    public void setCourseCode(String course_code) {
        this.course_code = course_code;
    }

    public void setStartDate(LocalDate start_date) {
        this.startDate = start_date;
    }

    public void setEndDate(LocalDate end_date) {
        this.end_date = end_date;
    }

    public void setNrParticipants(int nr_participants) {
        this.nr_participants = nr_participants;
    }
}

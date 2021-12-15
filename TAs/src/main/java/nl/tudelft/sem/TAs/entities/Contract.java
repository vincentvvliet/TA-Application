package nl.tudelft.sem.TAs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "contract" , schema = "taschema")
public class Contract {
    @Id
    @Column(name = "id" , nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @Column(name = "studentid" , nullable = false)
    @JsonProperty(value = "studentId")
    private UUID studentId;

    @Column(name = "courseid", nullable = false)
    @JsonProperty(value = "courseId")
    private UUID courseId;

    @Column(name = "date")
    @JsonProperty(value = "date")
    private Date date;

    @Column(name = "maxhours")
    @JsonProperty(value = "maxHours")
    private Integer maxHours;

    @Column(name = "task")
    @JsonProperty(value = "taskDescription")
    private String taskDescription;

    @Column(name = "salary")
    @JsonProperty(value = "salary")
    private double salaryPerHour;

    public Contract(UUID studentId , UUID courseId){
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.studentId = studentId;
    }

    public Contract() {
        this.id = UUID.randomUUID();
    }

    public UUID getStudentId() {
        return studentId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public Date getDate() {
        return date;
    }

    public Integer getMaxHours() {
        return maxHours;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public double getSalaryPerHour() {
        return salaryPerHour;
    }

    public UUID getId() {
        return id;
    }

    public void setDate(Date d){
        this.date = d;
    }

    public void setMaxHours(int maxHours) {
        this.maxHours = maxHours;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setSalaryPerHour(double salaryPerHour) {
        this.salaryPerHour = salaryPerHour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Double.compare(contract.salaryPerHour, salaryPerHour) == 0 && id.equals(contract.id) && studentId.equals(contract.studentId) && courseId.equals(contract.courseId) && Objects.equals(date, contract.date) && Objects.equals(maxHours, contract.maxHours) && Objects.equals(taskDescription, contract.taskDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, courseId, date, maxHours, taskDescription, salaryPerHour);
    }
}

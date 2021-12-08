package nl.tudelft.sem.Application.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;



@Entity
@Data
@Table(name = "application" ,schema = "applicationschema")
public class Application {
    @Id
    @Column(name = "id" ,nullable = false)
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
    public Application(UUID courseId , UUID studentId) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.studentId = studentId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public UUID getCourseId() {
        return courseId;
    }
<<<<<<< HEAD
=======

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean validate(){
        Boolean isValid = false;
        try{
            Validator validator = new IsCourseOpen(); // create chain of responsibility
            validator.setLast(new IsGradeSufficient());
            validator.setLast(new IsUniqueApplication());

            isValid = validator.handle(this);
        } catch (InvalidApplicationException e){
            e.printStackTrace();
        }
        return isValid;
    }

    public Optional<Double> getGrade(){
        String uri = "localhost:47112/grade/getGrade/" + this.studentId  + "/" + this.courseId ;
        Optional<Double> result = restTemplate.getForObject(uri, Optional.class);
        return result;
    }

    public Optional<LocalDate> getCourseStartDate(){
        String uri = "localhost:47112/course/getCourseStartDate/" + this.courseId;
        Optional<LocalDate> result = restTemplate.getForObject(uri, Optional.class);
        return result;
    }

>>>>>>> Implement_apply_application
}


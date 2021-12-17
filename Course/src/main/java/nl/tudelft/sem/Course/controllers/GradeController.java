package nl.tudelft.sem.Course.controllers;

import nl.tudelft.sem.Course.entities.Course;
import nl.tudelft.sem.Course.entities.Grade;
import nl.tudelft.sem.Course.repositories.CourseRepository;
import nl.tudelft.sem.Course.repositories.GradeRepository;
import nl.tudelft.sem.DTO.GradeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/grade/")
public class GradeController {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private GradeRepository gradeRepository;

    /**
     * GET endpoint retrieves grade by studentId and courseId
     * @param studentId (UUID) of the student
     * @param courseId (UUID) of the course
     * @return optional of grade
     */
    @GetMapping("/getGrade/{student_id}/{course_id}")
    public Optional<Grade> getGradeByStudentAndCourse(@PathVariable(value = "student_id") UUID studentId, @PathVariable(value = "course_id") UUID courseId) {
        return gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    /**
     * GET endpoint retrieves GradeDTOs for grades tied to course.
     * @param courseId id of sepecific course.
     * @return Flux of GradeDTOs that are for specified course.
     */
    @GetMapping("/getGrades/{course_id}")
    public Flux<GradeDTO> getGrades(@PathVariable("course_id") UUID courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested course does not exits!");
        }
        List<Grade> grades = gradeRepository.findAllByCourse(course.get());
        if (grades.isEmpty()) {
            // throwing is not necessary here I believe.
        }
        // Transform into a Flux of gradeDTOs
        return Flux.fromStream(
            grades.stream()
                .map(
                    (Grade g)-> new GradeDTO(g.getStudentId(), g.getGrade())
                )
        );
    }

    /**
     * POST endpoint creating a grade (identified by studentId and courseId)
     * @param studentId of the student whose grade is created
     * @param courseId of the course for which grade is created
     * @param gradeValue of the given student for the given course
     * @return true after the grade is created and saved in the database
     */
    @PostMapping("/createGrade/{student_id}/{course_id}/{grade}")
    public boolean createGrade(@PathVariable(value = "student_id") UUID studentId , @PathVariable(value = "course_id") UUID courseId, @PathVariable(value = "grade") double gradeValue) {
        Course course =  courseRepository.findById(courseId).orElseThrow(() -> new NoSuchElementException("course does not exist"));
        if (gradeValue < 1.0 || gradeValue > 10.0) {
            throw new IllegalArgumentException("grade must be between 1.0 and 10.0");
        }
        Grade grade = new Grade(studentId, course, gradeValue);
        gradeRepository.save(grade);
        return true;
    }

    /**PATCH Endpoint to change the value of a grade
     * @param id of the grade whose value is modified
     * @param gradeValue new value of the grade
     * @return true after the grade is successfully modified
     */
    @RequestMapping("/modifyGrade/{id}/{grade}")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean modifyGradeValue(@PathVariable(value = "id") UUID id, @PathVariable(value = "grade") double gradeValue) {
        Grade grade = gradeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("no grade found"));
        if (gradeValue < 1.0 || gradeValue > 10.0) {
            throw new IllegalArgumentException("grade must be between 1.0 and 10.0");
        }
        grade.setGrade(gradeValue);
        gradeRepository.save(grade);
        return true;
    }
}

package nl.tudelft.sem.Course.controllers;

import nl.tudelft.sem.Course.entities.Course;
import nl.tudelft.sem.Course.repositories.CourseRepository;
import nl.tudelft.sem.Course.repositories.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/course/")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    private final int selectionPeriod = 3; // amount of time before start of course a student can register

    /**
     * GET endpoint retrieves course by id
     * @param id (UUID) of the course
     * @return optional of course
     */
    @GetMapping("/getCourse/{id}")
    public Optional<Course> getCourseById(@PathVariable(value = "id") UUID id) {
        return courseRepository.findById(id);
    }

    /**
     * GET endpoint retrieves startDate of course by id
     * @param id (UUID) of the course
     * @return optional of course
     */
    @GetMapping("/getCourseStartDate/{id}")
    public Optional<LocalDate> getCourseStartDateById(@PathVariable(value = "id") UUID id) {
        Optional<Course> course = courseRepository.findById(id);
        Optional<LocalDate> date = Optional.empty();
        if(course.isPresent()){
            date = Optional.of(course.get().getStartDate());
        }
        return date;
    }


    /**
     * GET endpoint retrieves all existing courses
     * @return list of courses
     */
    @GetMapping("/getCourses")
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    /**
     * GET endpoint retrieves all open courses
     * @return list of courses
     */
    @GetMapping("/getOpenCourses")
    public List<Course> getOpenCourses() {
        return courseRepository.findByStartDateBetween(LocalDate.now(), LocalDate.now().plusWeeks(selectionPeriod));
    }

    /**
     * POST endpoint creating a course (identified by studentId and courseId)
     * @param course_code of the course
     * @param nr_participants of the course
     * @param start_date of the course
     * @param end_date of the course
     * @return true if the course is successfully created and saved in the database, false otherwise
     */
    @PostMapping("/createCourse/{course_code}/{nr_participants}/{start_date}/{end_date}")
    public boolean createCourse(@PathVariable(value = "course_code") String course_code, @PathVariable(value = "nr_participants") int nr_participants, @PathVariable(value = "start_date") LocalDate start_date, @PathVariable(value = "end_date") LocalDate end_date) {
        if (start_date.isAfter(end_date)) {
            throw new IllegalArgumentException("start date is after end date of the course");
        }
        if (nr_participants < 0) {
            throw new IllegalArgumentException("number of participants is negative");
        }
        Course course = new Course(course_code, nr_participants, start_date, end_date);
        courseRepository.save(course);
        return true;
    }

    /**PATCH Endpoint to change the code of a course
     * @param id of the course
     * @param course_code new code of the course
     * @return true if the object was successfully modified
     */
    @RequestMapping("/modifyCourseCode/{id}/{course_code}")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean modifyCourseCode(@PathVariable(value = "id") UUID id, @PathVariable(value = "course_code") String course_code) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        course.setCourseCode(course_code);
        courseRepository.save(course);
        return true;
    }

    /**PATCH Endpoint to change the number of participants of a course
     * @param id of the course
     * @param nr_participants new number of participants of the course
     * @return true if the object was successfully modified
     */
    @RequestMapping("/modifyNrParticipants/{id}/{nr_participants}")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean modifyNrParticipants(@PathVariable(value = "id") UUID id, @PathVariable(value = "nr_participants") int nr_participants) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        if (nr_participants < 0) {
            throw new IllegalArgumentException("number of participants is negative");
        }
        course.setNrParticipants(nr_participants);
        courseRepository.save(course);
        return true;
    }

    /**PATCH Endpoint to change the start date of a course
     * @param id of the course
     * @param start_date new start date of the course
     * @return true if the object was successfully modified
     */
    @RequestMapping("/modifyStartDate/{id}/{start_date}")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean modifyStartDate(@PathVariable(value = "id") UUID id, @PathVariable(value = "start_date") LocalDate start_date) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        if (start_date.isAfter(course.getEndDate())) {
            throw new IllegalArgumentException("start date is after end date of the course");
        }
        course.setStartDate(start_date);
        courseRepository.save(course);
        return true;
    }

    /**PATCH Endpoint to change the end date of a course
     * @param id of the course
     * @param end_date new end date of the course
     * @return true if the object was successfully modified
     */
    @RequestMapping("/modifyEndDate/{id}/{end_date}")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean modifyEndDate(@PathVariable(value = "id") UUID id, @PathVariable(value = "end_date") LocalDate end_date) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        if (end_date.isBefore(course.getStartDate())) {
            throw new IllegalArgumentException("end date is before start date of the course");
        }
        course.setEndDate(end_date);
        courseRepository.save(course);
        return true;
    }

    /**
     * DELETE endpoint deletes a course by id
     * @param id of the course
     * @return true if the deletion was successful, false otherwise
     */
    @DeleteMapping("deleteCourse/{id}")
    public boolean deleteCourse(@PathVariable (value = "id") UUID id) {
        try {
            courseRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

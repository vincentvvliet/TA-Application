package nl.tudelft.sem.Course.controllers;

import nl.tudelft.sem.Course.entities.Course;
import nl.tudelft.sem.Course.entities.Grade;
import nl.tudelft.sem.Course.repositories.CourseRepository;
import nl.tudelft.sem.Course.repositories.GradeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest
public class GradeControllerTest {

    @Autowired
    GradeController gradeController;

    @MockBean
    CourseRepository courseRepository;
    @MockBean
    GradeRepository gradeRepository;

    UUID studentId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    Course course = new Course();
    Grade grade = new Grade(UUID.randomUUID(), course , 7.8);


    @BeforeEach
    public void init() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.ofNullable(course));
        when(gradeRepository.findByStudentIdAndCourseId(studentId,courseId)).thenReturn(Optional.ofNullable(grade));
    }

    @Test
    public void getGradeTest() {
        Assertions.assertEquals(grade,gradeController.getGradeByStudentAndCourse(studentId,courseId).get());
    }
}

package nl.tudelft.sem.Course.controllers;

import nl.tudelft.sem.Course.entities.Course;
import nl.tudelft.sem.Course.entities.Grade;
import nl.tudelft.sem.Course.repositories.CourseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@SpringBootTest
public class CourseControllerTest {
    @Autowired
    CourseController courseController;
    @MockBean
    CourseRepository courseRepository;

    UUID id = UUID.randomUUID();
    Course course = new Course();
    List<Course> courseList = new ArrayList<>();

    @BeforeEach
    public void init() {
        courseList.add(course);
        when(courseRepository.findById(id)).thenReturn(Optional.ofNullable(course));
    }

    @Test
    public void getCourseTest() {
        Assertions.assertEquals(course,courseController.getCourseById(id).block());
    }

    @Test
    public void getCourseStartDateTest() {
        LocalDate date = LocalDate.now();
        course.setStartDate(date);
        Assertions.assertEquals(date,courseController.getCourseStartDateById(id).block());

    }

    @Test
    public void getNrParticipantsTest() {
        int nr = 130;
        course.setNrParticipants(nr);
        Assertions.assertEquals(nr,courseController.getCourseNrParticipantsById(id).block());
    }

    @Test
    public void getAllTest() {
        when(courseRepository.findAll()).thenReturn(courseList);
        Assertions.assertEquals(courseList,courseController.getCourses().toStream().collect(Collectors.toList()));
    }

    @Test
    public void getOpenTest() {
        LocalDate date = LocalDate.now().plusWeeks(10);
        course.setStartDate(date);
        courseList.clear();
        courseList.add(course);
        when(courseRepository.findByStartDateIsAfter(LocalDate.now().plusWeeks(3))).thenReturn(courseList);
        Assertions.assertEquals(courseList,courseController.getOpenCourses().toStream().collect(Collectors.toList()));
    }

    @Test
    public void modifyCourseCodeTest() {
        courseController.modifyCourseCode(id,"CSE400");
        Assertions.assertEquals(course.getCourseCode() , "CSE400");
    }

    @Test
    public void modifyNrParticipantsTest() {
        courseController.modifyNrParticipants(id,35);
        Assertions.assertEquals(course.getNrParticipants() , 35);
    }

    @Test
    public void modifyNrParticipantsWrongTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> courseController.modifyNrParticipants(id, -8));
        String expectedMessage = "number of participants is negative";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void deleteTest() {
        courseController.deleteCourse(id);
        verify(courseRepository).deleteById(id);
    }
}

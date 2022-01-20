package nl.tudelft.sem.Course.controllers;

import nl.tudelft.sem.Course.entities.Course;
import nl.tudelft.sem.Course.entities.Grade;
import nl.tudelft.sem.Course.repositories.CourseRepository;
import nl.tudelft.sem.Course.repositories.GradeRepository;
import nl.tudelft.sem.DTO.GradeDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.*;

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
    Grade grade = new Grade(studentId, course, 7.8);


    @BeforeEach
    public void init() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.ofNullable(course));
        when(gradeRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.ofNullable(grade));
        when(gradeRepository.findById(grade.getId())).thenReturn(Optional.ofNullable(grade));
    }

    @Test
    public void getGradeByStudentAndCourseTest() {
        Assertions.assertEquals(grade, gradeController.getGradeByStudentAndCourse(studentId, courseId).get());
    }

    @Test
    public void getGradesListTest() {
        GradeDTO gradeDTO = new GradeDTO(studentId, grade.getGrade());
        List<Grade> list = new ArrayList<>();
        list.add(grade);
        when(gradeRepository.findAllByCourse(course)).thenReturn(list);
        Assertions.assertEquals(gradeController.getGrades(courseId).blockFirst(), gradeDTO);
    }

    @Test
    public void getEmptyGradesListTest() {
        when(gradeRepository.findAllByCourse(course)).thenReturn(List.of());
        Assertions.assertNull(gradeController.getGrades(courseId).blockFirst());
    }

    @Test
    public void getGradesListEmptyCourseTest() {
        when(courseRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> gradeController.getGrades(UUID.randomUUID()));
        String expectedMessage = "Requested course does not exits!";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getGradeDTOTest() {
        GradeDTO gradeDTO = new GradeDTO(studentId, grade.getGrade());
        Assertions.assertEquals(gradeController.getGrades(courseId, studentId).block(), gradeDTO);
    }

    @Test
    public void getEmptyGradeDTOTest() {
        when(gradeRepository.findByStudentIdAndCourseId(UUID.randomUUID(), courseId)).thenReturn(Optional.empty());
        Assertions.assertEquals(gradeController.getGrades(courseId, UUID.randomUUID()).block(), Mono.empty().block());
    }

    @Test
    public void createGradeTest() {
        Assertions.assertTrue(gradeController.createGrade(studentId, courseId, 7.8));
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    public void createGradeLessThanOneTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> gradeController.createGrade(studentId, courseId, 0.8));
        String expectedMessage = "grade must be between 1.0 and 10.0";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @Test
    public void createGradeGreaterThanTenTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> gradeController.createGrade(studentId, courseId, 11.0));
        String expectedMessage = "grade must be between 1.0 and 10.0";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @Test
    public void createGradeEqualToOneTest() {
        Assertions.assertTrue(gradeController.createGrade(studentId, courseId, 1.0));
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    public void createGradeEqualToTenTest() {
        Assertions.assertTrue(gradeController.createGrade(studentId, courseId, 10.0));
        verify(gradeRepository).save(any(Grade.class));
    }


    @Test
    public void createGradeNullCourseTest() {
        when(courseRepository.findById(UUID.randomUUID())).thenReturn(null);
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> gradeController.createGrade(studentId, UUID.randomUUID(), 8.0));
        String expectedMessage = "course does not exist";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @Test
    public void modifyGradeTest() {
        Assertions.assertTrue(gradeController.modifyGradeValue(grade.getId(), 9.0));
        Assertions.assertEquals(9.0, grade.getGrade());
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    public void modifyGradeLessThanOneTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> gradeController.modifyGradeValue(grade.getId(), 0.8));
        String expectedMessage = "grade must be between 1.0 and 10.0";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @Test
    public void modifyGradeGreaterThanTenTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> gradeController.modifyGradeValue(grade.getId(), 11.0));
        String expectedMessage = "grade must be between 1.0 and 10.0";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @Test
    public void modifyGradeEqualToOneTest() {
        Assertions.assertTrue(gradeController.modifyGradeValue(grade.getId(), 1.0));
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    public void modifyGradeEqualToTenTest() {
        Assertions.assertTrue(gradeController.modifyGradeValue(grade.getId(), 10.0));
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    public void modifyGradeNullCourseTest() {
        when(courseRepository.findById(UUID.randomUUID())).thenReturn(null);
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> gradeController.modifyGradeValue(UUID.randomUUID(), 8.0));
        String expectedMessage = "no grade found";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(gradeRepository, never()).save(any(Grade.class));
    }
}

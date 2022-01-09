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

import java.util.ArrayList;
import java.util.List;
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
    Grade grade = new Grade(studentId, course , 7.8);


    @BeforeEach
    public void init() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.ofNullable(course));
        when(gradeRepository.findByStudentIdAndCourseId(studentId,courseId)).thenReturn(Optional.ofNullable(grade));
        when(gradeRepository.findById(grade.getId())).thenReturn(Optional.ofNullable(grade));
    }

    @Test
    public void getGradeTest() {
        Assertions.assertEquals(grade,gradeController.getGradeByStudentAndCourse(studentId,courseId).get());
    }

    @Test
    public void modifyGradeTest() {
        gradeController.modifyGradeValue(grade.getId(),9.0);
        Assertions.assertEquals(9.0,grade.getGrade());
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    public void modifyGradeWrongTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->gradeController.modifyGradeValue(grade.getId(), 0.8));
        String expectedMessage = "grade must be between 1.0 and 10.0";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @Test
    public void createGradeTest() {
        gradeController.createGrade(studentId,courseId, 7.8);
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    public void createGradeWrong() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->gradeController.createGrade(studentId,courseId, 0.8));
        String expectedMessage = "grade must be between 1.0 and 10.0";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(gradeRepository, never()).save(any(Grade.class));

    }

    @Test
    public void getGradeDTOTest() {
        GradeDTO gradeDTO = new GradeDTO(studentId,grade.getGrade());
        Assertions.assertEquals(gradeController.getGrades(courseId,studentId).block(),gradeDTO);
    }

    @Test
    public void getGradesListTest() {
        GradeDTO gradeDTO = new GradeDTO(studentId,grade.getGrade());
        List<Grade> list = new ArrayList<>();
        list.add(grade);
        when(gradeRepository.findAllByCourse(course)).thenReturn(list);
        Assertions.assertEquals(gradeController.getGrades(courseId).blockFirst(),gradeDTO);
    }

}

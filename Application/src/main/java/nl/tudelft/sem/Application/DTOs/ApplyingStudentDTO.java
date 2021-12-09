package nl.tudelft.sem.Application.DTOs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;

@Data
public class ApplyingStudentDTO {
    private UUID studentId;
    private String name;
    private double grade;
    private Optional<List<String>> taExperience;
    private Optional<Integer> rating;

    /**
     * Creates a DTO representing an applying student.
     *
     * @param studentId Id of the student.
     * @param name Name of the student.
     * @param grade Grade a student got for the course they're applying for.
     * @param taExperience List of CourseCodes a student has TA'd for, if a student has TA'd.
     * @param rating Rating of student as TA, if a student has TA'd.
     */

    public ApplyingStudentDTO(UUID studentId, String name, double grade,
                              Optional<List<String>> taExperience, Optional<Integer> rating) {
        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
        this.taExperience = taExperience;
        this.rating = rating;
    }
}

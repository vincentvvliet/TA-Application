package DTOs;

import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
public class ApplyingStudentDTO {
    private UUID studentId;
    private double grade;
    private List<TAExperienceDTO> taExperience;
    private Optional<Integer> rating;

    /**
     * Creates a DTO representing an applying student.
     *
     * @param studentId Id of the student.
     * @param grade Grade a student got for the course they're applying for.
     * @param taExperience List of CourseCodes a student has TA'd for, if a student has TA'd.
     * @param rating Rating of student as TA, if a student has TA'd.
     */

    public
    ApplyingStudentDTO(UUID studentId, double grade, List<TAExperienceDTO> taExperience, Optional<Integer> rating) {
        this.studentId = studentId;
        this.grade = grade;
        this.taExperience = taExperience;
        this.rating = rating;
    }
}

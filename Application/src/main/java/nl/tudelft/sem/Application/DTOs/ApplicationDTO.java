package nl.tudelft.sem.Application.DTOs;

import java.util.UUID;
import lombok.Data;


@Data
public class ApplicationDTO {
    UUID id;
    UUID courseId;
    UUID studentId;

    public ApplicationDTO(UUID courseId, UUID studentId) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
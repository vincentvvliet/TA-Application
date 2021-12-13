package DTOs;

import lombok.Data;

import java.util.UUID;


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
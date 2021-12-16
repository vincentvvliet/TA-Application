package nl.tudelft.sem.User.services;

import java.util.UUID;

class ApplicationDTO {
    UUID id;
    UUID courseId;
    UUID studentId;

    public ApplicationDTO(UUID courseId, UUID studentId) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
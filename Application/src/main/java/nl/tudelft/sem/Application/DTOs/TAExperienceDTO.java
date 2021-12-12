package nl.tudelft.sem.Application.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class TAExperienceDTO {
    @Getter @Setter private String courseCode;
    @Getter @Setter private int rating;

    /**
     * Creates a DTO representing course, rating pair for a TA's past experience.
     * @param courseCode coursecode  Ta'd for.
     * @param rating Ta's rating for course.
     */
    public TAExperienceDTO(String courseCode, int rating) {
        this.courseCode = courseCode;
        this.rating = rating;
    }
}

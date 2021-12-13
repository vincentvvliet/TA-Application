package nl.tudelft.sem.DTO;


public class TAExperienceDTO {
    private String courseCode;
    private int rating;

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

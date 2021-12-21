package nl.tudelft.sem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

/**
 * DTO to represent average rating of a student.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    private UUID studentId;
    private Optional<Integer> rating;
}
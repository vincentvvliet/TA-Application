package nl.tudelft.sem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

/**
 * DTO to leave rating for a TA, id represents TA instance, not student ID.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRatingDTO {
    private UUID id;
    private Optional<Integer> rating;
}
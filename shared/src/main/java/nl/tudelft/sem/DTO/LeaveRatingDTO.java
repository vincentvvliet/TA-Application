package nl.tudelft.sem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRatingDTO {
    private UUID id;
    private UUID courseId;
    private Optional<Integer> rating;
}
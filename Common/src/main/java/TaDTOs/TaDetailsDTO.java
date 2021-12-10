package TaDTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * A DTO that contains details about a TA
 */
public class TaDetailsDTO {

    @Getter @Setter private UUID id;
    @Getter @Setter private UUID studentId;
    @Getter @Setter private UUID courseId;
    @Getter @Setter private int rating;

}

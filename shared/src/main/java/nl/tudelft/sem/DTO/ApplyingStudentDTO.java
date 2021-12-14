package nl.tudelft.sem.DTO;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyingStudentDTO {
    @Getter @Setter private UUID studentId;
    @Getter @Setter private double grade;
    @Getter @Setter private Optional<Integer> rating;
}

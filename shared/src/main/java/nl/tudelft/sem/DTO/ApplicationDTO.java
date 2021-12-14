package nl.tudelft.sem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {
    @Getter @Setter UUID courseId;
    @Getter @Setter UUID studentId;
}
package nl.tudelft.sem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDTO implements Comparable<RecommendationDTO> {
    private UUID studentId;
    private Optional<Integer> rating;
    private double grade;

    @Override
    public int compareTo(RecommendationDTO recommendationDTO) {
        if(this.getGrade() > recommendationDTO.getGrade()) return 1;
        if(this.getGrade() == recommendationDTO.getGrade()) {
           if(this.getRating().isPresent() && recommendationDTO.getRating().isPresent()) {
               return Integer.compare(this.getRating().get(), recommendationDTO.getRating().get());}
           if(this.getRating().isPresent()) return 1;
           if(recommendationDTO.getRating().isPresent()) return -1;
           return 1;
        }
        return -1;
    }
}

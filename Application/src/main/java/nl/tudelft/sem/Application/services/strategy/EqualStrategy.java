package nl.tudelft.sem.Application.services.strategy;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Tuple;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class EqualStrategy extends Strategy {

    public static String canonicalName = "Grade&Rating";

    /**Recommend method applies the sort on the list of applicants.
     * @param list of applicants.
     * @return the recommended list.
     */
    @Override
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list) {
        List<RecommendationDTO> recommendationList = list.stream()
            .filter(x -> x.getRating().isPresent())
            .sorted(Comparator.comparingDouble(x -> x.getRating().orElse(0) + x.getGrade()))
            .collect(Collectors.toList());
        Collections.reverse(recommendationList);
        return recommendationList;
    }
}

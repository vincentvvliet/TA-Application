package nl.tudelft.sem.Application.services.strategy;

import java.util.*;
import java.util.stream.*;

import lombok.NoArgsConstructor;
import nl.tudelft.sem.DTO.RecommendationDTO;

@NoArgsConstructor
public class IgnoreRatingStrategy extends Strategy {

    public static String canonicalName = "IgnoreRating";

    /**Recommend method applies the sort on the list of applicants.
     * @param list of applicants.
     * @return the recommended list.
     */
    @Override
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list) {
        List<RecommendationDTO> recommendationList = list.stream()
            .sorted(Comparator.comparingDouble(RecommendationDTO::getGrade))
            .collect(Collectors.toList());
        Collections.reverse(recommendationList);
        return recommendationList;
    }


}

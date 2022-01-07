package nl.tudelft.sem.Application.services.strategy;

import java.util.*;
import java.util.stream.*;
import nl.tudelft.sem.DTO.RecommendationDTO;


public class IgnoreRatingStrategy implements Strategy {

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

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

        Stream<RecommendationDTO> recommendationStream = list.stream()
            .sorted(Comparator.comparingDouble(RecommendationDTO::getGrade));
        List<RecommendationDTO> l = recommendationStream.collect(Collectors.toList());
        Collections.reverse(l);
        return l;
    }
}

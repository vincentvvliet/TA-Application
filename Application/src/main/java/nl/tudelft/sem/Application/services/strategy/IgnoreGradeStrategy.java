package nl.tudelft.sem.Application.services.strategy;

import java.util.Collections;
import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IgnoreGradeStrategy implements Strategy {

    /**Recommend method applies the sort on the list of applicants.
     * @param list of applicants.
     * @return the recommended list.
     */
    @Override
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list) {
        Stream<RecommendationDTO> recommendationStream = list.stream().filter(x -> x.getRating().isPresent())
            .sorted(Comparator.comparingInt(x -> x.getRating().orElse(0)));
        List<RecommendationDTO> l =  recommendationStream.collect(Collectors.toList());
        Collections.reverse(l);
        return l;
    }
}

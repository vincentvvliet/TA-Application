package nl.tudelft.sem.Application.services.strategy;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Tuple;
import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.Collections;
import java.util.List;


public class EqualStrategy implements Strategy {

    /**Recommend method applies the sort on the list of applicants.
     * @param list of applicants.
     * @return the recommended list.
     */
    @Override
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list) {
        Stream<RecommendationDTO> recommendationStream = list.stream().filter(x -> x.getRating().isPresent())
            .sorted(Comparator.comparingDouble(x -> x.getRating().orElse(0) + x.getGrade()));
        List<RecommendationDTO> l = recommendationStream.collect(Collectors.toList());
        Collections.reverse(l);
        return l;
    }
}

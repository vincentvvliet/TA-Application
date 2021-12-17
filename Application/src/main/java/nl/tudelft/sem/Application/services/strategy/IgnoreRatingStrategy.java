package nl.tudelft.sem.Application.services.strategy;

import java.util.*;
import java.util.stream.*;
import nl.tudelft.sem.DTO.RecommendationDTO;


public class IgnoreRatingStrategy implements Strategy {
    @Override
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list) {

        Stream<RecommendationDTO> recommendationStream = list.stream().sorted(Comparator.comparingDouble(RecommendationDTO::getGrade));
        return recommendationStream.collect(Collectors.toList());
    }
}

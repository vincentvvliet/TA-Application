package nl.tudelft.sem.Application.services.strategy;

import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IgnoreGradeStrategy implements Strategy {
    @Override
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list) {
        Stream<RecommendationDTO> recommendationStream = list.stream().filter(x -> x.getRating().isPresent()).sorted(Comparator.comparingInt(x -> x.getRating().orElse(0)));
        return recommendationStream.collect(Collectors.toList());
    }
}

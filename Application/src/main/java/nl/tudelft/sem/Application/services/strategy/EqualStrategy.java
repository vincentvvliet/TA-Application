package nl.tudelft.sem.Application.services.strategy;

import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.Collections;
import java.util.List;


public class EqualStrategy implements Strategy {
    @Override
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list) {
        Collections.sort(list);
        return list;
    }
}

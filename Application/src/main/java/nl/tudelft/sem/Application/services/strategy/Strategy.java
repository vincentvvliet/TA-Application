package nl.tudelft.sem.Application.services.strategy;

import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.List;

public interface Strategy {
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list);
}

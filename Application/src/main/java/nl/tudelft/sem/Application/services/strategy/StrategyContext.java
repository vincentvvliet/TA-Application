package nl.tudelft.sem.Application.services.strategy;

import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.List;

public class StrategyContext {
    private Strategy strategy;

    public void setRecommendation(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<RecommendationDTO> giveRecommendation(List<RecommendationDTO> list) {
        return strategy.recommend(list);
    }

}

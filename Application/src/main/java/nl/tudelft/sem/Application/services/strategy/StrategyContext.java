package nl.tudelft.sem.Application.services.strategy;

import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.List;

public class StrategyContext {
    private Strategy strategy;

    /** This method sets the strategy for the recommendation system.
     * @param strategy to use
     */
    public void setRecommendation(Strategy strategy) {
        this.strategy = strategy;
    }

    /** This method return the recommended list of applicants after the strategy is applied.
     * @param list of applicants to recommend.
     * @return the list of recommended applicants.
     */
    public List<RecommendationDTO> giveRecommendation(List<RecommendationDTO> list) {
        return strategy.recommend(list);
    }

}

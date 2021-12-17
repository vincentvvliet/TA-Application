package nl.tudelft.sem.Application.services.strategy;

import java.util.List;

public class StrategyContext {
    private Strategy strategy;

    public void setRecommendation(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<String> giveRecommendation() {
        return strategy.recommend();
    }

}

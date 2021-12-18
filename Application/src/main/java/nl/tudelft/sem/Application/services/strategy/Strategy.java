package nl.tudelft.sem.Application.services.strategy;

import nl.tudelft.sem.DTO.RecommendationDTO;

import java.util.List;

public interface Strategy {

    /**Recommend method applies the sort on the list of applicants.
     * @param list of applicants.
     * @return the recommended list.
     */
    public List<RecommendationDTO> recommend(List<RecommendationDTO> list);
}

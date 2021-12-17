package nl.tudelft.sem.Application.services.strategy;

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
        Collections.sort(list);
        return list;
    }
}

package nl.tudelft.sem.Application.services.strategy;

import nl.tudelft.sem.DTO.RecommendationDTO;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

public abstract class Strategy {

    /**Recommend method applies the sort on the list of applicants.
     *
     * @param list of applicants.
     * @return the recommended list.
     */
    public  abstract List<RecommendationDTO> recommend(List<RecommendationDTO> list);


    /**Returns an instance of a strategy given its canonical name.
     * How to implement your own strategy:
     * create class extending `Strategy` and give it a static string field named `canonicalName`
     * and satisfy the strategy interface.
     *
     * @param name of the strategy.
     * @return an instance of specified strategy.
     * @throws Exception if strategy does not exist or can't be found.
     */
    public static Strategy getStrategy(String name) throws Exception {

        //get all existing subclasses of strategy to be able to iterate over them
        Reflections reflections = new Reflections("nl.tudelft.sem.Application");
        Set<Class<? extends Strategy>> subTypes = reflections.getSubTypesOf(Strategy.class);

        for (var c: subTypes) {
            //compares given name to canonical name of subclass c
            if (c.getDeclaredField("canonicalName").get(null).equals(name)) {
                try {
                    //instantiate said subclass
                    return c.getConstructor().newInstance();
                } catch (Exception e) {
                    continue;
                }
            }
        }
        throw new Exception("strategy doesn't exist!");

    }



}

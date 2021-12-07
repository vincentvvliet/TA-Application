package nl.tudelft.sem.Application.entities;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.management.InvalidApplicationException;
import java.util.Optional;

public class IsGradeSufficient extends BaseValidator {
    private  final double minimum = 6;

    @Override
    public Boolean handle(Application application) throws InvalidApplicationException {
        Optional<Double> grade1 = application.getGrade();
        if(grade1.isEmpty()){
            throw new InvalidApplicationException("could not retrieve course grade with the given student and course IDs");
        }
        double grade = grade1.get();
        if(grade < minimum){
            throw new InvalidApplicationException("Grade was not sufficient");
        }
        return true;
    }
}

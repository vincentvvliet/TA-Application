package nl.tudelft.sem.Application.services.validator;

import nl.tudelft.sem.Application.entities.Application;


public interface Validator {
    void setNext(Validator h);

    void setLast(Validator h);

    boolean checkNext(Application application) throws Exception;
    Boolean handle(Application application) throws Exception;
}

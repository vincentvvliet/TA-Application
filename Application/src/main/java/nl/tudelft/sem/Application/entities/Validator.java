package nl.tudelft.sem.Application.entities;

import javax.management.InvalidApplicationException;

public interface Validator {
    void setNext(Validator h);
    void setLast(Validator h);
    Boolean handle(Application application) throws InvalidApplicationException;
}

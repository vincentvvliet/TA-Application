package nl.tudelft.sem.Application.exceptions;

public class EmptyResourceException extends Exception {
    public EmptyResourceException(String errormsg) {
        super(errormsg);
    }
}

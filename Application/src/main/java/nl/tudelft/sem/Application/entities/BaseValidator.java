package nl.tudelft.sem.Application.entities;

import javax.management.InvalidApplicationException;

public abstract class BaseValidator implements Validator{
    private Validator next;

    public void setNext(Validator next){
        this.next = next;
    }

    public void setLast(Validator next){
        if(next == null){
            setNext(next);
        }else {
            setLast(next);
        }
    }

    protected boolean checkNext(Application application) throws InvalidApplicationException {
        if(next == null){
            return true;
        }
        return handle(application);
    }
}

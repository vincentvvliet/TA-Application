package nl.tudelft.sem.Application.services.validator;

import nl.tudelft.sem.Application.entities.Application;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;

@Service
public abstract class BaseValidator implements Validator{
    private Validator next;

    public void setNext(Validator next){
        this.next = next;
    }

    /** at service at the and of the chain of services
     *
     * @param next the validator that needs to be added
     */
    public void setLast(Validator next){
        if(this.next == null){
            setNext(next);
        }else {
            setLast(next);
        }
    }

    /** Handles to the next service in the chain and returns true if at the end.
     * @return boolean
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean checkNext(Application application) throws InvalidApplicationException {
        if(next == null){
            return true;
        }
        return handle(application);
    }
}

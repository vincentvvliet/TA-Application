package nl.tudelft.sem.Application.entities;

import javax.management.InvalidApplicationException;

public class IsCourseOpen extends BaseValidator{

    @Override
    public Boolean handle(Application application) throws InvalidApplicationException {

        return super.checkNext(application);
    }
}

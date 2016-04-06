package com.view.component.db.editors.validators;

public class NullValidator extends AbstractValidator {

    public NullValidator(String message) {
        super(message);
    }

    @Override
    public boolean verify(Object object) {
        if (object instanceof String)
            return !((String) object).isEmpty();
        else
            return object != null;
    }
}

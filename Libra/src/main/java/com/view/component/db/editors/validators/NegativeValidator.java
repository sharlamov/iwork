package com.view.component.db.editors.validators;

public class NegativeValidator extends AbstractValidator {


    public NegativeValidator(String message) {
        super(message);
    }

    @Override
    public boolean verify(Object object) {
        try {
            return object == null || Double.valueOf(object.toString()) < 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}

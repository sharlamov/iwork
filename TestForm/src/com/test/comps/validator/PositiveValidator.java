package com.test.comps.validator;

public class PositiveValidator extends AbstractValidator {

    public PositiveValidator(String message) {
        super(message);
    }

    @Override
    public boolean verify(Object object) {
        try {
            return object == null || Double.valueOf(object.toString()) > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }
}

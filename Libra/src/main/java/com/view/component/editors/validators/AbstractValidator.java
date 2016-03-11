package com.view.component.editors.validators;

abstract public class AbstractValidator {

    private final String message;

    public AbstractValidator(String message) {
        this.message = message;
    }

    abstract public boolean verify(Object object);

    public String getErrorMessage() {
        return message;
    }
}

package com.view.component.editors;

import com.view.component.editors.validators.AbstractValidator;

import java.awt.event.FocusListener;

public interface IEdit extends FocusListener {

    void addValidator(AbstractValidator validator);

    boolean verify();

    void showError(String message);

    void addChangeEditListener(ChangeEditListener listener);

    void fireChangeEditEvent();

    void setChangeable(boolean isChangeable);

    Object getValue();

    void setValue(Object value);

    String getName();

    boolean isEmpty();
}

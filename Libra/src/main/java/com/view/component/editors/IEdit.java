package com.view.component.editors;

public interface IEdit {

    void addChangeEditListener(ChangeEditListener listener);

    void fireChangeEditEvent();

    void setChangeable(boolean isChangeable);

    Object getValue();

    void setValue(Object value);

    String getName();

    boolean isEmpty();
}

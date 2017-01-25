package com.test.comps.enums;

public enum CheckType {
    NEGATIVE, NULLABLE, POSITIVE;

    public boolean verify(Object object) {
        if (object == null)
            return false;

        String text = object.toString();

        switch (this) {
            case NEGATIVE:
                return Double.valueOf(text) < 0;
            case POSITIVE:
                return Double.valueOf(text) > 0;
            case NULLABLE:
                return !text.isEmpty();
        }
        return false;
    }
}

package com.enums;

public enum ArmType {
    IN(1), OUT(2);

    private int value;

    ArmType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

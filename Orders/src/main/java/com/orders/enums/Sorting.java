package com.orders.enums;

public enum Sorting {
    UP("По возрастанию"), DOWN("По возрастанию"), NONE("Без сортировка");

    private String text;

    Sorting(String s) {
        text = s;
    }
}

package com.enums;

import java.util.Locale;

public enum LangType {
    RU("Рус", new Locale("ru")), RO("Ro", new Locale("ro"));

    private final Locale loc;
    String name;

    LangType(String name, Locale loc) {
        this.name = name;
        this.loc = loc;
    }

    public static LangType next(LangType lang) {
        LangType[] enums = LangType.values();
        for (int i = 0; i < enums.length && lang != null; i++) {
            if (enums[i].equals(lang))
                return enums[(i + 1) % enums.length];
        }
        return enums[0];
    }

    public static LangType find(String name) {
        for (LangType anEnum : LangType.values()) {
            if (anEnum.name.equals(name)) {
                return anEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public Locale getLoc() {
        return loc;
    }
}

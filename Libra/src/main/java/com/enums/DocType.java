package com.enums;

import com.model.Act;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DocType {
    IN(1, new ArrayList<Act>()),
    OUT(2, Arrays.asList(new Act("out.act1", SearchType.ACTOUT0.getSql()), new Act("out.act2", SearchType.ACTOUT1.getSql())));

    private int value;
    private List<Act> acts;

    DocType(int value, List<Act> acts) {
        this.value = value;
        this.acts = acts;
    }

    public int getValue() {
        return value;
    }

    public List<Act> getActs() {
        return acts;
    }
}

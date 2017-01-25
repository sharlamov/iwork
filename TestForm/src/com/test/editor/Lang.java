package com.test.editor;

import com.test.comps.interfaces.ITranslator;

/**
 * Created by sharlamov on 16.01.2017.
 */
public class Lang implements ITranslator {
    @Override
    public String lng(String key) {
        return key + "_1";
    }
}

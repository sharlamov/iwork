package com.test;

import java.awt.*;

/**
 * Created by sharlamov on 14.11.2016.
 */
public class ViewUtil {

    public static <T> T getCompByName(String cName, Container cont) {
        for (Component c : cont.getComponents()) {
            if (cName.equals(c.getName())) {
                return (T) c;
            } else {
                getCompByName(cName, (Container) c);

            }
        }
        return null;
    }
}

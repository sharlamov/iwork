package com.util;

import java.awt.*;
import java.util.ArrayList;

public class CustomFocusTraversalPolicy extends FocusTraversalPolicy {
    java.util.List<Component> order;

    public CustomFocusTraversalPolicy() {
        this.order = new ArrayList<Component>();
    }

    public void addComponent(Component aComponent) {
        if (aComponent != null)
            order.add(aComponent);
    }

    public Component getComponentAfter(Container focusCycleRoot,
                                       Component aComponent) {
        if (!order.isEmpty()) {
            int idx = order.indexOf(aComponent);
            return order.get(idx >= order.size() - 1 ? 0 : idx + 1);
        } else
            return null;
    }

    public Component getComponentBefore(Container focusCycleRoot,
                                        Component aComponent) {
        if (!order.isEmpty()) {
            int idx = order.indexOf(aComponent) - 1;
            return order.get(idx < 0 ? order.size() - 1 : idx);
        } else
            return null;
    }

    public Component getDefaultComponent(Container focusCycleRoot) {
        return getFirstComponent(focusCycleRoot);
    }

    public Component getLastComponent(Container focusCycleRoot) {
        return order.isEmpty() ? null : order.get(order.size() - 1);
    }

    public Component getFirstComponent(Container focusCycleRoot) {
        return order.isEmpty() ? null : order.get(0);
    }
}

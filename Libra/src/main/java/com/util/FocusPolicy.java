package com.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FocusPolicy extends FocusTraversalPolicy {
    private List<Component> order;

    public FocusPolicy() {
        this.order = new ArrayList<>();
    }

    public void add(Component aComponent) {
        if (aComponent != null)
            order.add(aComponent);
    }

    public void remove(Component aComponent) {
        order.remove(aComponent);
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

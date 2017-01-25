package com.test.comps.interfaces;

import com.dao.model.DataSet;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusListener;

public interface IComp extends FocusListener {

    Color clr = Color.decode("#FFFFCC");

    Object getValue(String name);

    void setValue(String name, Object o);

    void setChangeable(boolean changeable);

    void refresh();

    default boolean isEmpty() {
        Container comp = (Container) this;
        return getValue(comp.getName()) == null;
    }

    default void showError(String message) {
        JComponent comp = (JComponent) this;
        comp.setBorder(BorderFactory.createLineBorder(Color.red));
        BalloonTipStyle edgedLook = new EdgedBalloonStyle(clr, Color.red);
        BalloonTip myBalloonTip = new BalloonTip(comp, message, edgedLook, false);
        TimingUtils.showTimedBalloon(myBalloonTip, 3000);
    }

    default ITranslator getTranslator() {
        IComp cmp = source();
        return cmp != null ? cmp.getTranslator() : null;
    }

    default IDataSource getDataSource() {
        IComp cmp = source();
        return cmp != null ? cmp.getDataSource() : null;
    }

    default DataSet getDataSet() {
        IComp cmp = source();
        return cmp != null ? cmp.getDataSet() : null;
    }

    default IComp source() {
        Component cmp = (Component) this;
        while (cmp != null) {
            cmp = cmp.getParent();
            if (cmp instanceof IComp) break;
        }
        return (IComp) cmp;
    }
}

package com.test.comps;


import com.dao.model.DataSet;
import com.test.comps.interfaces.IComp;
import com.test.comps.interfaces.IDataSource;
import com.test.comps.interfaces.ISettings;
import com.test.comps.interfaces.ITranslator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;

public class TPanel extends JPanel implements IComp, ISettings{

    private IDataSource service;
    private ITranslator translator;
    private String sql;
    private DataSet set;

    public TPanel() {
        super(null);
    }

    public TPanel(TProp prop, IDataSource service, ITranslator translator) throws Exception {
        this();
        this.service = service;
        this.translator = translator;
        load(prop);
    }

    @Override
    public void prepareProperties(TProp prop) {
        prop.put("sql", sql);
        prop.put("changeable", isEnabled());
    }

    @Override
    public void prepareComponent(TProp prop) {
        sql = prop.fetch("sql");
        setChangeable(prop.fetch("changeable"));
    }

    @Override
    public void setChangeable(boolean changeable) {
        setEnabled(changeable);
    }

    @Override
    public void refresh() {
        try {
            set = service.exec(sql, source().getDataSet());
        } catch (Exception e) {e.printStackTrace();}

        if(set == null || set.isEmpty()){
            source().refresh();
        }else {
            for (Component component : getComponents()) {
                if(component instanceof IComp)
                    ((IComp) component).getValue(component.getName());
            }
        }
    }

    @Override
    public Object getValue(String name) {
        return set == null || set.isEmpty() ? source().getValue(name) : set.getObject(name);
    }

    @Override
    public void setValue(String name, Object o) {
        if (set == null || set.isEmpty())
            source().setValue(name, o);
        else
            set.setObject(name, o);
    }

    @Override
    public DataSet getDataSet() {
        return set;
    }

    @Override
    public ITranslator getTranslator() {
        return translator;
    }

    @Override
    public IDataSource getDataSource() {
        return service;
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}

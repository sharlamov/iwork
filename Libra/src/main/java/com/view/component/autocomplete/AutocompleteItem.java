package com.view.component.autocomplete;

import com.enums.SearchType;
import com.model.CustomItem;
import com.service.LibraService;
import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

public class AutocompleteItem extends JComboBox<CustomItem> implements KeyListener, ItemListener {

    private final LibraService service;
    private final SearchType type;
    private JTextField field;
    private boolean shouldHide;

    public AutocompleteItem(LibraService service, SearchType type) {
        super();
        this.service = service;
        this.type = type;
        addItemListener(this);
        setEditable(true);
        setSelectedIndex(-1);

        field = (JTextField) getEditor().getEditorComponent();
        field.addKeyListener(this);
    }


    private void setSuggestionModel(ComboBoxModel<CustomItem> mdl, String str) {
        setModel(mdl);
        setSelectedIndex(-1);
        field.setText(str);
    }

    private ComboBoxModel<CustomItem> getSuggestedModel(String text) {
        Vector<CustomItem> founds = null;
        try {
            founds = new Vector<CustomItem>(service.searchItems(text, type));
        } catch (Exception e) {
            e.printStackTrace();
            Libra.eMsg(e.getMessage());
        }
        return new DefaultComboBoxModel<CustomItem>(founds);
    }

    public void keyTyped(KeyEvent e) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                String text = field.getText();
                if (text.isEmpty() || shouldHide) {
                    hidePopup();
                } else {
                    ComboBoxModel<CustomItem> m = getSuggestedModel(text);
                    if (m.getSize() == 0) {
                        hidePopup();
                    } else {
                        setSuggestionModel(m, text);
                        showPopup();
                    }
                }
            }
        });
    }

    public void keyPressed(KeyEvent e) {
        shouldHide = false;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                shouldHide = true;
                break;
            case KeyEvent.VK_ENTER:
                shouldHide = true;
                break;
            case KeyEvent.VK_ESCAPE:
                shouldHide = true;
                break;
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED){
            if(!(getSelectedItem() instanceof CustomItem)) {
                setSelectedItem(null);
            }
        }
    }

    public JTextField getTextArea() {
        return field;
    }
}

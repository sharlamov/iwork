package com.view.component.weightboard;

import com.driver.ScaleEventListener;
import com.driver.ScalesDriver;
import com.util.Libra;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class WeightBoard extends JPanel implements ScaleEventListener {

    public JButton btnAdd;
    private JLabel score = new JLabel();
    private Color stableColor = Color.orange;
    private Color unstableColor = Color.decode("#FF9999");
    private boolean isOnline;
    private boolean isBlock;

    public WeightBoard(final ScalesDriver driver, boolean isOnline) {
        this.isOnline = isOnline;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JLabel titleLabel = new JLabel();
        titleLabel.setBackground(Color.lightGray);
        titleLabel.setOpaque(true);
        titleLabel.setText(driver.toString());
        titleLabel.setFont(new Font("Courier", Font.BOLD, 12));
        add(titleLabel, BorderLayout.NORTH);

        score.setBackground(stableColor);
        score.setOpaque(true);
        score.setHorizontalAlignment(SwingConstants.RIGHT);
        score.setFont(new Font("Courier", Font.BOLD, 45));
        add(score, BorderLayout.CENTER);

        driver.addEventListener(this);

        if (isOnline) {
            Dimension small = new Dimension(200, 70);
            setPreferredSize(small);
        } else {
            Dimension big = new Dimension(200, 90);
            setPreferredSize(big);
            btnAdd = new JButton(Libra.translate("scale.take"));
            add(btnAdd, BorderLayout.SOUTH);
        }
    }

    public void setBlock(boolean isBlock) {
        this.isBlock = isBlock;
        btnAdd.setEnabled(!isBlock);
    }

    public Integer getWeight() {
        String value = score.getText();
        return value.isEmpty() ? null : Integer.valueOf(value);
    }

    public void setWeight(Integer weight) {
        score.setText(weight == null ? "" : weight.toString());
    }

    public void scaleExecuted(Integer weight, boolean isStable) {
        setWeight(weight);
        if (!isOnline) {
            score.setBackground(isStable ? stableColor : unstableColor);
            if (!isBlock) {
                btnAdd.setEnabled(isStable);
            }
        }
    }
}

package com.bin;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.util.Fonts;
import com.util.Libra;
import com.util.Pictures;
import com.view.component.widget.Display;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;

import static com.util.Libra.eMsg;

public class HistoryPanel extends JPanel {


    private Dimension dimension = new Dimension(Integer.MAX_VALUE, 90);
    private Dimension xSize = new Dimension(200, 125);
    private Border brd = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    public HistoryPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Dimension size = new Dimension(200, 300);
        setMaximumSize(size);
        setPreferredSize(size);
    }

    public void refreshData(BigDecimal id) {
        removeAll();
        try {
            DataSet dataSet = Libra.libraService.executeQuery(Libra.sql("HISTORY"), DataSet.init("id", id));
            for (Object[] objects : dataSet) {
                addInfo((BigDecimal) objects[0], (Date) objects[1], (CustomItem) objects[2], (BigDecimal) objects[3]);
                makeFoto(objects[4]);
                makeFoto(objects[5]);
            }
        } catch (Exception ex) {
            eMsg(ex);
        }
        revalidate();
        repaint();
    }

    public void makeFoto(Object foto) {
        try {
            if (foto != null) {
                Blob blob = (Blob) foto;
                final Display display = new Display();
                display.setMaximumSize(xSize);
                display.setPreferredSize(xSize);
                display.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            SwingUtilities.invokeLater(() -> openImage(display.getPicture()));
                        }
                    }
                });

                BufferedImage img = ImageIO.read(blob.getBinaryStream());
                display.frameObtained(img);
                add(display);
            }
        } catch (Exception e) {
            Libra.eMsg(e, true);
        }

    }

    public void addInfo(BigDecimal direction, Date date, CustomItem user, BigDecimal weight) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(dimension);
        p.setPreferredSize(dimension);
        p.setBorder(brd);
        JLabel l = new JLabel(direction.intValue() == 1 ? Pictures.loadedIcon : Pictures.unloadedIcon);
        p.add(l, BorderLayout.WEST);
        JLabel userLabel = new JLabel(user.toString(), SwingConstants.CENTER);
        p.add(userLabel, BorderLayout.NORTH);
        JLabel dateLabel = new JLabel(Libra.dateTimeFormat.format(date), SwingConstants.CENTER);
        p.add(dateLabel, BorderLayout.CENTER);
        JLabel weightLabel = new JLabel(String.valueOf(weight), SwingConstants.CENTER);
        p.add(weightLabel, BorderLayout.EAST);
        weightLabel.setForeground(Color.green);
        weightLabel.setFont(Fonts.bold24);
        add(p);
    }


    protected void openImage(BufferedImage img) {
        JDialog dialog = new JDialog((Frame) null, Libra.TITLE);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        final ImageIcon backgroundImage = new ImageIcon(img);
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width = Math.max(backgroundImage.getIconWidth(), size.width);
                size.height = Math.max(backgroundImage.getIconHeight(), size.height);

                return size;
            }
        };
        dialog.add(mainPanel);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

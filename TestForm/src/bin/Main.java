package bin;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame{

    public JPanel mainPane;
    public JPanel compsPane;
    public JPanel propsPane;

    public static void main(String[] args) {
        new Main();
    }


    public Main()  {
        super("Form designer");

        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initMainPanel();
        initCompsPanel();
        initPropsPanel();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setVisible(true);
    }

    public void initMainPanel(){
        mainPane = new JPanel();
        mainPane.setBackground(Color.darkGray);
        mainPane.setLayout(null);
        add(mainPane, BorderLayout.CENTER);
    }

    public void initCompsPanel(){
        compsPane = new JPanel();
        compsPane.setPreferredSize(new Dimension(100, getHeight()));

        JLabel l1 = new JLabel("Panel");
        l1.setName(JPanel.class.getName());
        compsPane.add(l1);
        l1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addComps(new JPanel());
                }
            }
        });

        add(compsPane, BorderLayout.WEST);
    }

    public void initPropsPanel(){
        propsPane = new JPanel();
        propsPane.setPreferredSize(new Dimension(200, getHeight()));
        add(propsPane, BorderLayout.EAST);
    }



    public void addComps(JComponent comp){
        mainPane.add(comp);//Adds this component to main container
        comp.setBorder(new LineBorder(Color.BLACK, 1));
        comp.setSize(150, 30);
        comp.setLocation(0, 0);
        mainPane.repaint();
    }

    public void getAllProps(JComponent comp){
        mainPane.add(comp);//Adds this component to main container
        comp.setBorder(new LineBorder(Color.BLACK, 1));
        comp.setSize(150, 30);
        comp.setLocation(0, 0);
        mainPane.repaint();
    }

    public void setAllProps(JComponent comp){
        mainPane.add(comp);//Adds this component to main container
        comp.setBorder(new LineBorder(Color.BLACK, 1));
        comp.setSize(150, 30);
        comp.setLocation(0, 0);
        mainPane.repaint();
    }


}

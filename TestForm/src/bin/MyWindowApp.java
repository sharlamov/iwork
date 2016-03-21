package bin;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyWindowApp extends JFrame { //Наследуя от JFrame мы получаем всю функциональность окна

    private JPanel toolPanel;
    private JPanel customPanel;
    private JButton btnSave = new JButton("Save");

    public MyWindowApp() {
        super("Panel admin"); //Заголовок окна
        setSize(800, 500); //Если не выставить
        //setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //это нужно для того чтобы при
        setLayout(new BorderLayout());

        toolPanel = new JPanel(new BorderLayout());
        toolPanel.setPreferredSize(new Dimension(200, 100));
        toolPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        DragListener drag = new DragListener();
        JList<Class> list = new JList<>(new Class[]{JPanel.class, JLabel.class, JTextField.class});
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setDragEnabled(true);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());

                    try {
                        Object obj = ((Class) list.getModel().getElementAt(index)).newInstance();
                        JComponent comp = (JComponent) obj;
                        comp.setBounds(10, 10, 100, 30);
                        comp.addMouseListener(drag);
                        comp.addMouseMotionListener(drag);
                        customPanel.add(comp);
                        customPanel.revalidate();
                        customPanel.repaint();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        toolPanel.add(list, BorderLayout.CENTER);
        add(toolPanel, BorderLayout.EAST);

        JToolBar bar = new JToolBar();
        bar.add(btnSave);
        btnSave.addActionListener(e -> System.out.println(customPanel.getComponentCount()));
        add(bar, BorderLayout.NORTH);

        customPanel = new JPanel(null);
        customPanel.setBackground(Color.gray);
        customPanel.setDoubleBuffered(true);
        add(customPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) { //эта функция может быть и в другом классе
        MyWindowApp app = new MyWindowApp(); //Создаем экземпляр нашего приложения
        app.setVisible(true); //С этого момента приложение запущено!
    }
}


class DragListener extends MouseInputAdapter {
    Point location;
    MouseEvent pressed;

    public void mousePressed(MouseEvent me) {
        pressed = me;
    }

    public void mouseDragged(MouseEvent me) {
        Component component = me.getComponent();
        location = component.getLocation(location);
        int x = location.x - pressed.getX() + me.getX();
        int y = location.y - pressed.getY() + me.getY();
        component.setLocation(x, y);
    }
}
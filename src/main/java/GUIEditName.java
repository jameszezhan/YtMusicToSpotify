import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIEditName  {
    JFrame frame = null;
    JPanel panel = null;

    public GUIEditName() {
        frame = new JFrame();
        panel = new JPanel(new BorderLayout());
        JTextField textfield = new JTextField("new text field");
        textfield.setActionCommand("asdf");
        textfield.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        System.out.println("as");
                    }
                }
        );
        panel.add(textfield);
        frame.add(panel);
        frame.setSize(500,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);
    }
}

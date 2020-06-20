import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class GUI implements ActionListener, ItemListener {
    JFrame frame;
    JPanel panel;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
    }

    public GUI() throws HeadlessException {
        frame = new JFrame();
        panel = new JPanel();
        frame.setSize(500,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);

        makeButton("Fetch playlist from YouTube");
    }

    private JButton makeButton(String caption) {
        JButton button = new JButton(caption);
        button.setBounds(10,20,400,30);
        button.setActionCommand(caption);
        button.addActionListener(this);
        panel.add(button);
        return button;
    }

    private void makeList(){
        ArrayList<String> dummy = new ArrayList<String>();
        dummy.add("I bet my life");
        dummy.add("adele");

        for(int i = 0; i< dummy.size(); i++){
            JCheckBox checkbox = new JCheckBox(dummy.get(i));
            checkbox.setBounds(10, 60, 400, 30);
            checkbox.setSelected(true);
            checkbox.addItemListener(this);
            panel.add(checkbox);
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if ("Fetch playlist from YouTube" == actionEvent.getActionCommand()){
            makeList();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        System.out.println(itemEvent.getID());
    }
}

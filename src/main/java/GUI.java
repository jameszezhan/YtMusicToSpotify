import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener, ItemListener {
    private final JPanel panel;
//    private final ScrollPane sPanel;

    public GUI() throws HeadlessException {
        super("GUI");
        panel = new JPanel();
        add(panel);

//        sPanel = new ScrollPane();
//        add(sPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        makeButton("Fetch playlist from YouTube");
        pack();
        setVisible(true);
    }

    private JButton makeButton(String caption) {
        JButton button = new JButton(caption);
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
            checkbox.setSelected(true);
            checkbox.addItemListener(this);
            panel.add(checkbox);
        }
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
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

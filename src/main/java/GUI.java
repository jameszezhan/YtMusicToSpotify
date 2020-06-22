import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;

public class GUI implements ActionListener, ItemListener {
    JFrame frame;
    JPanel panel, panelBtn, panelLeft, panelRight, panelBottom;
    YtApiHandler ytApiHandler;
    SpotifyApiHandler spotifyApiHandler;
    SpotifyApiHandlerForUser spotifyApiHandlerForUser;
    ArrayList<BaseTrack> ytTracks = null;
    ArrayList<BaseTrack> spTracks = null;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
    }

    public GUI() throws HeadlessException {
        ytApiHandler = new YtApiHandler();
        spotifyApiHandler = new SpotifyApiHandler();
        spotifyApiHandlerForUser = new SpotifyApiHandlerForUser();

        frame = new JFrame();
        panel = new JPanel(new BorderLayout());
        panelBtn = new JPanel();
        panelBtn.setLayout(new GridLayout());
        panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        panelRight = new JPanel();
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        panelBottom = new JPanel();

        panel.add(panelBtn, BorderLayout.NORTH);
        panel.add(panelLeft, BorderLayout.WEST);
        panel.add(panelRight, BorderLayout.EAST);
        panel.add(panelBottom, BorderLayout.SOUTH);

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
        panelBtn.add(button);
        return button;
    }

    private JTextField makeTextField(String content){
        JTextField textfield = new JTextField(content);
        panelLeft.add(textfield);
        return textfield;
    }

    private void makeList(ArrayList<BaseTrack> tracks, JPanel parentPanel){
        if (tracks.size() == 0){
            makeTextField("No track found");
        }else{
            for(int i = 0; i < tracks.size(); i++){
                BaseTrack track = tracks.get(i);
                String trackTitle = track.trackName;
                JCheckBox checkbox = new JCheckBox(trackTitle);
                checkbox.setActionCommand(track.trackId);
                checkbox.setBounds(10, 60, 400, 30);
                checkbox.setSelected(true);
                checkbox.addItemListener(this);
                parentPanel.add(checkbox);
            }
        }
        panelLeft.updateUI();
    }

    private void attachTo(Component child, JPanel parent){
        parent.add(child);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if ("Fetch playlist from YouTube" == actionEvent.getActionCommand()){
//            ArrayList<BaseTrack> ytTracks = ytApiHandler.fetchLikedTracks();
            try {
                ytTracks = ytApiHandler.processReturnJson();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            makeList(ytTracks, panelLeft);
            makeButton("YTProceed");
        } else if ("YTProceed" == actionEvent.getActionCommand()){
            spotifyApiHandler.getAndSetAccessToken();
            spTracks = spotifyApiHandler.searchTracks(ytTracks);
            makeList(spTracks, panelRight);
            makeButton("SPProceed");
        } else if ("SPProceed" == actionEvent.getActionCommand()){
            spotifyApiHandlerForUser.authorizationAndGetAccessCode();
            String[] spUrisArr = new String[spTracks.size()];
            spUrisArr = spTracks.toArray(spUrisArr);
            String playlistId = spotifyApiHandlerForUser.createPlaylist("MIGRATION");
            spotifyApiHandlerForUser.addTrackToPlaylist(playlistId , spUrisArr);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        JCheckBox cb = (JCheckBox) itemEvent.getItem();
        System.out.println(cb.getActionCommand());
        System.out.println(cb.getText());
    }
}

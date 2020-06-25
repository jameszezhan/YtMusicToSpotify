import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

public class GUI implements ActionListener, ItemListener {
    JFrame frame;
    JPanel panel, panelBtn, panelLeft, panelRight, panelBottom;
    YtApiHandler ytApiHandler;
    SpotifyApiHandler spotifyApiHandler;
    SpotifyApiHandlerForUser spotifyApiHandlerForUser;
    HashMap<String, BaseTrack> ytTracks = null;
    HashMap<String, BaseTrack> ytPlaylists = null;
    HashMap<String, BaseTrack> spTracks = null;
    JScrollPane scrollPanelLeft, scrollPanelRight;
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

        /** button panel */
        panelBtn = new JPanel();
        panelBtn.setName("Button Panel");
        panelBtn.setLayout(new GridLayout());
        panel.add(panelBtn, BorderLayout.NORTH);

        /** left panel*/
        panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        panelLeft.setName("Left Panel");
        scrollPanelLeft = new JScrollPane(panelLeft);
        scrollPanelLeft.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPanelLeft, BorderLayout.WEST);

        /** right panel*/
        panelRight = new JPanel();
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        panelRight.setName("Right Panel");
        scrollPanelRight = new JScrollPane(panelRight);
        scrollPanelRight.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPanelRight, BorderLayout.EAST);

        /** bottom panel*/
        panelBottom = new JPanel();
        panel.add(panelBottom, BorderLayout.SOUTH);

        frame.setSize(500,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);

        makeButton("Fetch playlist from YouTube", "btn_yt_start");
    }

    private JButton makeButton(String caption, String btnActionCommand) {
        JButton button = new JButton(caption);
        button.setActionCommand(btnActionCommand);
        button.setBounds(10,20,400,30);
        button.addActionListener(this);
        panelBtn.add(button);
        return button;
    }

    private JTextField makeTextField(String content){
        JTextField textfield = new JTextField(content);
        panelLeft.add(textfield);
        return textfield;
    }

    private void makeList(HashMap<String, BaseTrack> tracks, JPanel parentPanel){
        if (tracks.size() == 0){
            makeTextField("No track found");
        }else{
            for(Map.Entry track : tracks.entrySet()){
                String trackKey = (String) track.getKey();
                BaseTrack trackVal = (BaseTrack) track.getValue();
                String trackTitle = trackVal.trackName;
                JCheckBox checkbox = new JCheckBox(trackTitle);
                checkbox.setActionCommand(trackVal.trackId);
                checkbox.setBounds(10, 60, 400, 30);
                checkbox.setSelected(true);
                checkbox.addItemListener(this);
                parentPanel.add(checkbox);
            }
        }
        parentPanel.updateUI();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        switch (actionCommand){
            case "btn_yt_start":
                ytPlaylists = ytApiHandler.fetchAllPlaylists();
                makeList(ytPlaylists, panelLeft);
                makeButton("YTProceed", "btn_yt_one_fetch");
                break;
            case "btn_yt_one_fetch":
                ytTracks = ytApiHandler.fetchLikedTracks();
//                try {
//                    ytTracks = ytApiHandler.processReturnJson();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                makeList(ytTracks, panelLeft);
                makeButton("YTProceed", "btn_yt_one_fetch");
                break;
            case "btn_yt_all_fetch":
                break;
            case "btn_sp_search":
                spotifyApiHandler.getAndSetAccessToken();
                spTracks = spotifyApiHandler.searchTracks(ytTracks);
                makeList(spTracks, panelRight);
                makeButton("SPProceed", "btn_sp_migrate");
                break;
            case "btn_sp_migrate":
                spotifyApiHandlerForUser.authorizationAndGetAccessCode();
                String playlistId = spotifyApiHandlerForUser.createPlaylist("MIGRATION");
                spotifyApiHandlerForUser.addTrackToPlaylist(playlistId , spTracks);
                break;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        JCheckBox cb = (JCheckBox) itemEvent.getItem();
        String trackId = cb.getActionCommand();
        Container parentContainer = cb.getParent();
        String parentContainerName = parentContainer.getName();
        switch (parentContainerName){
            case "Left Panel":
                if(cb.isSelected()){
                    ytTracks.get(trackId).trackActionStatus = true;
                }else{
                    ytTracks.get(trackId).trackActionStatus = false;
                }
                break;
            case "Right Panel":
                if(cb.isSelected()){
                    spTracks.get(trackId).trackActionStatus = true;
                }else{
                    spTracks.get(trackId).trackActionStatus = false;
                }
                break;
        }
    }
}

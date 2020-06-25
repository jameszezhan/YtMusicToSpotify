import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

public class GUI implements ActionListener, ItemListener {
    JFrame frame;
    JPanel panel, panelBtn, panelLeft, panelRight, panelBottom, panelCenter;
    YtApiHandler ytApiHandler;
    SpotifyApiHandler spotifyApiHandler;
    SpotifyApiHandlerForUser spotifyApiHandlerForUser;
    HashMap<String, BaseTrack> ytTracks = new HashMap<String, BaseTrack>();
    HashMap<String, BaseTrack> ytPlaylists = new HashMap<String, BaseTrack>();
    HashMap<String, BaseTrack> spTracks = new HashMap<String, BaseTrack>();
    JScrollPane scrollPanelLeft, scrollPanelRight, scrollPanelCenter;
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

        /** center panel*/
        panelCenter = new JPanel();
        panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.Y_AXIS));
        panelCenter.setName("Center Panel");
        scrollPanelCenter = new JScrollPane(panelCenter);
        scrollPanelCenter.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPanelCenter, BorderLayout.CENTER);

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

    private void makeList(HashMap<String, BaseTrack> tracks, JPanel parentPanel, String actionPrefix){
        if (tracks.size() == 0){
            makeTextField("No track found");
        }else{
            for(Map.Entry track : tracks.entrySet()){
                String trackKey = (String) track.getKey();
                BaseTrack trackVal = (BaseTrack) track.getValue();
                String trackTitle = trackVal.trackName;
                JCheckBox checkbox = new JCheckBox(trackTitle);
                /**
                 * yt_track | track_id
                 * yt_list | list_id
                 * sp_track | track_id
                 */
                checkbox.setActionCommand(actionPrefix + " | " +trackVal.trackId);
//                checkbox.setActionCommand(trackVal.trackId);
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
                makeList(ytPlaylists, panelLeft, "yt_list");
                makeButton("YTProceed", "btn_yt_one_fetch");
                break;
            case "btn_yt_one_fetch":
                for(Map.Entry playlist : ytPlaylists.entrySet()){
                    String playlistId = (String) playlist.getKey();
                    BaseTrack playlistItem = (BaseTrack) playlist.getValue();
                    if(playlistItem.trackActionStatus.equals(false)){
                        continue;
                    }
                    ytTracks.putAll(ytApiHandler.fetchTracksFromPlaylist(playlistId));
                }
//                ytTracks = ytApiHandler.fetchLikedTracks();
//                try {
//                    ytTracks = ytApiHandler.processReturnJson();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                makeList(ytTracks, panelCenter, "yt_track");
                makeButton("YTProceed", "btn_sp_search");
                break;
            case "btn_yt_all_fetch":
                break;
            case "btn_sp_search":
                spotifyApiHandler.getAndSetAccessToken();
                spTracks = spotifyApiHandler.searchTracks(ytTracks);
                makeList(spTracks, panelRight, "sp_track");
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
        String[] itemIdentifier = cb.getActionCommand().split(" | ");
        String actionCode = itemIdentifier[0];
        String itemId = itemIdentifier[2];
        switch (actionCode){
            case "yt_track":
                if(cb.isSelected()){
                    ytTracks.get(itemId).trackActionStatus = true;
                }else{
                    ytTracks.get(itemId).trackActionStatus = false;
                }
                break;
            case "yt_list":
                if(cb.isSelected()){
                    ytPlaylists.get(itemId).trackActionStatus = true;
                }else{
                    ytPlaylists.get(itemId).trackActionStatus = false;
                }
                break;
            case "sp_track":
                if(cb.isSelected()){
                    spTracks.get(itemId).trackActionStatus = true;
                }else{
                    spTracks.get(itemId).trackActionStatus = false;
                }
                break;
        }
    }
}

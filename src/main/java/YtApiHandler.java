import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class YtApiHandler {
    private static final String CLIENT_SECRETS= "client_secret_321071089338-6hlb9h02kql4g1op8nsbkbfpf46d28h7.apps.googleusercontent.com.json";
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");
    private static final String APPLICATION_NAME = "YtMusicToSpotify";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static YouTube youtubeService;

    /**
     * Create an authorized Credential object.
     * Default at localhost:40001/Callback for redirect_uri
     * Please make sure 40001 is used.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = Main.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        LocalServerReceiver localServer = new LocalServerReceiver.Builder().setPort(40001).build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, localServer).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static void authorizeAndSetService()  {
        try{
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = authorize(httpTransport);
            youtubeService = new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException |IOException e){
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static HashMap<String, BaseTrack> fetchLikedTracks(){
        while (youtubeService == null){
            authorizeAndSetService();
        }
        // Define and execute the API request
        try{
            YouTube.Videos.List request = youtubeService.videos().list("snippet,contentDetails,statistics");
            VideoListResponse response = request.setMyRating("like").setMaxResults((long)100).execute();
            List<Video> ytTracks = response.getItems();
            return processReturn(ytTracks);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /** Fetch list of all playlists */
    public HashMap<String, BaseTrack> fetchAllPlaylists(){
        while (youtubeService == null){
            authorizeAndSetService();
        }
        try{
            YouTube.Playlists.List request = youtubeService.playlists().list("snippet,contentDetails");
            PlaylistListResponse response = request.setMaxResults(25L).setMine(true).execute();
            List<Playlist> playlists = response.getItems();
            HashMap<String, BaseTrack> ytPlaylistHashMap = new HashMap<String, BaseTrack>();
            for(Playlist playlist: playlists){
                String playlistId = playlist.getId();
                PlaylistSnippet playlistSnippet = playlist.getSnippet();
                String playlistName = playlistSnippet.getTitle();
                ytPlaylistHashMap.put(playlistId, new BaseTrack(playlistName, new ArrayList<String>(), playlistId, "YouTube_playlist"));
            }
            return ytPlaylistHashMap;
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static HashMap<String, BaseTrack> processReturn(List<Video> ytTracks){
        HashMap<String, BaseTrack> ytHashMap = new HashMap<String, BaseTrack>();
        for(int i = 0; i < ytTracks.size(); i ++){
            Video track = ytTracks.get(i);
            String id = track.getId();
            VideoSnippet videoSnippet = track.getSnippet();
            String title = videoSnippet.getTitle();
            ytHashMap.put(id, new BaseTrack(title, new ArrayList<String>(), id, "YouTube"));
        }
        return ytHashMap;
    }

    public static HashMap<String, BaseTrack> processReturnJson() throws ParseException, IOException{
        JSONParser jsonParser = new JSONParser();
        HashMap<String, BaseTrack> ytHashMap = new HashMap<String, BaseTrack>();

        FileReader reader = new FileReader("dummyReturnFromYt.json");
        JSONObject dummy = (JSONObject) jsonParser.parse(reader);
        JSONArray items = (JSONArray) dummy.get("items");
        Iterator<JSONObject> iterator = items.iterator();
        while(iterator.hasNext()){
            JSONObject itemInfo = (JSONObject) iterator.next().get("snippet");
            String id = (String) iterator.next().get("id");
            String title = (String) itemInfo.get("title");
            ytHashMap.put(id, new BaseTrack(title, new ArrayList<String>(), id, "YouTube"));
        }
        return ytHashMap;
    }
}

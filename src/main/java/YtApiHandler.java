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
import com.google.api.services.youtube.model.VideoListResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class YtApiHandler {
    private static final String CLIENT_SECRETS= "client_secret_321071089338-6hlb9h02kql4g1op8nsbkbfpf46d28h7.apps.googleusercontent.com.json";
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");
    private static final String APPLICATION_NAME = "YtMusicToSpotify";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

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
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void start(){
        try{
            YouTube youtubeService = getService();

            // Define and execute the API request
            YouTube.Videos.List request = youtubeService.videos().list("snippet,contentDetails,statistics");
            VideoListResponse response = request.setMyRating("like").setMaxResults((long)10).execute();
            System.out.println(response);
            System.out.println("asfasdfad");
        } catch (GeneralSecurityException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<BaseTrack> processReturnJson() throws ParseException, IOException{
        JSONParser jsonParser = new JSONParser();
        ArrayList<BaseTrack> songArrayList = new ArrayList<BaseTrack>();

        FileReader reader = new FileReader("dummyReturnFromYt.json");
        JSONObject dummy = (JSONObject) jsonParser.parse(reader);
        JSONArray items = (JSONArray) dummy.get("items");
        Iterator<JSONObject> iterator = items.iterator();
        while(iterator.hasNext()){
            JSONObject itemInfo = (JSONObject) iterator.next().get("snippet");
            String id = (String) iterator.next().get("id");
            String title = (String) itemInfo.get("title");
            songArrayList.add(new BaseTrack(title, new ArrayList<String>(), id, "YouTube"));
        }
        return songArrayList;
    }
}

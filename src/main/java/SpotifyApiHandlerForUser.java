import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.User;
import org.apache.hc.core5.http.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

/**
 * This is a Spotify API handler class for user-specific api calls.
 */
public class SpotifyApiHandlerForUser {
    String SPOTIFY_USER_ID;
    String SPOTIFY_CLIENT_ID;
    String SPOTIFY_CLIENT_SECRET;
    String accessToken;
    String refreshToken;
    SpotifyApi spotifyApi;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private Collection<String> SCOPE = Arrays.asList("playlist-modify-public");

    /**
     * Constructor
     *
     * @throws IOException
     */
    public SpotifyApiHandlerForUser() {
        Properties properties = new Properties();
        try{
            FileInputStream in = new FileInputStream(".env");
            properties.load(in);
            in.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        SPOTIFY_CLIENT_ID = properties.getProperty("spotify_client_id");
        SPOTIFY_CLIENT_SECRET = properties.getProperty("spotify_client_secret");
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(SPOTIFY_CLIENT_ID)
                .setClientSecret(SPOTIFY_CLIENT_SECRET)
                .build();

        /** Authorization */
        authorizationAndGetAccessCode();

        /** Set user_id*/
        fetchUserId();
    }

    /**
     * Direct to browser for user to authorize.
     * accessToken and refreshToken will be set here
     */
    public void authorizationAndGetAccessCode(){
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            AuthorizationCodeFlow acf = new AuthorizationCodeFlow.Builder(
                    BearerToken.authorizationHeaderAccessMethod(),
                    httpTransport,
                    JSON_FACTORY,
                    new GenericUrl("https://accounts.spotify.com/api/token"),
                    new ClientParametersAuthentication(SPOTIFY_CLIENT_ID,SPOTIFY_CLIENT_SECRET),
                    SPOTIFY_CLIENT_ID,
                    "https://accounts.spotify.com/authorize")
                    .setScopes(SCOPE)
                    .build();

            LocalServerReceiver localServer = new LocalServerReceiver.Builder().setPort(40001).build();
            Credential credential =
                    new AuthorizationCodeInstalledApp(acf, localServer).authorize("user");
            accessToken = credential.getAccessToken();
            refreshToken = credential.getRefreshToken();
            spotifyApi.setAccessToken(accessToken);
            spotifyApi.setRefreshToken(refreshToken);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Fetch user_id for current user and set it to SPOTIFY_USER_ID
     */
    public void fetchUserId(){
        try{
            User user = spotifyApi.getCurrentUsersProfile().build().execute();
            SPOTIFY_USER_ID = user.getId();
        } catch(ParseException | SpotifyWebApiException | IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Create a public playlist
     * @param name
     */
    public String createPlaylist(String name) {
        try{
            final Playlist playlist = spotifyApi.createPlaylist(SPOTIFY_USER_ID, name).build().execute();
            return playlist.getId();
        } catch (ParseException | SpotifyWebApiException | IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * Add track to playlist
     */
    public void addTrackToPlaylist(String playlistId, String[] trackUris) {
        try{
            // "spotify:track:track_id"
            spotifyApi.addItemsToPlaylist(playlistId, trackUris).build().execute();
        } catch (ParseException | SpotifyWebApiException | IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This is a Spotify API handler class for user-specific api calls.
 */
public class SpotifyApiHandlerForUser {
    String userId;
    static String SPOTIFY_CLIENT_ID;
    static String SPOTIFY_CLIENT_SECRET;
    static SpotifyApi spotifyApi;
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:40002/callback");
    static AuthorizationCodeUriRequest authorizationCodeUriRequest;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    static String accessToken;


    public SpotifyApiHandlerForUser(String userId) throws IOException {
        this.userId = userId;
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(".env");
        properties.load(in);
        in.close();
        SPOTIFY_CLIENT_ID = properties.getProperty("spotify_client_id");
        SPOTIFY_CLIENT_SECRET = properties.getProperty("spotify_client_secret");
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(SPOTIFY_CLIENT_ID)
                .setClientSecret(SPOTIFY_CLIENT_SECRET)
                .setRedirectUri(redirectUri)
                .build();
        authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
//                .state() @todo Need to add state
                .scope("playlist-modify-public")
                .build();
        System.out.println("asdf");
    }

    public static void authorizationCodeUri_Async(){
        try {
            final CompletableFuture<URI> uriFuture = authorizationCodeUriRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final URI uri = uriFuture.join();

            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            AuthorizationCodeFlow acf = new AuthorizationCodeFlow.Builder(
                    BearerToken.authorizationHeaderAccessMethod(),
                    httpTransport,
                    JSON_FACTORY,
                    new GenericUrl("https://accounts.spotify.com/api/token"),
                    new ClientParametersAuthentication(SPOTIFY_CLIENT_ID,SPOTIFY_CLIENT_SECRET),
                    SPOTIFY_CLIENT_ID,
                    "https://accounts.spotify.com/authorize"
            ).build();

            LocalServerReceiver localServer = new LocalServerReceiver.Builder().setPort(40001).build();
            Credential credential =
                    new AuthorizationCodeInstalledApp(acf, localServer).authorize("user");
            accessToken = credential.getAccessToken();
            spotifyApi.setAccessToken(accessToken);
            final Playlist playlist = spotifyApi.createPlaylist("zezhanchen", "testmigration").build().execute();



            System.out.println("URI: " + uri.toString());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SpotifyWebApiException e) {
            e.printStackTrace();
        }
    }

    public void test(){
        authorizationCodeUri_Async();
    }


}

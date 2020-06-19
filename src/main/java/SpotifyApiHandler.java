import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.FileInputStream;
import org.apache.hc.core5.http.ParseException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SpotifyApiHandler {
    static String SPOTIFY_CLIENT_ID;
    static String SPOTIFY_CLIENT_SECRET;
    static SpotifyApi spotifyApi;
    static ClientCredentialsRequest clientCredentialsRequest;

    public SpotifyApiHandler() throws IOException {
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(".env");
        properties.load(in);
        in.close();
        SPOTIFY_CLIENT_ID = properties.getProperty("spotify_client_id");
        SPOTIFY_CLIENT_SECRET = properties.getProperty("spotify_client_secret");
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(SPOTIFY_CLIENT_ID)
                .setClientSecret(SPOTIFY_CLIENT_SECRET)
                .build();
        clientCredentialsRequest = spotifyApi.clientCredentials().build();
    }

    public static void clientCredentials_Sync() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void clientCredentials_Async() {
        try {
            final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.clientCredentials
            final ClientCredentials clientCredentials = clientCredentialsFuture.join();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
    public static void start() {
        clientCredentials_Sync();
        clientCredentials_Async();
    }

}

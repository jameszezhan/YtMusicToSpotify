import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import java.io.FileInputStream;

import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import org.apache.hc.core5.http.ParseException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.*;

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

    public static void getAndSetAccessToken() {
        clientCredentials_Async();
    }

    public static void searchTrack(String query) throws ParseException, SpotifyWebApiException, IOException {
        final Paging<Track> trackPaging= spotifyApi.searchTracks(query).build().execute();
        final Track[] tracks = trackPaging.getItems();
        for(int i = 0; i< tracks.length; i ++){
            Track track = tracks[i];
            String title = track.getName();
            ArtistSimplified[] artist = track.getArtists();
            String id = track.getId();
            System.out.println(title);
        }
        System.out.println("Total: " + trackPaging.getTotal());
    }
}

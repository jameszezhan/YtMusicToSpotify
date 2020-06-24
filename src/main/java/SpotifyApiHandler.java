import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import java.io.FileInputStream;

import org.apache.hc.core5.http.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class SpotifyApiHandler {
    static String SPOTIFY_CLIENT_ID;
    static String SPOTIFY_CLIENT_SECRET;
    static SpotifyApi spotifyApi;
    static ClientCredentialsRequest clientCredentialsRequest;

    public SpotifyApiHandler() {
        try{
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
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
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

    /**
     * Use client_id and client_secret to get and set access token
     * If access token exists, skip
     */
    public static void getAndSetAccessToken() {
        if (spotifyApi.getAccessToken() == null){
            clientCredentials_Async();
        }
    }

    /**
     * Search specific track (song)
     * @param ytTrackName
     * @throws ParseException
     * @throws SpotifyWebApiException
     * @throws IOException
     */
    public static BaseTrack searchTrack(String ytTrackName)  {
        try{
            final Paging<Track> trackPaging= spotifyApi.searchTracks(ytTrackName).build().execute();
            final Track[] tracks = trackPaging.getItems();
            for(int i = 0; i< tracks.length; i ++){
                Track track = tracks[i];
                String spTrackName = track.getName();

                // If track names are different, skip.
                if (!spTrackName.toLowerCase().equals(ytTrackName.toLowerCase())){
                    continue;
                }else{
                    String spTrackId = track.getId();

                    ArrayList<String> spArtistsArrayList = new ArrayList<String>();
                    BaseTrack spTrack = new BaseTrack(spTrackName, spArtistsArrayList, spTrackId, "Spotify");
                    return spTrack;
                }
            }
        }catch(ParseException | SpotifyWebApiException | IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Search multitracks and return an ArrayList of BaseTrack objects
     * with track information from Spotify
     * @param ytTracks
     * @return
     */
    public static HashMap<String, BaseTrack> searchTracks(HashMap<String, BaseTrack> ytTracks){
        HashMap<String, BaseTrack> spTracks = new HashMap<String, BaseTrack>();
        for(Map.Entry entry: ytTracks.entrySet()){
            BaseTrack ytTrack = (BaseTrack) entry.getValue();
            if(ytTrack.trackActionStatus.equals(false)){
                continue;
            }
            BaseTrack spTrack = searchTrack(ytTrack.trackName);
            if(!Objects.equals(spTrack, null)){
                spTracks.put(spTrack.trackId, spTrack);
            }
        }
        return spTracks;
    }
}

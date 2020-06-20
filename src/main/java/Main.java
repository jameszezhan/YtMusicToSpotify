import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, ParseException, SpotifyWebApiException, org.apache.hc.core5.http.ParseException {
//        YtApiHandler ytApiHandler = new YtApiHandler();
//        ytApiHandler.start();
//        ArrayList<BaseTrack> ytTracks = ytApiHandler.processReturnJson();

//        SpotifyApiHandler spotifyApiHandler = new SpotifyApiHandler();
//        spotifyApiHandler.getAndSetAccessToken();
//        ArrayList<BaseTrack> spTracks = spotifyApiHandler.searchTracks(ytTracks);

        SpotifyApiHandlerForUser spotifyApiHandlerForUser = new SpotifyApiHandlerForUser();
        spotifyApiHandlerForUser.createPlaylist("MIGRATION");

        System.exit(0);
    }
}
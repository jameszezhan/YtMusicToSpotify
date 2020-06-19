import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.json.simple.JSONArray;
import org.apache.hc.core5.http.ParseException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, ParseException, SpotifyWebApiException {
//        YtApiHandler ytApiHandler = new YtApiHandler();
//        JSONArray songList = ytApiHandler.processReturnJson();


        SpotifyApiHandler spotifyApiHandler = new SpotifyApiHandler();
        spotifyApiHandler.getAndSetAccessToken();
        spotifyApiHandler.searchTrack("boys lizzio");


    }
}
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, ParseException, SpotifyWebApiException, org.apache.hc.core5.http.ParseException {
//        GUI gui = new GUI();
//        YtApiHandler ytApiHandler = new YtApiHandler();
////        ytApiHandler.start();
//        ArrayList<BaseTrack> ytTracks = ytApiHandler.processReturnJson();
////
//        SpotifyApiHandler spotifyApiHandler = new SpotifyApiHandler();
//        spotifyApiHandler.getAndSetAccessToken();
//        ArrayList<BaseTrack> spTracks = spotifyApiHandler.searchTracks(ytTracks);
////
//        ArrayList<String> spUris = new ArrayList<String>();
//        for(int i = 0; i<spTracks.size(); i++){
//            System.out.println(spTracks.get(i).trackName);
//            System.out.println("spotify:track:" + spTracks.get(i).trackId);
//            spUris.add("spotify:track:" + spTracks.get(i).trackId);
//        }
//        String[] spUrisArr = new String[spUris.size()];
//        spUrisArr = spUris.toArray(spUrisArr);
//
//        SpotifyApiHandlerForUser spotifyApiHandlerForUser = new SpotifyApiHandlerForUser();
//        String playlistId = spotifyApiHandlerForUser.createPlaylist("MIGRATION");
//        spotifyApiHandlerForUser.addTrackToPlaylist(playlistId, spUrisArr);
//
//        System.exit(0);
    }
}
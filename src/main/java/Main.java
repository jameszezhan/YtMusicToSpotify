import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
//        YtApiHandler ytApiHandler = new YtApiHandler();
//        JSONArray songList = ytApiHandler.processReturnJson();


        SpotifyApiHandler spotifyApiHandler = new SpotifyApiHandler();
        spotifyApiHandler.start();
    }
}
import java.util.ArrayList;

public class BaseTrack {
    public String trackName;
    public ArrayList<String> trackArtists = new ArrayList<String>();
    public String trackId;
    public String trackPlatform;

    public BaseTrack(
            String trackName,
            ArrayList<String> trackArtists,
            String trackId,
            String trackPlatform
    ) {
        this.trackName = trackName;
        this.trackArtists = trackArtists;
        this.trackId = trackId;
        this.trackPlatform = trackPlatform;
    }
}

public class BaseTrack {
    public String trackName;
    public String[] trackArtists = new String[10];
    public String trackId;
    public String trackPlatform;

    public BaseTrack(
            String trackName,
            String[] trackArtists,
            String trackId,
            String trackPlatform
    ) {
        this.trackName = trackName;
        this.trackArtists = trackArtists;
        this.trackId = trackId;
        this.trackPlatform = trackPlatform;
    }
}

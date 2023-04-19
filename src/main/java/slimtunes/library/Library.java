package slimtunes.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Library {
    private final List<Song> songs = new ArrayList<>();
    private final List<String> artists = new ArrayList<>();
    private final Map<String, List<Song>> playlists = new HashMap<>();


    public List<Song> getSongs() {
        return songs;
    }
}

package slimtunes.library;

import java.util.*;

public class Library {
    // Use of LinkedHashMap allows us to preserve order
    private final Map<Integer, Song> songs = new LinkedHashMap<>();
    private final List<String> artists = new ArrayList<>();
    private final Map<String, Playlist> playlists = new LinkedHashMap<>();

    public List<Song> getSongs() {
        return new ArrayList<>(songs.values());
    }

    public void putSong(int trackId, Song song) {
        songs.put(trackId, song);
    }

    public void putPlaylist(String name, Playlist playlist) { playlists.put(name, playlist); }
}

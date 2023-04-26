package slimtunes.library;

import java.util.*;

public class Library {
    // Use of LinkedHashMap allows us to preserve order
    private final Map<Integer, Song> songs = new LinkedHashMap<>();
    private final Set<String> artists = new TreeSet<>();
    private final Map<String, Playlist> playlists = new LinkedHashMap<>();

    public List<Song> getSongs() {
        return new ArrayList<>(songs.values());
    }

    public void putSong(int trackId, Song song) {
        songs.put(trackId, song);
        if (song.getArtist() != null)
            artists.add(song.getArtist());

    }

    public Song getSong(int trackId) {
        return songs.get(trackId);
    }
    public void putPlaylist(String name, Playlist playlist) { playlists.put(name, playlist); }

    public List<Playlist> getPlaylists() { return new ArrayList<>(playlists.values()); }
    public Playlist getPlaylist(String name) { return playlists.get(name); }

    public void printPlaylists() {
        for (String playlist : playlists.keySet())
            System.out.println(playlist + ": " + playlists.get(playlist).getSongs().size());
    }
}

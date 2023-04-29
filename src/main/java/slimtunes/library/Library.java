package slimtunes.library;

import slimtunes.library.xml.WriteXML;
import slimtunes.library.xml.Writer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Library extends WriteXML {

    public enum Fields {
        MAJOR_VERSION, MINOR_VERSION, DATE, APPLICATION_VERSION, FEATURES, SHOW_CONTENT_RATINGS, MUSIC_FOLDER, LIBRARY_PERSISTENT_ID;

        public static String nameToValue(String name) {
            return name.trim().toUpperCase().replace(' ', '_');
        }

        @Override
        public String toString() {
            return NAMES[ordinal()];
        }

        public static final String[] NAMES = {"Major Version", "Minor Version", "Date", "Application Version", "Features", "Show Content Ratings", "Music Folder", "Library Persistent ID"};
    }


    private Integer majorVersion;
    private Integer minorVersion;
    private LocalDateTime date;
    private String applicationVersion;
    private Integer features;
    private Boolean showContentRatings;
    private Path musicFolder;
    private String libraryPersistentId;

    // Use of LinkedHashMap allows us to preserve order
    private final Map<Integer, Song> songs = new LinkedHashMap<>();
    private final Set<String> artists = new TreeSet<>();
    private final Map<String, Playlist> playlists = new LinkedHashMap<>();

    public void addField(String key, String value) {
        key = key.trim();
        value = value.trim();
        Library.Fields field = Library.Fields.valueOf(Library.Fields.nameToValue(key));
        switch (field) {
            case MAJOR_VERSION -> majorVersion = Integer.parseInt(value);
            case MINOR_VERSION -> minorVersion = Integer.parseInt(value);
            case DATE -> date = Library.parseDate(value);
            case APPLICATION_VERSION -> applicationVersion = value;
            case FEATURES -> features = Integer.parseInt(value);
            case SHOW_CONTENT_RATINGS -> showContentRatings = Boolean.parseBoolean(value);
            case MUSIC_FOLDER -> musicFolder = Library.stringToPath(value);
            case LIBRARY_PERSISTENT_ID -> libraryPersistentId = value;
        }
    }

    public static Path stringToPath(String path)  {
        try {
            URI uri = new URI(path);
            path = uri.getPath();
            // Hack for Windows
            if (path.startsWith("/") && path.contains(":"))
                path = path.substring(1);
            return Path.of(path);
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    public static LocalDateTime parseDate(String date) {
            /*if (date.endsWith("Z"))
                return LocalDateTime.parse(date.substring(0, date.length() - 1));
            else*/
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    public List<Song> getSongs() {
        return new ArrayList<>(songs.values());
    }

    public void putSong(int trackId, Song song) {
        songs.put(trackId, song);
        if (song.getArtist() != null)
            artists.add(song.getArtist());
    }

    public void write(Writer writer) {
        writer.writePreamble();
        writer.plist(true);
        writer.dict(true);

        write(writer, Fields.MAJOR_VERSION, majorVersion);
        write(writer, Fields.MINOR_VERSION, minorVersion);
        write(writer, Fields.DATE, date);
        write(writer, Fields.APPLICATION_VERSION, applicationVersion);
        write(writer, Fields.FEATURES, features);
        write(writer, Fields.SHOW_CONTENT_RATINGS, showContentRatings);
        write(writer, Fields.MUSIC_FOLDER, musicFolder);
        write(writer, Fields.LIBRARY_PERSISTENT_ID, libraryPersistentId);

        writer.keyDict("Tracks"); // open dict

            for (Map.Entry<Integer, Song> entry : songs.entrySet()) {
                writer.keyDict(entry.getKey().toString()); // open dict
                entry.getValue().write(writer);
                writer.dict(false);
            }

        writer.dict(false);

        writer.keyArray("Playlists"); // open dict

        for (Map.Entry<String, Playlist> entry : playlists.entrySet()) {
            writer.keyDict(entry.getKey()); // open dict
            entry.getValue().write(writer);
            writer.dict(false);
        }

        writer.array(false);

        writer.dict(false);
        writer.plist(false);
        writer.close();
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

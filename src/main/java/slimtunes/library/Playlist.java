package slimtunes.library;

import slimtunes.library.xml.WriteXML;
import slimtunes.library.xml.Writer;

import java.io.IOException;
import java.util.*;

public class Playlist extends WriteXML {


    public enum Fields {
        NAME, DESCRIPTION, MASTER, PLAYLIST_ID, PLAYLIST_PERSISTENT_ID, DISTINGUISHED_KIND, MUSIC, MOVIES, TV_SHOWS, PODCASTS, AUDIOBOOKS, SMART_INFO, SMART_CRITERIA,  VISIBLE, ALL_ITEMS;

        public static String nameToValue(String name) {
            return name.trim().toUpperCase().replace(' ', '_');
        }

        @Override
        public String toString() {
            return NAMES[ordinal()];
        }

        public static final String[] NAMES = {"Name", "Description", "Master", "Playlist ID", "Playlist Persistent ID", "Distinguished Kind", "Music", "Movies", "TV Shows", "Podcasts", "Audiobooks", "Smart Info", "Smart Criteria", "Visible", "All Items"};
    }

        public void addField(String key, String value) {
            key = key.trim();
            value = value.trim();
            Fields field = Fields.valueOf(Fields.nameToValue(key));
            switch (field)  {
                case NAME -> name = value;
                case DESCRIPTION -> description = value;
                case MASTER -> master = Boolean.parseBoolean(value);
                case PLAYLIST_ID -> playlistId = Integer.parseInt(value);
                case PLAYLIST_PERSISTENT_ID -> playlistPersistentId = value;
                case DISTINGUISHED_KIND -> distinguishedKind = Integer.parseInt(value);
                case MUSIC -> music = Boolean.parseBoolean(value);
                case MOVIES -> movies = Boolean.parseBoolean(value);
                case TV_SHOWS -> tvShows = Boolean.parseBoolean(value);
                case PODCASTS -> podcasts = Boolean.parseBoolean(value);
                case AUDIOBOOKS -> audiobooks = Boolean.parseBoolean(value);
                case SMART_INFO -> smartInfo = value;
                case SMART_CRITERIA -> smartCriteria = value;
                case VISIBLE -> visible = Boolean.parseBoolean(value);
                case ALL_ITEMS -> allItems = Boolean.parseBoolean(value);
            }
    }

    public String getName() {
        return name;
    }

    private String name;
    private String description;
    private Boolean master;
    private Integer playlistId;
    private String playlistPersistentId;
    private Integer distinguishedKind;
    private Boolean music;
    private Boolean movies;
    private Boolean tvShows;
    private Boolean podcasts;
    private Boolean audiobooks;
    private String smartInfo;
    private String smartCriteria;
    private Boolean visible;
    private Boolean allItems;

    private final Map<Integer, Song> songs = new LinkedHashMap<>();

    public List<Song> getSongs() {
        return new ArrayList<>(songs.values());
    }

    public void addSong(int trackId, Song song) {
        songs.put(trackId, song);
    }


    @Override
    public void write(Writer writer) {
        write(writer, Fields.NAME, name);
        write(writer, Fields.DESCRIPTION, description);
        write(writer, Fields.MASTER, master);
        write(writer, Fields.PLAYLIST_ID, playlistId);
        write(writer, Fields.PLAYLIST_PERSISTENT_ID, playlistPersistentId);
        write(writer, Fields.DISTINGUISHED_KIND, distinguishedKind);
        write(writer, Fields.MUSIC, music);
        write(writer, Fields.MOVIES, movies);
        write(writer, Fields.TV_SHOWS, tvShows);
        write(writer, Fields.VISIBLE, visible);
        write(writer, Fields.PODCASTS, podcasts);
        write(writer, Fields.AUDIOBOOKS, audiobooks);
        write(writer, Fields.ALL_ITEMS, allItems);
        if (smartInfo != null)
            writer.keyData(Fields.SMART_INFO.toString(), smartInfo);
        if (smartCriteria != null)
            writer.keyData(Fields.SMART_CRITERIA.toString(), smartCriteria);

        if (songs.size() > 0) {
            writer.keyArray("Playlist Items"); // open array

            for (Integer id : songs.keySet()) {
                writer.dict(true);
                writer.keyInteger("Track ID", id);
                writer.dict(false);
            }
            writer.array(false);
        }
    }


    @Override
    public String toString() {
        return name;
    }
}

package slimtunes.library;

import java.util.*;

public class Playlist {

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
    public String toString() {
        return name;
    }
}

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

    public static class Builder {
        private final Map<String, String> fields = new HashMap<>();
        private Map<Integer, Song> songs = null;
        private boolean building = false;

        public void startBuilding() {
            fields.clear();
            songs = new LinkedHashMap<>();
            building = true;
        }

        public void addField(String key, String value) {
            if (!building)
                throw new IllegalArgumentException("Cannot add a field unless in the process of building a playlist.");

            fields.put(key.trim(), value.trim());
        }

        public void addSong(Integer key, Song song) {
            if (!building)
                throw new IllegalArgumentException("Cannot add a song unless in the process of building a playlist.");

            songs.put(key, song);
        }

        public Playlist buildPlaylist() {
            if (!building)
                throw new IllegalArgumentException("Cannot build a playlist until building has been started.");

            building = false;

            String name = null;
            String description = null;
            boolean master = false;
            int playlistId = -1;
            String playlistPersistentId = null;
            int distinguishedKind = -1;
            boolean music = false;
            boolean movies = false;
            boolean tvShows = false;
            boolean podcasts = false;
            boolean audiobooks = false;
            String smartInfo = null;
            String smartCriteria = null;
            boolean visible = false;
            boolean allItems = false;

            for(Map.Entry<String, String> entry : fields.entrySet()) {
                try {
                    Fields field = Fields.valueOf(Fields.nameToValue(entry.getKey()));
                    String value = entry.getValue();
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
                } catch (IllegalArgumentException ignored) {}
            }

            return new Playlist(name, description, master, playlistId, playlistPersistentId, distinguishedKind, music, movies, tvShows, podcasts, audiobooks, smartInfo, smartCriteria, visible, allItems, songs);
        }
    }

    public String getName() {
        return name;
    }

    private final String name;
    private final String description;
    private final boolean master;
    private final int playlistId;
    private final String playlistPersistentId;
    private final int distinguishedKind;
    private final boolean music;
    private final boolean movies;
    private final boolean tvShows;
    private final boolean podcasts;
    private final boolean audiobooks;
    private final String smartInfo;
    private final String smartCriteria;
    private final boolean visible;
    private final boolean allItems;

    private final Map<Integer, Song> songs;

    public List<Song> getSongs() {
        return new ArrayList<>(songs.values());
    }

    private Playlist(String name, String description, boolean master, int playlistId, String playlistPersistentId, int distinguishedKind, boolean music, boolean movies, boolean tvShows, boolean podcasts, boolean audiobooks, String smartInfo, String smartCriteria, boolean visible, boolean allItems, Map<Integer, Song> songs) {
        this.name = name;
        this.description = description;
        this.master = master;
        this.playlistId = playlistId;
        this.playlistPersistentId = playlistPersistentId;
        this.distinguishedKind = distinguishedKind;
        this.music = music;
        this.movies = movies;
        this.tvShows = tvShows;
        this.podcasts = podcasts;
        this.audiobooks = audiobooks;
        this.smartInfo = smartInfo;
        this.smartCriteria = smartCriteria;
        this.visible = visible;
        this.allItems = allItems;
        this.songs = songs;
    }

    @Override
    public String toString() {
        return name;
    }
}

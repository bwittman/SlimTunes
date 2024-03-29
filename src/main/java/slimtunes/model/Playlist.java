package slimtunes.model;

import slimtunes.model.xml.WriteXML;
import slimtunes.model.xml.Writer;

import java.util.*;

public class Playlist extends FileTableModel implements WriteXML {

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

        public void addField(Fields field, String value) {

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
                case SMART_INFO -> smartInfo = Library.cleanData(value);
                case SMART_CRITERIA -> smartCriteria = Library.cleanData(value);
                case VISIBLE -> visible = Boolean.parseBoolean(value);
                case ALL_ITEMS -> allItems = Boolean.parseBoolean(value);
            }
    }

    public void addField(String key, String value) {
        Fields field = Fields.valueOf(Fields.nameToValue(key.trim()));
        addField(field, value);
    }



    public Object getField(Fields field) {
        return switch (field)  {
            case NAME -> name;
            case DESCRIPTION -> description;
            case MASTER -> master;
            case PLAYLIST_ID -> playlistId;
            case PLAYLIST_PERSISTENT_ID -> playlistPersistentId;
            case DISTINGUISHED_KIND -> distinguishedKind;
            case MUSIC -> music;
            case MOVIES -> movies;
            case TV_SHOWS -> tvShows;
            case PODCASTS -> podcasts;
            case AUDIOBOOKS -> audiobooks;
            case SMART_INFO -> smartInfo;
            case SMART_CRITERIA -> smartCriteria;
            case VISIBLE -> visible;
            case ALL_ITEMS -> allItems;
        };
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

    private final List<File> files = new ArrayList<>();

    @Override
    public void add(File file) {
        files.add(file);
        fireTableDataChanged();
    }

    @Override
    public void add(int index, File file) {
        files.add(index, file);
        fireTableDataChanged();
    }

    public boolean contains(File file) {
        return files.contains(file);
    }

    @Override
    public int find(File file) {
        return files.indexOf(file);
    }

    @Override
    public boolean remove(File file) {
        boolean removed = files.remove(file);
        if (removed)
            fireTableDataChanged();
        return removed;
    }

    @Override
    public void write(Writer writer) {
        writer.write(Fields.NAME, name);
        writer.write(Fields.DESCRIPTION, description);
        writer.write(Fields.MASTER, master);
        writer.write(Fields.PLAYLIST_ID, playlistId);
        writer.write(Fields.PLAYLIST_PERSISTENT_ID, playlistPersistentId);
        writer.write(Fields.DISTINGUISHED_KIND, distinguishedKind);
        writer.write(Fields.MUSIC, music);
        writer.write(Fields.MOVIES, movies);
        writer.write(Fields.TV_SHOWS, tvShows);
        writer.write(Fields.VISIBLE, visible);
        writer.write(Fields.PODCASTS, podcasts);
        writer.write(Fields.AUDIOBOOKS, audiobooks);
        writer.write(Fields.ALL_ITEMS, allItems);
        if (smartInfo != null)
            writer.keyData(Fields.SMART_INFO.toString(), smartInfo);
        if (smartCriteria != null)
            writer.keyData(Fields.SMART_CRITERIA.toString(), smartCriteria);

        if (!files.isEmpty()) {
            writer.keyArray("Playlist Items"); // open array

            for (File file : files) {
                writer.dict(true);
                writer.keyInteger("Track ID", Long.valueOf(file.getTrackId()));
                writer.dict(false);
            }
            writer.array(false);
        }
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public File get(int rowIndex) {
        return files.get(rowIndex);
    }

    @Override
    public String toString() {
        return name;
    }
}

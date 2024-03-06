package slimtunes.model;

import slimtunes.model.xml.WriteXML;
import slimtunes.model.xml.Writer;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Library extends FileTableModel implements WriteXML {

    public Playlist createPlaylist(String name) {
        Playlist playlist = new Playlist();
        ++lastPlaylistIndex;
        playlist.addField(Playlist.Fields.PLAYLIST_ID, lastPlaylistIndex + "");
        playlist.addField(Playlist.Fields.PLAYLIST_PERSISTENT_ID, String.format("%016X", File.RANDOM.nextLong()));
        playlist.addField(Playlist.Fields.NAME, name);
        return playlist;
    }

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

    private int lastFileIndex = 0;
    private int lastPlaylistIndex = 0;

    // Use of LinkedHashMap allows us to preserve order
    private final Map<Integer, File> fileMap = new HashMap<>();
    private final List<File> files = new ArrayList<>();

    private final Set<String> artists = new TreeSet<>();
    private final DefaultListModel<FileTableModel> playlists = new DefaultListModel<>();

    public Library() {
        playlists.addElement(this); // First playlist is always the library itself
    }

    public void addField(String key, String value) {
        Library.Fields field = Library.Fields.valueOf(Library.Fields.nameToValue(key.trim()));
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

    public static String pathToString(Path path)  {
        try {
            String fileName = path.getFileName().toString();
            int dotLocation = fileName.lastIndexOf('.');
            // Hack: Regular (non-directory) files will have an extension preceded by a dot
            // We assume it's a regular file if its name is at least 3 characters long,
            // its last dot is not the beginning, and its last dot is not at the end
            boolean isFile = fileName.length() >= 3 && dotLocation > 0 && dotLocation < fileName.length() - 1;
            URI uri = path.toUri();
            uri = new URI(uri.getScheme(), "localhost", uri.getPath(), null, null);
            if (isFile)
                return Writer.escapeXML(uri.toASCIIString());
            else { // Add / to directory names if not already present
                String directoryName = Writer.escapeXML(uri.toASCIIString());
                if (directoryName.endsWith("/"))
                    return  directoryName;
                else
                    return directoryName + "/";
            }
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    public boolean removePlaylist(FileTableModel playlist) {
        return playlists.removeElement(playlist);
    }

    public static LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String formatDate(LocalDateTime dateTime) {
        dateTime = dateTime.minusNanos(dateTime.getNano()); // Remove fractional seconds
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }
    public static String cleanData(String data) {
        return data.trim().replaceAll("\t", "");
    }

    public static String cleanComments(String comment) {
        return comment.replaceAll("\r\n", "\n");
    }

    public void putFile(int trackId, File file) {
        putFile(trackId, file, files.size());
    }

    private void putFile(int trackId, File file, int index) {
        if (trackId > lastFileIndex)
            lastFileIndex = trackId;
        fileMap.put(trackId, file);
        files.add(index, file);
        if (file.getArtist() != null)
            artists.add(file.getArtist());
    }

    @Override
    public boolean remove(File file) {
        if(file != null) {
            File result = fileMap.remove(file.getTrackId());
            if (result != null) {
                files.remove(file);
                fireTableDataChanged();
            }
            return result != null;
        }

        return false;
    }

    @Override
    public void add(File file) {
        ++lastFileIndex;
        file.addField(File.Fields.TRACK_ID, "" + lastFileIndex);
        putFile(lastFileIndex, file);
        fireTableDataChanged();
    }

    @Override
    public void add(int index, File file) {
        putFile(file.getTrackId(), file, index);
        fireTableDataChanged();
    }

    @Override
    public boolean contains(File file) {
        return files.contains(file);
    }

    @Override
    public int find(File file) {
        return files.indexOf(file);
    }

    public void write(Writer writer) {
        writer.writePreamble();
        writer.plist(true);
        writer.dict(true);

        writer.write(Fields.MAJOR_VERSION, majorVersion);
        writer.write(Fields.MINOR_VERSION, minorVersion);
        writer.write(Fields.DATE, date);
        writer.write(Fields.APPLICATION_VERSION, applicationVersion);
        writer.write(Fields.FEATURES, features);
        writer.write(Fields.SHOW_CONTENT_RATINGS, showContentRatings);
        writer.write(Fields.MUSIC_FOLDER, musicFolder);
        writer.write(Fields.LIBRARY_PERSISTENT_ID, libraryPersistentId);

        writer.keyDict("Tracks"); // open dict

        for (File file : files) {
            writer.keyDict(file.getTrackId() + ""); // open dict
            file.write(writer);
            writer.dict(false);
        }

        writer.dict(false);

        writer.keyArray("Playlists"); // open dict

        // Skip the first playlist, because it's the library itself
        for (int i = 1; i < playlists.size(); ++i) {
            FileTableModel playlist = playlists.get(i);
            writer.dict(true); // open dict
            playlist.write(writer);
            writer.dict(false);
        }

        writer.array(false);

        writer.dict(false);
        writer.plist(false);
        writer.close();
    }

    @Override
    public String toString() {
        // Non-breaking spaces make All Files look less cramped
        return "<html><b>All Files</b>&nbsp;&nbsp;&nbsp;&nbsp;</html>";
    }

    public File getFileById(int trackId) {
        return fileMap.get(trackId);
    }
    public void addPlaylist(Playlist playlist) {
        Integer id = (Integer) playlist.getField(Playlist.Fields.PLAYLIST_ID);
        if (id != null)
            lastPlaylistIndex = Math.max(id, lastPlaylistIndex);
        playlists.addElement(playlist);
    }

    public void addPlaylist(int index, Playlist playlist) {
        Integer id = (Integer) playlist.getField(Playlist.Fields.PLAYLIST_ID);
        if (id != null)
            lastPlaylistIndex = Math.max(id, lastPlaylistIndex);
        playlists.insertElementAt(playlist, index);
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public File get(int rowIndex) {
        return files.get(rowIndex);
    }


    public DefaultListModel<FileTableModel> getPlaylists() { return playlists; }
}

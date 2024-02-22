package slimtunes.model;

import slimtunes.model.xml.WriteXML;
import slimtunes.model.xml.Writer;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Library extends FileTableModel implements WriteXML {
    

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

    private int lastIndex = 0;

    // Use of LinkedHashMap allows us to preserve order
    private final Map<Integer, File> files = new LinkedHashMap<>();
    
    private List<File> fileList = null;
    private final Set<String> artists = new TreeSet<>();
    private final List<Playlist> playlists = new ArrayList<>();

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
        if (trackId > lastIndex)
            lastIndex = trackId;
        files.put(trackId, file);
        if (file.getArtist() != null)
            artists.add(file.getArtist());
    }

    @Override
    public boolean remove(File file) {
        if(file != null) {
            File result = files.remove(file.getTrackId());
            if (result != null) {
                fileList = null;
                fireTableDataChanged();
            }
            return result != null;
        }

        return false;
    }

    @Override
    public void add(File file) {
        ++lastIndex;
        file.addField(File.Fields.TRACK_ID, "" + lastIndex);
        putFile(lastIndex, file);
        fileList = null;
        fireTableDataChanged();
    }

    public void write(Writer writer) {
        writer.writePreamble();
        writer.plist(true);
        writer.dict(true);

        WriteXML.write(writer, Fields.MAJOR_VERSION, majorVersion);
        WriteXML.write(writer, Fields.MINOR_VERSION, minorVersion);
        WriteXML.write(writer, Fields.DATE, date);
        WriteXML.write(writer, Fields.APPLICATION_VERSION, applicationVersion);
        WriteXML.write(writer, Fields.FEATURES, features);
        WriteXML.write(writer, Fields.SHOW_CONTENT_RATINGS, showContentRatings);
        WriteXML.write(writer, Fields.MUSIC_FOLDER, musicFolder);
        WriteXML.write(writer, Fields.LIBRARY_PERSISTENT_ID, libraryPersistentId);

        writer.keyDict("Tracks"); // open dict

        for (Map.Entry<Integer, File> entry : files.entrySet()) {
            writer.keyDict(entry.getKey().toString()); // open dict
            entry.getValue().write(writer);
            writer.dict(false);
        }

        writer.dict(false);

        writer.keyArray("Playlists"); // open dict

        for (Playlist playlist : playlists) {
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
        return "<html><b>All Files</b></html>";
    }

    public File getFile(int trackId) {
        return files.get(trackId);
    }
    public void addPlaylist(Playlist playlist) { playlists.add(playlist); }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public File get(int rowIndex) {
        if (fileList == null)
            fileList = new ArrayList<>(files.values());
        return fileList.get(rowIndex);
    }


    public List<Playlist> getPlaylists() { return playlists; }
}

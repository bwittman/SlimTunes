package slimtunes.library;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Song {
    public enum Fields {
        TRACK_ID, NAME, ARTIST, KIND, SIZE, TOTAL_TIME, DATE_MODIFIED, DATE_ADDED, BIT_RATE, SAMPLE_RATE, PLAY_COUNT, PLAY_DATE, PLAY_DATE_UTC, PERSISTENT_ID, TRACK_TYPE, LOCATION, FILE_FOLDER_COUNT, LIBRARY_FOLDER_COUNT, SKIP_COUNT, SKIP_DATE, ALBUM_ARTIST, COMPOSER, ALBUM, GENRE, TRACK_NUMBER, YEAR, TRACK_COUNT, ARTWORK_COUNT, SORT_NAME, COMMENTS, NORMALIZATION, BPM, SORT_ALBUM, SORT_ALBUM_ARTIST, SORT_ARTIST, DISC_NUMBER, DISC_COUNT, GROUPING, WORK, SORT_COMPOSER, VOLUME_ADJUSTMENT, COMPILATION, PART_OF_GAPLESS_ALBUM;

        public static String nameToValue(String name) {
            return name.trim().toUpperCase().replace(' ', '_');
        }

        @Override
        public String toString() {
            return NAMES[ordinal()];
        }

        public static final String[] NAMES = {"Track ID", "Name", "Artist", "Kind", "Size", "Total Time", "Date Modified", "Date Added", "Bit Rate", "Sample Rate", "Play Count", "Play Date", "Play Date UTC", "Persistent ID", "Track Type", "Location", "File Folder Count", "Library Folder Count", "Skip Count", "Skip Date", "Album Artist", "Composer", "Album", "Genre", "Track Number", "Year", "Track Count", "Artwork Count", "Sort Name", "Comments", "Normalization", "BPM", "Sort Album", "Sort Album Artist", "Sort Artist", "Disc Number", "Disc Count", "Grouping", "Work", "Sort Composer", "Volume Adjustment", "Compilation", "Part Of Gapless Album"};
    }

    public static class Builder {
        private Map<String, String> fields = new HashMap<>();
        private boolean building = false;

        public void startBuilding() {
            fields.clear();
            building = true;
        }

        public void addField(String key, String value) {
            if (!building)
                throw new IllegalArgumentException("Cannot add a field unless in the process of building a song.");

            fields.put(key.trim(), value.trim());
        }

        private static LocalDateTime parseDate(String date) {
            if (date.endsWith("Z"))
                return LocalDateTime.parse(date.substring(0, date.length() - 1));
            else
                return LocalDateTime.parse(date);
        }

        private Path stringToPath(String path)  {
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

        public Song buildSong() {
            if (!building)
                throw new IllegalArgumentException("Cannot build a song until building has been started.");

            building = false;

            int trackId = -1;
            String name = null;
            String artist = null;
            String kind = null;
            int size = -1; // bytes
            int totalTime = -1; // seconds
            LocalDateTime dateModified = null;
            LocalDateTime dateAdded = null;
            int bitRate = -1;
            int sampleRate = -1;
            int playCount = -1;
            long playDate = -1;
            LocalDateTime playDateUTC = null;
            String persistentID = null;
            String trackType = null;
            Path location = null;
            int fileFolderCount = -1;
            int libraryFolderCount = -1;
            int skipCount = -1;
            LocalDateTime skipDate = null;
            String albumArtist = null;
            String composer = null;
            String album = null;
            String genre = null;
            int trackNumber = -1;
            int year = -1;
            int trackCount = -1;
            int artworkCount = -1;
            String sortName = null;
            String comments = null;
            int normalization = -1;
            int bpm = -1;
            String sortAlbum = null;
            String sortAlbumArtist = null;
            String sortArtist = null;
            int discNumber = -1;
            int discCount = -1;
            String grouping = null;
            String work = null;
            String sortComposer = null;
            int volumeAdjustment = -1;
            boolean compilation = false;
            boolean partOfGaplessAlbum = false;

            for(Map.Entry<String, String> entry : fields.entrySet()) {
                try {
                    Fields field = Fields.valueOf(Fields.nameToValue(entry.getKey()));
                    String value = entry.getValue();
                    switch (field) {
                        case TRACK_ID -> trackId = Integer.parseInt(value);
                        case NAME -> name = value;
                        case ARTIST -> artist = value;
                        case KIND -> kind = value;
                        case SIZE -> size = Integer.parseInt(value);
                        case TOTAL_TIME -> totalTime = Integer.parseInt(value);
                        case DATE_MODIFIED -> dateModified = parseDate(value);
                        case DATE_ADDED -> dateAdded = parseDate(value);
                        case BIT_RATE -> bitRate = Integer.parseInt(value);
                        case SAMPLE_RATE -> sampleRate = Integer.parseInt(value);
                        case PLAY_COUNT -> playCount = Integer.parseInt(value);
                        case PLAY_DATE -> playDate = Long.parseLong(value);
                        case PLAY_DATE_UTC -> playDateUTC = parseDate(value);
                        case PERSISTENT_ID -> persistentID = value;
                        case TRACK_TYPE -> trackType = value;
                        case LOCATION -> location = stringToPath(value);
                        case FILE_FOLDER_COUNT -> fileFolderCount = Integer.parseInt(value);
                        case LIBRARY_FOLDER_COUNT -> libraryFolderCount = Integer.parseInt(value);
                        case SKIP_COUNT -> skipCount = Integer.parseInt(value);
                        case SKIP_DATE -> skipDate = parseDate(value);
                        case ALBUM_ARTIST -> albumArtist = value;
                        case COMPOSER -> composer = value;
                        case ALBUM -> album = value;
                        case GENRE -> genre = value;
                        case TRACK_NUMBER -> trackNumber = Integer.parseInt(value);
                        case YEAR -> year = Integer.parseInt(value);
                        case TRACK_COUNT -> trackCount = Integer.parseInt(value);
                        case ARTWORK_COUNT -> artworkCount = Integer.parseInt(value);
                        case SORT_NAME -> sortName = value;
                        case COMMENTS -> comments = value;
                        case NORMALIZATION -> normalization = Integer.parseInt(value);
                        case BPM -> bpm = Integer.parseInt(value);
                        case SORT_ALBUM -> sortAlbum = value;
                        case SORT_ALBUM_ARTIST -> sortAlbumArtist = value;
                        case SORT_ARTIST -> sortArtist = value;
                        case DISC_NUMBER -> discNumber = Integer.parseInt(value);
                        case DISC_COUNT -> discCount = Integer.parseInt(value);
                        case GROUPING -> grouping = value;
                        case WORK -> work = value;
                        case SORT_COMPOSER -> sortComposer = value;
                        case VOLUME_ADJUSTMENT -> volumeAdjustment = Integer.parseInt(value);
                        case COMPILATION -> compilation = Boolean.parseBoolean(value);
                        case PART_OF_GAPLESS_ALBUM -> partOfGaplessAlbum = Boolean.parseBoolean(value);
                    }
                } catch (IllegalArgumentException ignored) {}
            }

            return new Song(trackId, name, artist, kind, size, totalTime, dateModified, dateAdded, bitRate, sampleRate,
                    playCount, playDate, playDateUTC, persistentID, trackType, location, fileFolderCount, libraryFolderCount,
                    skipCount, skipDate, albumArtist, composer, album, genre, trackNumber, year, trackCount,
                    artworkCount, sortName, comments, normalization, bpm, sortAlbum, sortAlbumArtist, sortArtist,
                    discNumber, discCount, grouping, work, sortComposer, volumeAdjustment, compilation, partOfGaplessAlbum);
        }


    }



    public int getTrackId() {
        return trackId;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getKind() {
        return kind;
    }

    public int getSize() {
        return size;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public int getBitRate() {
        return bitRate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getPlayCount() {
        return playCount;
    }

    public long getPlayDate() {
        return playDate;
    }

    public LocalDateTime getPlayDateUTC() {
        return playDateUTC;
    }

    public String getPersistentID() {
        return persistentID;
    }

    public String getTrackType() {
        return trackType;
    }

    public Path getLocation() {
        return location;
    }

    public int getFileFolderCount() {
        return fileFolderCount;
    }

    public int getLibraryFolderCount() {
        return libraryFolderCount;
    }

    public int getSkipCount() {
        return skipCount;
    }

    public LocalDateTime getSkipDate() {
        return skipDate;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getComposer() {
        return composer;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getYear() {
        return year;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public int getArtworkCount() {
        return artworkCount;
    }

    public String getSortName() {
        return sortName;
    }

    public String getComments() {
        return comments;
    }

    public int getNormalization() {
        return normalization;
    }

    public int getBpm() {
        return bpm;
    }

    public String getSortAlbum() {
        return sortAlbum;
    }

    public String getSortAlbumArtist() {
        return sortAlbumArtist;
    }

    public String getSortArtist() {
        return sortArtist;
    }

    public int getDiscNumber() {
        return discNumber;
    }

    public int getDiscCount() {
        return discCount;
    }

    public String getGrouping() {
        return grouping;
    }

    public String getWork() {
        return work;
    }

    public String getSortComposer() {
        return sortComposer;
    }

    public int getVolumeAdjustment() {
        return volumeAdjustment;
    }

    public boolean isCompilation() {
        return compilation;
    }

    public boolean isPartOfGaplessAlbum() {
        return partOfGaplessAlbum;
    }

    public static String millisecondsToTime(int milliseconds) {
        if (milliseconds == -1)
            return "";

        int seconds = milliseconds / 1000;
        int hours = seconds / 3600;
        int minutes = seconds % 3600 / 60;
        seconds = seconds % 60;

        if (hours >= 1)
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%d:%02d", minutes, seconds);
    }

    private final int trackId;
    private final String name;
    private final String artist;
    private final String kind;
    private final int size; // bytes

    private final int totalTime; // seconds

    private final LocalDateTime dateModified;
    private final LocalDateTime dateAdded;

    private final int bitRate;

    private final int sampleRate;

    private final int playCount;

    private final long playDate;

    private final LocalDateTime playDateUTC;

    private final String persistentID;

    private final String trackType;

    private final Path location;


    private final int fileFolderCount;

    private final int libraryFolderCount;

    private final int skipCount;

    private final LocalDateTime skipDate;


    private final String albumArtist;

    private final String composer;

    private final String album;
    private final String genre;
    private final int trackNumber;
    private final int year;

    private final int trackCount;

    private final int artworkCount;

    private final String sortName;

    private final String comments;

    private final int normalization;

    private final int bpm;

    private final String sortAlbum;
    private final String sortAlbumArtist;
    private final String sortArtist;
    private final int discNumber;
    private final int discCount;

    private final String grouping;

    private final String work;

    private final String sortComposer;

    private final int volumeAdjustment;

    private final boolean compilation;

    private final boolean partOfGaplessAlbum;

    private Song(int trackId, String name, String artist, String kind, int size, int totalTime, LocalDateTime dateModified,
                 LocalDateTime dateAdded, int bitRate, int sampleRate, int playCount, long playDate, LocalDateTime playDateUTC,
                 String persistentID, String trackType, Path location, int fileFolderCount, int libraryFolderCount,
                 int skipCount, LocalDateTime skipDate, String albumArtist, String composer, String album, String genre,
                 int trackNumber, int year, int trackCount, int artworkCount, String sortName, String comments,
                 int normalization, int bpm, String sortAlbum, String sortAlbumArtist, String sortArtist,
                 int discNumber, int discCount, String grouping, String work, String sortComposer, int volumeAdjustment,
                 boolean compilation, boolean partOfGaplessAlbum) {
        this.trackId = trackId;
        this.name = name;
        this.artist = artist;
        this.kind = kind;
        this.size = size;
        this.totalTime = totalTime;
        this.dateModified = dateModified;
        this.dateAdded = dateAdded;
        this.bitRate = bitRate;
        this.sampleRate = sampleRate;
        this.playCount = playCount;
        this.playDate = playDate;
        this.playDateUTC = playDateUTC;
        this.persistentID = persistentID;
        this.trackType = trackType;
        this.location = location;
        this.fileFolderCount = fileFolderCount;
        this.libraryFolderCount = libraryFolderCount;
        this.skipCount = skipCount;
        this.skipDate = skipDate;
        this.albumArtist = albumArtist;
        this.composer = composer;
        this.album = album;
        this.genre = genre;
        this.trackNumber = trackNumber;
        this.year = year;
        this.trackCount = trackCount;
        this.artworkCount = artworkCount;
        this.sortName = sortName;
        this.comments = comments;
        this.normalization = normalization;
        this.bpm = bpm;
        this.sortAlbum = sortAlbum;
        this.sortAlbumArtist = sortAlbumArtist;
        this.sortArtist = sortArtist;
        this.discNumber = discNumber;
        this.discCount = discCount;
        this.grouping = grouping;
        this.work = work;
        this.sortComposer = sortComposer;
        this.volumeAdjustment = volumeAdjustment;
        this.compilation = compilation;
        this.partOfGaplessAlbum = partOfGaplessAlbum;
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
    }

    private static void append(StringBuilder builder, String name, String value) {
        if(value != null) {
            builder.append(name).append(": ").append(value).append("\n");
        }
    }

    private static void append(StringBuilder builder, String name, long value) {
        if(value >= 0) {
            builder.append(name).append(": ").append(value).append("\n");
        }
    }

    private static void append(StringBuilder builder, String name, LocalDateTime dateTime) {
        if(dateTime != null) {
            builder.append(name).append(": ").append(formatDate(dateTime)).append("\n");
        }
    }

    private static void append(StringBuilder builder, String name, boolean value) {
        if(value) {
            builder.append(name).append(": ").append(value).append("\n");
        }
    }

    private static void appendAsTime(StringBuilder builder, String name, int time) {
        if(time >= 0) {
            builder.append(name).append(": ").append(millisecondsToTime(time)).append("\n");
        }
    }

    private static void append(StringBuilder builder, String name, Path path) {
        if(path != null) {
            builder.append(name).append(": ").append(path).append("\n");
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        append(builder, "Track ID", trackId);
        append(builder, "Name", name);
        append(builder, "Artist", artist);
        append(builder, "Kind", kind);
        append(builder, "Size", size);
        appendAsTime(builder, "Total Time", totalTime);
        append(builder, "Date Modified", dateModified);
        append(builder, "Date Added", dateAdded);
        append(builder, "Bit Rate", bitRate);
        append(builder, "Sample Rate", sampleRate);
        append(builder, "Play Count", playCount);
        append(builder, "Play Date", playDate);
        append(builder, "Play Date UTC", playDateUTC);
        append(builder, "Persistent ID", persistentID);
        append(builder, "Track Type", trackType);
        append(builder, "Location", location); // TODO: Fix Path rendering
        append(builder, "File Folder Count", fileFolderCount);
        append(builder, "Library Folder Count", libraryFolderCount);
        append(builder, "Skip Count", skipCount);
        append(builder, "Skip Date", skipDate);
        append(builder, "Album Arist", albumArtist);
        append(builder, "Composer", composer);
        append(builder, "Album", album);
        append(builder, "Genre", genre);
        append(builder, "Track Number", trackNumber);
        append(builder, "Year", year);
        append(builder, "Track Count", trackCount);
        append(builder, "Artwork Count", artworkCount);
        append(builder, "Sort Name", sortName);
        append(builder, "Comments", comments);
        append(builder, "Normalization", normalization);
        append(builder, "BPM", bpm);
        append(builder, "Sort Album", sortAlbum);
        append(builder, "Sort Album Artist", sortAlbumArtist);
        append(builder, "Sort Artist", sortArtist);
        append(builder, "Disc Number", discNumber);
        append(builder, "Disc Count", discCount);
        append(builder, "Grouping", grouping);
        append(builder, "Work", work);
        append(builder, "Sort Composer", sortComposer);
        append(builder, "Volume Adjustment", volumeAdjustment);
        append(builder, "Compilation", compilation);
        append(builder, "Part of Gapless Album", partOfGaplessAlbum);
        return builder.toString();
    }
}

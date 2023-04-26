package slimtunes.library;

import slimtunes.gui.Time;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
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
            /*if (date.endsWith("Z"))
                return LocalDateTime.parse(date.substring(0, date.length() - 1));
            else*/
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);


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

            Integer trackId = null;
            String name = null;
            String artist = null;
            String kind = null;
            Integer size = null; // bytes
            Integer totalTime = null; // seconds
            LocalDateTime dateModified = null;
            LocalDateTime dateAdded = null;
            Integer bitRate = null;
            Integer sampleRate = null;
            Integer playCount = null;
            Long playDate = null;
            LocalDateTime playDateUTC = null;
            String persistentID = null;
            String trackType = null;
            Path location = null;
            Integer fileFolderCount = null;
            Integer libraryFolderCount = null;
            Integer skipCount = null;
            LocalDateTime skipDate = null;
            String albumArtist = null;
            String composer = null;
            String album = null;
            String genre = null;
            Integer trackNumber = null;
            Integer year = null;
            Integer trackCount = null;
            Integer artworkCount = null;
            String sortName = null;
            String comments = null;
            Integer normalization = null;
            Integer bpm = null;
            String sortAlbum = null;
            String sortAlbumArtist = null;
            String sortArtist = null;
            Integer discNumber = null;
            Integer discCount = null;
            String grouping = null;
            String work = null;
            String sortComposer = null;
            Integer volumeAdjustment = null;
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



    public Integer getTrackId() {
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

    public Integer getSize() {
        return size;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public Integer getBitRate() {
        return bitRate;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public Long getPlayDate() {
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

    public Integer getFileFolderCount() {
        return fileFolderCount;
    }

    public Integer getLibraryFolderCount() {
        return libraryFolderCount;
    }

    public Integer getSkipCount() {
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

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public Integer getArtworkCount() {
        return artworkCount;
    }

    public String getSortName() {
        return sortName;
    }

    public String getComments() {
        return comments;
    }

    public Integer getNormalization() {
        return normalization;
    }

    public Integer getBpm() {
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

    public Integer getDiscNumber() {
        return discNumber;
    }

    public Integer getDiscCount() {
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

    public Integer getVolumeAdjustment() {
        return volumeAdjustment;
    }

    public boolean isCompilation() {
        return compilation;
    }

    public boolean isPartOfGaplessAlbum() {
        return partOfGaplessAlbum;
    }

    private final Integer trackId;
    private final String name;
    private final String artist;
    private final String kind;
    private final Integer size; // bytes

    private final Integer totalTime; // seconds

    private final LocalDateTime dateModified;
    private final LocalDateTime dateAdded;

    private final Integer bitRate;

    private final Integer sampleRate;

    private final Integer playCount;

    private final Long playDate;

    private final LocalDateTime playDateUTC;

    private final String persistentID;

    private final String trackType;

    private final Path location;


    private final Integer fileFolderCount;

    private final Integer libraryFolderCount;

    private final Integer skipCount;

    private final LocalDateTime skipDate;


    private final String albumArtist;

    private final String composer;

    private final String album;
    private final String genre;
    private final Integer trackNumber;
    private final Integer year;

    private final Integer trackCount;

    private final Integer artworkCount;

    private final String sortName;

    private final String comments;

    private final Integer normalization;

    private final Integer bpm;

    private final String sortAlbum;
    private final String sortAlbumArtist;
    private final String sortArtist;
    private final Integer discNumber;
    private final Integer discCount;

    private final String grouping;

    private final String work;

    private final String sortComposer;

    private final Integer volumeAdjustment;

    private final boolean compilation;

    private final boolean partOfGaplessAlbum;

    private Song(Integer trackId, String name, String artist, String kind, Integer size, Integer totalTime, LocalDateTime dateModified,
                 LocalDateTime dateAdded, Integer bitRate, Integer sampleRate, Integer playCount, Long playDate, LocalDateTime playDateUTC,
                 String persistentID, String trackType, Path location, Integer fileFolderCount, Integer libraryFolderCount,
                 Integer skipCount, LocalDateTime skipDate, String albumArtist, String composer, String album, String genre,
                 Integer trackNumber, Integer year, Integer trackCount, Integer artworkCount, String sortName, String comments,
                 Integer normalization, Integer bpm, String sortAlbum, String sortAlbumArtist, String sortArtist,
                 Integer discNumber, Integer discCount, String grouping, String work, String sortComposer, Integer volumeAdjustment,
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
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    private static void append(StringBuilder builder, String name, Object value) {
        if(value != null) {
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
            builder.append(name).append(": true\n");
        }
    }

    private static void appendAsTime(StringBuilder builder, String name, Integer time) {
        if(time != null) {
            builder.append(name).append(": ").append(new Time(time)).append("\n");
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
        append(builder, "Location", location);
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

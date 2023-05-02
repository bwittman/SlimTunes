package slimtunes.model;

import slimtunes.model.xml.WriteXML;
import slimtunes.model.xml.Writer;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class Song extends WriteXML {



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

    public void addField(String key, String value) {
        Fields field = Fields.valueOf(Fields.nameToValue(key.trim()));
        switch (field) {
            case TRACK_ID -> trackId = Integer.parseInt(value);
            case NAME -> name = value;
            case ARTIST -> artist = value;
            case KIND -> kind = value;
            case SIZE -> size = Integer.parseInt(value);
            case TOTAL_TIME -> totalTime = Integer.parseInt(value);
            case DATE_MODIFIED -> dateModified = Library.parseDate(value);
            case DATE_ADDED -> dateAdded = Library.parseDate(value);
            case BIT_RATE -> bitRate = Integer.parseInt(value);
            case SAMPLE_RATE -> sampleRate = Integer.parseInt(value);
            case PLAY_COUNT -> playCount = Integer.parseInt(value);
            case PLAY_DATE -> playDate = Long.parseLong(value);
            case PLAY_DATE_UTC -> playDateUTC = Library.parseDate(value);
            case PERSISTENT_ID -> persistentID = value;
            case TRACK_TYPE -> trackType = value;
            case LOCATION -> location = Library.stringToPath(value);
            case FILE_FOLDER_COUNT -> fileFolderCount = Integer.parseInt(value);
            case LIBRARY_FOLDER_COUNT -> libraryFolderCount = Integer.parseInt(value);
            case SKIP_COUNT -> skipCount = Integer.parseInt(value);
            case SKIP_DATE -> skipDate = Library.parseDate(value);
            case ALBUM_ARTIST -> albumArtist = value;
            case COMPOSER -> composer = value;
            case ALBUM -> album = value;
            case GENRE -> genre = value;
            case TRACK_NUMBER -> trackNumber = Integer.parseInt(value);
            case YEAR -> year = Integer.parseInt(value);
            case TRACK_COUNT -> trackCount = Integer.parseInt(value);
            case ARTWORK_COUNT -> artworkCount = Integer.parseInt(value);
            case SORT_NAME -> sortName = value;
            case COMMENTS -> comments = value.replaceAll("\n", System.lineSeparator());
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
    }

    private Integer trackId;
    private String name;
    private String artist;
    private String kind;
    private Integer size; // bytes

    private Integer totalTime; // seconds

    private LocalDateTime dateModified;
    private LocalDateTime dateAdded;

    private Integer bitRate;

    private Integer sampleRate;

    private Integer playCount;

    private Long playDate;

    private LocalDateTime playDateUTC;

    private String persistentID;

    private String trackType;

    private Path location;


    private Integer fileFolderCount;

    private Integer libraryFolderCount;

    private Integer skipCount;

    private LocalDateTime skipDate;


    private String albumArtist;

    private String composer;

    private String album;
    private String genre;
    private Integer trackNumber;
    private Integer year;

    private Integer trackCount;

    private Integer artworkCount;

    private String sortName;

    private String comments;

    private Integer normalization;

    private Integer bpm;

    private String sortAlbum;
    private String sortAlbumArtist;
    private String sortArtist;
    private Integer discNumber;
    private Integer discCount;

    private String grouping;

    private String work;

    private String sortComposer;

    private Integer volumeAdjustment;

    private Boolean compilation;

    private Boolean partOfGaplessAlbum;

    private static void append(StringBuilder builder, String name, Object value) {
        if(value != null) {
            builder.append(name).append(": ").append(value).append("\n");
        }
    }

    private static void append(StringBuilder builder, String name, LocalDateTime dateTime) {
        if(dateTime != null) {
            builder.append(name).append(": ").append(Library.formatDate(dateTime)).append("\n");
        }
    }

    private static void appendAsTime(StringBuilder builder, String name, Integer time) {
        if(time != null) {
            builder.append(name).append(": ").append(new Time(time)).append("\n");
        }
    }


    public Integer getTrackId() {
        return trackId;
    }

    public String getArtist() {
        return artist;
    }


    public String getName() {
        return name;
    }


    public Integer getTotalTime() {
        return totalTime;
    }

    public Integer getBitRate() {
        return bitRate;
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

    public void write(Writer writer) {
        write(writer, Fields.TRACK_ID, trackId);
        write(writer, Fields.NAME, name);
        write(writer, Fields.ARTIST, artist);

        write(writer, Fields.ALBUM_ARTIST, albumArtist); // optional
        write(writer, Fields.COMPOSER, composer); // optional
        write(writer, Fields.ALBUM, album); // optional
        write(writer, Fields.GROUPING, grouping); // optional
        write(writer, Fields.WORK, work); // optional
        write(writer, Fields.GENRE, genre); // optional

        write(writer, Fields.KIND, kind);
        write(writer, Fields.SIZE, size);
        write(writer, Fields.TOTAL_TIME, totalTime);

        write(writer, Fields.DISC_NUMBER, discNumber); // optional
        write(writer, Fields.DISC_COUNT, discCount); // optional

        write(writer, Fields.TRACK_NUMBER, trackNumber); // optional
        write(writer, Fields.TRACK_COUNT, trackCount); // optional
        write(writer, Fields.YEAR, year); // optional
        write(writer, Fields.BPM, bpm); // optional

        write(writer, Fields.DATE_MODIFIED, dateModified);
        write(writer, Fields.DATE_ADDED, dateAdded);
        write(writer, Fields.BIT_RATE, bitRate);
        write(writer, Fields.SAMPLE_RATE, sampleRate);

        write(writer, Fields.VOLUME_ADJUSTMENT, volumeAdjustment); // optional
        write(writer, Fields.PART_OF_GAPLESS_ALBUM, partOfGaplessAlbum); // optional

        write(writer,Fields.COMMENTS, comments); // optional
        //if (comments != null)
          //  writer.keyMultilineString(Fields.COMMENTS.toString(), comments); // optional

        write(writer, Fields.PLAY_COUNT, playCount);
        write(writer, Fields.PLAY_DATE, playDate);
        write(writer, Fields.PLAY_DATE_UTC, playDateUTC);

        write(writer, Fields.SKIP_COUNT, skipCount); // optional
        write(writer, Fields.SKIP_DATE, skipDate); // optional
        write(writer, Fields.NORMALIZATION, normalization); // optional
        write(writer, Fields.COMPILATION, compilation); // optional
        write(writer, Fields.ARTWORK_COUNT, artworkCount); // optional
        write(writer, Fields.SORT_ALBUM, sortAlbum); // optional
        write(writer, Fields.SORT_ALBUM_ARTIST, sortAlbumArtist); // optional
        write(writer, Fields.SORT_ARTIST, sortArtist); // optional
        write(writer, Fields.SORT_COMPOSER, sortComposer); // optional
        write(writer, Fields.SORT_NAME, sortName); // optional

        write(writer, Fields.PERSISTENT_ID, persistentID);
        write(writer, Fields.TRACK_TYPE, trackType);
        write(writer, Fields.LOCATION, location);
        write(writer, Fields.FILE_FOLDER_COUNT, fileFolderCount);
        write(writer, Fields.LIBRARY_FOLDER_COUNT, libraryFolderCount);
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

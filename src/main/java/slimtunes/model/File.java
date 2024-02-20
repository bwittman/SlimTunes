package slimtunes.model;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import slimtunes.model.xml.WriteXML;
import slimtunes.model.xml.Writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class File extends WriteXML {



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

    private static final Random RANDOM = new Random();

    public static File createFile(Path path) {
        try {
            AudioFile audioFile = AudioFileIO.read(path.toFile());
            Tag tag = audioFile.getTag();
            AudioHeader audioHeader = audioFile.getAudioHeader();
            String fileName = path.getFileName().toString().toLowerCase();
            String extension = "";
            if (fileName.lastIndexOf('.') != -1)
                extension = fileName.substring(fileName.lastIndexOf('.'));

            File file = new File();
            file.addField(Fields.NAME, tag.getFirst(FieldKey.TITLE));
            file.addField(Fields.ARTIST, tag.getFirst(FieldKey.ARTIST));

            String kind = switch (extension) {
                case ".aac", ".m4a", ".mp4", ".m4r" -> "Purchased AAC audio file";
                case ".m4b", ".m4p" -> "Protected AAC audio file";
                case ".m4v" -> "Protected MPEG-4 video file";
                case ".aiff", ".aif", ".aifc" -> "AIFF audio file"; // Non-standard iTunes
                case ".dsf" -> "DSD stream file"; // Non-standard iTunes
                case ".flac" -> "FLAC audio file"; // Non-standard iTunes
                case ".mov" -> "QuickTime movie file";
                case ".mp3" -> "MPEG audio file";
                case ".oga", ".ogg", ".ogx" -> "Ogg audio file"; // Non-standard iTunes
                case ".wav", ".wave" -> "WAV audio file"; // Non-standard iTunes
                case ".wma" -> "Windows Media audio file"; // Non-standard iTunes
                default -> null;
            };
            file.addField(Fields.KIND, kind);
            file.addField(Fields.SIZE, Files.size(path));
            file.addField(Fields.TOTAL_TIME, audioHeader.getTrackLength());
            file.addField(Fields.DATE_MODIFIED, Library.formatDate(LocalDateTime.ofEpochSecond(
                    Files.getLastModifiedTime(path).to(TimeUnit.SECONDS), 0, ZoneOffset.UTC)));
            file.addField(Fields.DATE_ADDED, Library.formatDate(LocalDateTime.now()));
            file.addField(Fields.BIT_RATE, audioHeader.getBitRate());
            file.addField(Fields.SAMPLE_RATE, audioHeader.getSampleRate());
            // Skip: PLAY_COUNT, PLAY_DATE, PLAY_DATE_UTC
            file.addField(Fields.PERSISTENT_ID, String.format("%016X", RANDOM.nextLong()));
            file.addField(Fields.TRACK_TYPE, "File");
            file.addField(Fields.LOCATION, Library.pathToString(path));
            file.addField(Fields.FILE_FOLDER_COUNT, -1);
            file.addField(Fields.LIBRARY_FOLDER_COUNT, -1);
            // Skip: SKIP_COUNT, SKIP_DATE
            file.addField(Fields.ALBUM_ARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST));
            file.addField(Fields.COMPOSER, tag.getFirst(FieldKey.COMPOSER));
            file.addField(Fields.ALBUM, tag.getFirst(FieldKey.ALBUM));
            file.addField(Fields.GENRE, tag.getFirst(FieldKey.GENRE));
            file.addField(Fields.TRACK_NUMBER, tag.getFirst(FieldKey.TRACK));
            file.addField(Fields.YEAR, tag.getFirst(FieldKey.YEAR));
            file.addField(Fields.TRACK_COUNT, tag.getFirst(FieldKey.TRACK_TOTAL));
            // Skip: ARTWORK_COUNT
            file.addField(Fields.SORT_NAME, tag.getFirst(FieldKey.TITLE_SORT));
            file.addField(Fields.COMMENTS, tag.getFirst(FieldKey.COMMENT));
            // Skip: NORMALIZATION
            file.addField(Fields.BPM, tag.getFirst(FieldKey.BPM));
            file.addField(Fields.SORT_ALBUM, tag.getFirst(FieldKey.ALBUM_SORT));
            file.addField(Fields.SORT_ALBUM_ARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST_SORT));
            file.addField(Fields.SORT_ARTIST, tag.getFirst(FieldKey.ARTIST_SORT));
            file.addField(Fields.DISC_NUMBER, tag.getFirst(FieldKey.DISC_NO));
            file.addField(Fields.DISC_COUNT, tag.getFirst(FieldKey.DISC_TOTAL));
            file.addField(Fields.GROUPING, tag.getFirst(FieldKey.GROUPING));
            file.addField(Fields.WORK, tag.getFirst(FieldKey.WORK));
            file.addField(Fields.SORT_COMPOSER, tag.getFirst(FieldKey.COMPOSER_SORT));
            // Skip: VOLUME_ADJUSTMENT
            file.addField(Fields.COMPILATION, tag.getFirst(FieldKey.IS_COMPILATION));
            // Skip: PART_OF_GAPLESS_ALBUM

            return file;
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            return null;
        }
    }

/*
    public static File createMP3FromMetadata(Metadata metadata, int trackId) {
        File file = new File();
        file.addField(Fields.TRACK_ID, "" + trackId);
        file.addField(Fields.NAME, metadata.get(TikaCoreProperties.TITLE));
        file.addField(Fields.ARTIST, metadata.get(XMPDM.ARTIST));
        //TODO: "Kind",
        //TODO: "Size"

        file.addField(Fields.TOTAL_TIME, metadata.get(XMPDM.DURATION));
        file.addField(Fields.DATE_MODIFIED, metadata.get(XMPDM.AUDIO_MOD_DATE));
        file.addField(Fields.DATE_ADDED, Library.formatDate(LocalDateTime.now()));

        "Bit Rate",

                file.addField(Fields.SAMPLE_RATE, metadata.get(XMPDM.AUDIO_SAMPLE_RATE));
          "Play Count", "Play Date", "Play Date UTC", "Persistent ID", "Track Type", "Location", "File Folder Count", "Library Folder Count", "Skip Count", "Skip Date", "Album Artist", "Composer", "Album", "Genre", "Track Number", "Year", "Track Count", "Artwork Count", "Sort Name", "Comments", "Normalization", "BPM", "Sort Album", "Sort Album Artist", "Sort Artist", "Disc Number", "Disc Count", "Grouping", "Work", "Sort Composer", "Volume Adjustment", "Compilation", "Part Of Gapless Album"};


        TikaCoreProperties
        static Property	ALTITUDE
        static Property	COMMENTS
        static Property	CONTENT_TYPE_HINT
        This is currently used to identify Content-Type that may be included within a document, such as in html documents (e.g.
        static Property	CONTRIBUTOR
        static Property	COVERAGE
        static Property	CREATED
        static Property	CREATOR
        static Property	CREATOR_TOOL
        static Property	DESCRIPTION
        static Property	EMBEDDED_RESOURCE_TYPE
        See EMBEDDED_RESOURCE_TYPE
        static Property	FORMAT
        static Property	IDENTIFIER
        static Property	KEYWORDS
        static Property	LANGUAGE
        static Property	LATITUDE
        static Property	LONGITUDE
        static Property	METADATA_DATE
        static Property	MODIFIED
        static Property	MODIFIER
        static Property	PRINT_DATE
        static Property	PUBLISHER
        static Property	RATING
        static Property	RELATION
        static Property	RIGHTS
        static Property	SOURCE


        XMPDM
        static Property	ABS_PEAK_AUDIO_FILE_PATH
"The absolute path to the file's peak audio file.
static Property	ALBUM
"The name of the album."
static Property	ALBUM_ARTIST
"The name of the album artist or group for compilation albums."
static Property	ALT_TAPE_NAME
"An alternative tape name, set via the project window or timecode dialog in Premiere.
static Property	ARTIST
"The name of the artist or artists."
static Property	AUDIO_CHANNEL_TYPE
"The audio channel type."
static Property	AUDIO_COMPRESSOR
"The audio compression used.
static Property	AUDIO_MOD_DATE
"The date and time when the audio was last modified."
static Property	AUDIO_SAMPLE_RATE
"The audio sample rate.
static Property	AUDIO_SAMPLE_TYPE
"The audio sample type."
static Property	COMPILATION
"An album created by various artists."
static Property	COMPOSER
"The composer's name."
static Property	COPYRIGHT
"The copyright information."
static Property	DISC_NUMBER
"The disc number for part of an album set."
static Property	DURATION
"The duration of the media file." Value is in Seconds, unless xmpDM:scale is also set.
static Property	ENGINEER
"The engineer's name."
static Property	FILE_DATA_RATE
"The file data rate in megabytes per second.
static Property	GENRE
"The name of the genre."
static Property	INSTRUMENT
"The musical instrument."
static Property	KEY
"The audio's musical key."
static Property	LOG_COMMENT
"User's log comments."
static Property	LOOP
"When true, the clip can be looped seamlessly."
static Property	METADATA_MOD_DATE
"The date and time when the metadata was last modified."
static Property	NUMBER_OF_BEATS
"The number of beats."
static Property	PULL_DOWN
"The sampling phase of film to be converted to video (pull-down)."
static Property	RELATIVE_PEAK_AUDIO_FILE_PATH
"The relative path to the file's peak audio file.
static Property	RELEASE_DATE
"The date the title was released."
static Property	SCALE_TYPE
"The musical scale used in the music.
static Property	SCENE
"The name of the scene."
static Property	SHOT_DATE
"The date and time when the video was shot."
static Property	SHOT_LOCATION
"The name of the location where the video was shot.
static Property	SHOT_NAME
"The name of the shot or take."
static Property	SPEAKER_PLACEMENT
"A description of the speaker angles from center front in degrees.
static Property	STRETCH_MODE
"The audio stretch mode."
static Property	TAPE_NAME
"The name of the tape from which the clip was captured, as set during the capture process."
static Property	TEMPO
"The audio's tempo."
static Property	TIME_SIGNATURE
"The time signature of the music."
static Property	TRACK_NUMBER
"A numeric value indicating the order of the audio file within its original recording."
static Property	VIDEO_ALPHA_MODE
"The alpha mode."
static Property	VIDEO_ALPHA_UNITY_IS_TRANSPARENT
"When true, unity is clear, when false, it is opaque."
static Property	VIDEO_COLOR_SPACE
"The color space."
static Property	VIDEO_COMPRESSOR
"Video compression used.
static Property	VIDEO_FIELD_ORDER
"The field order for video."
static Property	VIDEO_FRAME_RATE
"The video frame rate."
static Property	VIDEO_MOD_DATE
"The date and time when the video was last modified."
static Property	VIDEO_PIXEL_ASPECT_RATIO
"The aspect ratio, expressed as wd/ht.
static Property	VIDEO_PIXEL_DEPTH


        return file;
    }

 */

    private void addField(Fields field, long value) {
        addField(field, "" + value);
    }

    public void addField(Fields field, String value) {
        if (value == null)
            return;
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

    public void addField(String key, String value) {
        addField(Fields.valueOf(Fields.nameToValue(key.trim())), value);
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
        return artist + " - " + name;
    }

    public String getInformation() {
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

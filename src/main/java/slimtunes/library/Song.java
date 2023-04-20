package slimtunes.library;

import java.nio.file.Path;
import java.util.Date;

public class Song {
    public enum Fields {
        TRACK_ID, NAME, ARTIST, KIND, SIZE, TOTAL_TIME, DATE_MODIFIED, DATE_ADDED, BIT_RATE, SAMPLE_RATE, PLAY_COUNT, PLAY_DATE, PLAY_DATE_UTC, PERSISTENT_ID, TRACK_TYPE, LOCATION, FILE_FOLDER_COUNT, LIBRARY_FOLDER_COUNT, SKIP_COUNT, SKIP_DATE, ALBUM_ARTIST, COMPOSER, ALBUM, GENRE, TRACK_NUMBER, YEAR, TRACK_COUNT, ARTWORK_COUNT, SORT_NAME, COMMENTS, NORMALIZATION, BPM, SORT_ALBUM, SORT_ALBUM_ARTIST, SORT_ARTIST, DISC_NUMBER, DISC_COUNT, GROUPING, WORK, SORT_COMPOSER, VOLUME_ADJUSTMENT, COMPILATION, PART_OF_GAPLESS_ALBUM;

        private static String fix(String input) {
            if(input.equals("ID") || input.equals("BPM") || input.equals("UTC"))
                return input;
            return input.charAt(0) + input.substring(1).toLowerCase();
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (String part : name().split("_")) {
                if (first)
                    first = false;
                else
                    result.append(" ");
                result.append(fix(part));
            }
            return result.toString();
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

    public Date getDateModified() {
        return dateModified;
    }

    public Date getDateAdded() {
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

    public Date getPlayDate() {
        return playDate;
    }

    public int getPersistentID() {
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

    public Date getSkipDate() {
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

    private final int trackId;
    private final String name;
    private final String artist;
    private final String kind;
    private final int size; // bytes

    private final int totalTime; // seconds

    private final Date dateModified;
    private final Date dateAdded;

    private final int bitRate;

    private final int sampleRate;

    private final int playCount;

    private final Date playDate;

    private final int persistentID;

    private final String trackType;

    private final Path location;


    private final int fileFolderCount;

    private final int libraryFolderCount;

    private final int skipCount;

    private final Date skipDate;


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

    public Song(int trackId, String name, String artist, String kind, int size, int totalTime, Date dateModified, Date dateAdded, int bitRate, int sampleRate, int playCount, Date playDate, int persistentID, String trackType, Path location, int fileFolderCount, int libraryFolderCount, int skipCount, Date skipDate, String albumArtist, String composer, String album, String genre, int trackNumber, int year, int trackCount, int artworkCount, String sortName, String comments, int normalization, int bpm, String sortAlbum, String sortAlbumArtist, String sortArtist, int discNumber, int discCount, String grouping, String work, String sortComposer, int volumeAdjustment, boolean compilation, boolean partOfGaplessAlbum) {
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
}

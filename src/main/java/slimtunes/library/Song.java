package slimtunes.library;

import java.nio.file.Path;

public class Song {
    public enum Fields {
        ID, NAME, ARTIST, ALBUM, GENRE, SIZE, TIME, DISC, DISC_COUNT, TRACK, TRACK_COUNT, YEAR, BIT_RATE, SAMPLE_RATE, PATH;

        private static String fix(String input) {
            return input.charAt(0) + input.substring(1).toLowerCase();
        }

        public String toString() {
            if (this == ID)
                return "ID";

            String name = name();
            if (name.contains("_")) {
                StringBuilder result = new StringBuilder();
                boolean first = true;
                for (String part : name.split("_")) {
                    if (first)
                        first = false;
                    else
                        result.append(" ");
                    result.append(fix(part));
                }
                return result.toString();
            }
            else
                return fix(name);
        }

    }
    private final int id;
    private final String name;
    private final String artist;
    private final String album;
    private final String genre;
    private final int size; // bytes
    private final int time; // seconds
    private final int disc;
    private final int discCount;
    private final int track;
    private final int trackCount;
    private final int year;
    private final int bitRate;
    private final int sampleRate;
    private final Path path;

    public Song(int id, String name, String artist, String album, String genre, int size, int time, int disc, int discCount, int track, int trackCount, int year, int bitRate, int sampleRate, Path path) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.size = size;
        this.time = time;
        this.disc = disc;
        this.discCount = discCount;
        this.track = track;
        this.trackCount = trackCount;
        this.year = year;
        this.bitRate = bitRate;
        this.sampleRate = sampleRate;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public int getSize() {
        return size;
    }

    public int getTime() {
        return time;
    }

    public int getDisc() {
        return disc;
    }

    public int getDiscCount() {
        return discCount;
    }

    public int getTrack() {
        return track;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public int getYear() {
        return year;
    }

    public int getBitRate() {
        return bitRate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public Path getPath() {
        return path;
    }
}

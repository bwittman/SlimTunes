package slimtunes.gui;

import slimtunes.library.Song;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class SongTableModel extends AbstractTableModel {
    List<Song> songs;

    public SongTableModel(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public int getRowCount() {
        return songs.size();
    }

    @Override
    public int getColumnCount() {
        return Song.Fields.values().length;
    }

    private static String secondsToTime(int seconds) {
        if (seconds == -1)
            return "";

        int hours = seconds / 3600;
        int minutes = seconds % 3600 / 60;
        seconds = seconds % 60;

        if (hours >= 1)
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%d:%02d", minutes, seconds);
    }

    private static String clean(Object input) {
        if (input == null)
            return "";
        else if (input instanceof Integer) {
            int value = (int) input;
            if (value == -1)
                return "";
            else
                return input.toString();
        }
        else
            return input.toString();
    }

    @Override
    public String getColumnName(int column) {
        if (column < 0 || column >= getColumnCount())
            return "";

        return Song.Fields.values()[column].toString();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= songs.size() || columnIndex < 0 || columnIndex >= getColumnCount())
            return null;

        Song song = songs.get(rowIndex);
        return clean(switch (Song.Fields.values()[columnIndex]) {
            case ID -> song.getId();
            case NAME -> song.getName();
            case ARTIST -> song.getArtist();
            case ALBUM -> song.getAlbum();
            case GENRE -> song.getGenre();
            case SIZE -> song.getSize();
            case TIME -> secondsToTime(song.getTime());
            case DISC -> song.getDisc();
            case DISC_COUNT -> song.getDiscCount();
            case TRACK -> song.getTrack();
            case TRACK_COUNT -> song.getTrackCount();
            case YEAR -> song.getYear();
            case BIT_RATE -> song.getBitRate();
            case SAMPLE_RATE -> song.getSampleRate();
            case PATH -> song.getPath();
        });
    }
}

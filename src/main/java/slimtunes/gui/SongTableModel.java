package slimtunes.gui;

import slimtunes.library.Song;
import javax.swing.table.AbstractTableModel;
import java.util.List;

import static slimtunes.library.Song.Fields;

public class SongTableModel extends AbstractTableModel {
    List<Song> songs;

    private static final Song.Fields[] COLUMNS = new Fields[] {
            Song.Fields.TRACK_ID, Song.Fields.NAME, Song.Fields.ARTIST, Song.Fields.SIZE, Song.Fields.TOTAL_TIME, Song.Fields.BIT_RATE, Song.Fields.LOCATION, Song.Fields.ALBUM, Song.Fields.GENRE, Song.Fields.TRACK_NUMBER, Song.Fields.YEAR
    };

    public SongTableModel(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public int getRowCount() {
        return songs.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    private static String millisecondsToTime(int milliseconds) {
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

        return COLUMNS[column].toString();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= songs.size() || columnIndex < 0 || columnIndex >= getColumnCount())
            return null;

        Song song = songs.get(rowIndex);
        return clean(switch(COLUMNS[columnIndex]){
            case TRACK_ID -> song.getTrackId();
            case NAME -> song.getName();
            case ARTIST -> song.getArtist();
            case SIZE -> song.getSize();
            case TOTAL_TIME -> millisecondsToTime(song.getTotalTime());
            case BIT_RATE -> song.getBitRate();
            case LOCATION -> song.getLocation();
            case ALBUM -> song.getAlbum();
            case GENRE -> song.getGenre();
            case TRACK_NUMBER -> song.getTrackNumber();
            case YEAR -> song.getYear();
            default -> "";
        });
    }
}

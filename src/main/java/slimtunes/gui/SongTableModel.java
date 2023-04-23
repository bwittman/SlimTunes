package slimtunes.gui;

import slimtunes.library.Song;
import javax.swing.table.AbstractTableModel;
import java.util.List;

import static slimtunes.library.Song.Fields;

public class SongTableModel extends AbstractTableModel {
    List<Song> songs;

    private static final Song.Fields[] COLUMNS = new Fields[] {
            Song.Fields.TRACK_ID, Song.Fields.NAME, Song.Fields.ARTIST, Song.Fields.SIZE, Song.Fields.TOTAL_TIME, Song.Fields.BIT_RATE, Song.Fields.ALBUM, Song.Fields.GENRE, Song.Fields.TRACK_NUMBER, Song.Fields.YEAR
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

        if (column == 0)
            return "Number";

        return COLUMNS[column].toString();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= songs.size() || columnIndex < 0 || columnIndex >= getColumnCount())
            return null;

        Song song = songs.get(rowIndex);
        return clean(switch(COLUMNS[columnIndex]){
            case TRACK_ID -> rowIndex + 1;
            case NAME -> song.getName();
            case ARTIST -> song.getArtist();
            case SIZE -> song.getSize();
            case TOTAL_TIME -> Song.millisecondsToTime(song.getTotalTime());
            case BIT_RATE -> song.getBitRate();
            case ALBUM -> song.getAlbum();
            case GENRE -> song.getGenre();
            case TRACK_NUMBER -> song.getTrackNumber();
            case YEAR -> song.getYear();
            default -> "";
        });
    }
}

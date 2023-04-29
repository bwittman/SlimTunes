package slimtunes.gui;

import slimtunes.library.Song;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.time.LocalTime;
import java.util.List;

import static slimtunes.library.Song.Fields;

public class SongTableModel extends AbstractTableModel {
    List<Song> songs;

    private static final Song.Fields[] COLUMNS = new Fields[] {
            Song.Fields.TRACK_ID, Song.Fields.NAME, Song.Fields.ARTIST, Song.Fields.ALBUM, Song.Fields.TRACK_NUMBER, Song.Fields.TOTAL_TIME, Song.Fields.BIT_RATE, Song.Fields.YEAR, Song.Fields.GENRE,
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

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return switch (COLUMNS[columnIndex]) {
            case TRACK_ID, TRACK_NUMBER, BIT_RATE, YEAR -> Integer.class;
            case TOTAL_TIME -> LocalTime.class;
            case NAME, ARTIST, ALBUM, GENRE -> String.class;
            default -> Object.class;
        };
    }

    public static void setWidths(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
      for (int i = 0; i < COLUMNS.length; ++i) {
          TableColumn column = table.getColumnModel().getColumn(i);
          switch (COLUMNS[i]) {
              case TRACK_ID -> {
                  column.setPreferredWidth(30);
                  column.setMinWidth(30);
                  column.setMaxWidth(30);
                  column.setCellRenderer(right);
              }
              case TOTAL_TIME -> {
                  column.setPreferredWidth(35);
                  column.setMinWidth(35);
                  column.setMaxWidth(45); // In case of songs longer than an hour
                  column.setCellRenderer(center);
                  column.setHeaderRenderer(center);
              }
              case BIT_RATE, YEAR -> {
                  column.setPreferredWidth(35);
                  column.setMinWidth(35);
                  column.setMaxWidth(35);
                  column.setCellRenderer(center);
                  column.setHeaderRenderer(center);
              }
              case TRACK_NUMBER -> {
                  column.setPreferredWidth(25);
                  column.setMaxWidth(30);
                  column.setCellRenderer(center);
                  column.setHeaderRenderer(center);
              }
          }
      }
    }

    @Override
    public String getColumnName(int column) {
        if (column < 0 || column >= getColumnCount())
            return "";

        return switch(COLUMNS[column]){
            case NAME -> "Name";
            case ARTIST -> "Artist";
            case TOTAL_TIME -> "Time";
            case BIT_RATE -> "Rate";
            case ALBUM -> "Album";
            case GENRE -> "Genre";
            case TRACK_NUMBER -> "#";
            case YEAR -> "Year";
            default -> "";
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= songs.size() || columnIndex < 0 || columnIndex >= getColumnCount())
            return null;

        Song song = songs.get(rowIndex);
        return switch(COLUMNS[columnIndex]){
            case TRACK_ID -> rowIndex + 1;
            case NAME -> song.getName();
            case ARTIST -> song.getArtist();
            case TOTAL_TIME -> song.getTotalTime() == null ? null : new Time(song.getTotalTime());
            case BIT_RATE -> song.getBitRate();
            case ALBUM -> song.getAlbum();
            case GENRE -> song.getGenre();
            case TRACK_NUMBER -> song.getTrackNumber();
            case YEAR -> song.getYear();
            default -> "";
        };
    }
}

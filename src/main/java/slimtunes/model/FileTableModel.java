package slimtunes.model;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.time.LocalTime;
import java.util.List;

import static slimtunes.model.File.Fields;

public class FileTableModel extends AbstractTableModel {
    List<File> files;

    private static final File.Fields[] COLUMNS = new Fields[] {
            File.Fields.TRACK_ID, File.Fields.NAME, File.Fields.ARTIST, File.Fields.ALBUM, File.Fields.TRACK_NUMBER, File.Fields.TOTAL_TIME, File.Fields.BIT_RATE, File.Fields.YEAR, File.Fields.GENRE,
    };

    public FileTableModel(List<File> files) {
        this.files = files;
    }

    @Override
    public int getRowCount() {
        return files.size();
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
                  column.setMaxWidth(45); // In case of files longer than an hour
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
        if (rowIndex < 0 || rowIndex >= files.size() || columnIndex < 0 || columnIndex >= getColumnCount())
            return null;

        File file = files.get(rowIndex);
        return switch(COLUMNS[columnIndex]){
            case TRACK_ID -> rowIndex + 1;
            case NAME -> file.getName();
            case ARTIST -> file.getArtist();
            case TOTAL_TIME -> file.getTotalTime() == null ? null : new Time(file.getTotalTime());
            case BIT_RATE -> file.getBitRate();
            case ALBUM -> file.getAlbum();
            case GENRE -> file.getGenre();
            case TRACK_NUMBER -> file.getTrackNumber();
            case YEAR -> file.getYear();
            default -> "";
        };
    }
}

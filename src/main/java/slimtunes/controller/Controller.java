package slimtunes.controller;

import org.xml.sax.SAXException;
import slimtunes.model.Library;
import slimtunes.model.Playlist;
import slimtunes.model.Song;
import slimtunes.model.SongTableModel;
import slimtunes.model.xml.Reader;
import slimtunes.view.SlimTunes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Controller {

    private final SlimTunes slimTunes;
    private Library library;

    private boolean changed = false;

    final JFileChooser chooser = new JFileChooser();
    public Controller(SlimTunes slimTunes) {
        this.slimTunes = slimTunes;
        this.library = new Library();

        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file == null)
                    return false;
                return file.isDirectory() || file.toString().toLowerCase().endsWith(".xml");
            }

            @Override
            public String getDescription() {
                return "XML Files (*.xml)";
            }
        });

        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);


        setPlaylistSelection();
        setSongSelection();
        setMenus();
        setSearchBar();
    }

    private void setSearchBar() {
        JTextField searchBar = slimTunes.getSearchBar();
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }
        });
    }

    private void search() {
        if(library.getSongs().size() > 0) {
            RowFilter<SongTableModel, Object> filter = null;
            String text = slimTunes.getSearchBar().getText().trim().toLowerCase();
            if (!text.isEmpty()) {
                filter = new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends SongTableModel, ?> entry) {
                        return entry.getStringValue(Song.Fields.NAME.ordinal()).toLowerCase().contains(text) ||
                                entry.getStringValue(Song.Fields.ARTIST.ordinal()).toLowerCase().contains(text) ||
                                entry.getStringValue(Song.Fields.ALBUM.ordinal()).toLowerCase().contains(text);
                    }
                };
            }

            TableRowSorter<SongTableModel> sorter = (TableRowSorter<SongTableModel>) slimTunes.getSongTable().getRowSorter();
            sorter.setRowFilter(filter);
        }
    }

    private void setMenus() {
        JMenuItem newItem = slimTunes.getNewItem();
        newItem.addActionListener(e -> setLibrary(new Library()));

        JMenuItem openItem = slimTunes.getOpenItem();
        openItem.addActionListener(e -> openFile());

        JMenuItem saveItem = slimTunes.getSaveItem();
        JMenuItem saveAsItem = slimTunes.getSaveAsItem();

        JMenuItem exitItem = slimTunes.getExitItem();
        exitItem.addActionListener(e -> slimTunes.dispose());
    }

    private void openFile() {
        int result = chooser.showOpenDialog(slimTunes);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                Library library = new Library();
                Reader reader = new Reader();
                reader.read(file.toPath(), library);

                setLibrary(library);

                //Writer writer = new Writer(Path.of("Library2.xml"));
                //library.write(writer);
                //writer.close();
            } catch (ParserConfigurationException | IOException | SAXException ignore) {
                //throw new RuntimeException(e);
            }
        }
    }


    private void setLibrary(Library library) {
        this.library = library;
        DefaultListModel<Playlist> listModel = new DefaultListModel<>();
        for (Playlist playlist : library.getPlaylists())
            listModel.addElement(playlist);
        slimTunes.getPlaylists().setModel(listModel);

        List<Song> songs = library.getSongs();
        SongTableModel songTableModel = new SongTableModel(songs);
        slimTunes.getSongTable().setModel(songTableModel);
        slimTunes.validate();

        slimTunes.getSearchBar().setText("");
    }

    private void setSongSelection() {
        JList<Playlist> playlists = slimTunes.getPlaylists();
        JTable songTable = slimTunes.getSongTable();
        JLabel songLabel = slimTunes.getSongLabel();

        songTable.getSelectionModel().addListSelectionListener(event -> {
            if (songTable.getSelectedRowCount() > 1)
                songLabel.setText(songTable.getSelectedRowCount() + " songs selected");
            else if (songTable.getSelectedRowCount() == 1) {
                int[] indices = playlists.getSelectedIndices();
                int row = songTable.convertRowIndexToModel(songTable.getSelectedRow());
                if (indices.length >= 1)
                    songLabel.setText("<html>" + playlists.getSelectedValue().getSongs().get(row).toString().replaceAll("\n", "<br/>") + "</html>");
                else
                    songLabel.setText("<html>" + library.getSongs().get(row).toString().replaceAll("\n", "<br/>") + "</html>");
            }
            else
                songLabel.setText("");
        });
    }

    private void setPlaylistSelection() {
        JList<Playlist> playlists = slimTunes.getPlaylists();
        JTable songTable = slimTunes.getSongTable();
        playlists.getSelectionModel().addListSelectionListener(event ->{
            int[] indices = playlists.getSelectedIndices();
            if (indices.length >= 1)
                songTable.setModel(new SongTableModel(playlists.getSelectedValue().getSongs()));
            else
                songTable.setModel(new SongTableModel(library.getSongs()));
            SongTableModel.setWidths(songTable);
            slimTunes.getSearchBar().setText("");
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ignore) {
        }

        SlimTunes slimTunes = new SlimTunes();
        new Controller(slimTunes);
    }
}

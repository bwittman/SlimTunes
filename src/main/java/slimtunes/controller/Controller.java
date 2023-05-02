package slimtunes.controller;

import org.xml.sax.SAXException;
import slimtunes.model.Library;
import slimtunes.model.Playlist;
import slimtunes.model.Song;
import slimtunes.model.SongTableModel;
import slimtunes.model.xml.Reader;
import slimtunes.view.SlimTunes;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Controller {

    private SlimTunes slimTunes;
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

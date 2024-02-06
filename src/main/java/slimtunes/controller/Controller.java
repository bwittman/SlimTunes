package slimtunes.controller;

import org.xml.sax.SAXException;
import slimtunes.model.*;
import slimtunes.model.xml.Reader;
import slimtunes.model.xml.Writer;
import slimtunes.view.PlaylistSelection;
import slimtunes.view.SlimTunes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final SlimTunes slimTunes;
    private Library library;

    private boolean changed = false;
    private java.io.File currentFile = null;

    final JFileChooser xmlChooser = new JFileChooser();
    final JFileChooser mediaChooser = new JFileChooser();
    public Controller() {
        slimTunes = new SlimTunes();
        library = new Library();

        xmlChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(java.io.File file) {
                if (file == null)
                    return false;
                return file.isDirectory() || file.toString().toLowerCase().endsWith(".xml");
            }

            @Override
            public String getDescription() {
                return "XML Files (*.xml)";
            }
        });

        xmlChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        addPlaylistListeners();
        addFileTableListeners();
        addMenuListeners();
        addButtonListeners();
        addSearchBarListeners();

        slimTunes.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });
    }

    private boolean safeToContinue(String message) {
        if(changed) {
            int answer = JOptionPane.showConfirmDialog(slimTunes, message, "Save Library?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.CANCEL_OPTION)
                return false;
            if (answer == JOptionPane.YES_OPTION) {
                if (currentFile != null) {
                    saveLibrary();
                }
                else if (!saveLibraryAs()) // If failed to save
                    return false;
            }
        }

        return true;
    }

    private void quit() {
        if (safeToContinue("Library has unsaved changes. Do you want to save before exiting?"))
            slimTunes.dispose();
    }

    private void addButtonListeners() {
        JButton removeFileFromLibraryButton = slimTunes.getRemoveFileFromLibraryButton();
        removeFileFromLibraryButton.addActionListener(e -> removeFileFromLibrary());

        JButton addFileToPlaylistsButton = slimTunes.getAddFileToPlaylistsButton();
        addFileToPlaylistsButton.addActionListener(e -> addFileToPlaylists());

        JButton removeFileFromPlaylistButton = slimTunes.getRemoveFileFromPlaylistButton();
        removeFileFromPlaylistButton.addActionListener(e -> removeFileFromPlaylist());
    }

    private void addSearchBarListeners() {
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
        if(library.getFiles().size() > 0) {
            RowFilter<FileTableModel, Object> filter = null;
            String text = slimTunes.getSearchBar().getText().trim().toLowerCase();
            if (!text.isEmpty()) {
                filter = new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends FileTableModel, ?> entry) {
                        return entry.getStringValue(File.Fields.NAME.ordinal()).toLowerCase().contains(text) ||
                                entry.getStringValue(File.Fields.ARTIST.ordinal()).toLowerCase().contains(text) ||
                                entry.getStringValue(File.Fields.ALBUM.ordinal()).toLowerCase().contains(text);
                    }
                };
            }

            try {
                TableRowSorter<FileTableModel> sorter = (TableRowSorter<FileTableModel>) slimTunes.getFileTable().getRowSorter();
                sorter.setRowFilter(filter);
            }
            catch (ClassCastException ignored) {}
        }
    }

    private void addMenuListeners() {
        // File menu
        slimTunes.getNewItem().addActionListener(e -> newLibrary());
        slimTunes.getOpenItem().addActionListener(e -> openFile());
        slimTunes.getSaveItem().addActionListener(e -> saveLibrary());
        slimTunes.getSaveAsItem().addActionListener(e -> saveLibraryAs());

        JMenuItem exitItem = slimTunes.getExitItem();
        exitItem.addActionListener(e -> quit());

        // Media menu
        slimTunes.getAddFileToLibraryItem().addActionListener(e -> addFileToLibrary());
        slimTunes.getRemoveFileFromLibraryItem().addActionListener(e -> removeFileFromLibrary());
        slimTunes.getAddFileToPlaylistsItem().addActionListener(e -> addFileToPlaylists());
        slimTunes.getRemoveFileFromPlaylistItem().addActionListener(e -> removeFileFromPlaylist());

        // Popup menu
        slimTunes.getRemoveFileFromLibraryPopupItem().addActionListener(e -> removeFileFromLibrary());
        slimTunes.getAddFileToPlaylistsPopupItem().addActionListener(e -> addFileToPlaylists());
        slimTunes.getRemoveFileFromPlaylistPopupItem().addActionListener(e -> removeFileFromPlaylist());
    }

    private void newLibrary() {
        if (safeToContinue("Library has unsaved changes. Do you want to save before creating a new library?")) {
            setLibrary(new Library());
            currentFile = null;
            slimTunes.getSaveAsItem().setEnabled(true);
            setChanged(false);
        }
    }

    private void removeFileFromPlaylist() {

    }

    private void addFileToPlaylists() {
        JList<FileList> playlists = slimTunes.getPlaylists();
        JTable fileTable = slimTunes.getFileTable();
        ListSelectionModel model = fileTable.getSelectionModel();
        int[] selections = model.getSelectedIndices();
        File[] files = new File[selections.length];

        for (int i = 0; i < files.length; ++i)
            files[i] = playlists.getSelectedValue().getFiles().get(fileTable.convertRowIndexToModel(selections[i]));

        PlaylistSelection playlistSelection = new PlaylistSelection(slimTunes, files, library.getPlaylists());
        playlistSelection.getCancelButton().addActionListener(e -> playlistSelection.dispose());
        playlistSelection.getDoneButton().addActionListener(e -> updatePlaylists(playlistSelection, files));
        playlistSelection.setVisible(true);
    }

    private void updatePlaylists(PlaylistSelection playlistSelection, File[] files) {
        List<Playlist> addLists = new ArrayList<>();
        List<Playlist> removeLists = new ArrayList<>();
        playlistSelection.updatePlaylists(addLists, removeLists);

        boolean changed = this.changed; // might already be changed
        for (File file : files) {
            for (Playlist playlist : addLists) {
                if (!playlist.contains(file)) {
                    playlist.addFile(file);
                    changed = true;
                }
            }

            for (Playlist playlist : removeLists) {
                changed = changed || playlist.remove(file);
            }
        }

        slimTunes.getFileTable().clearSelection();
        setChanged(changed);
        playlistSelection.dispose();
    }

    private void removeFileFromLibrary() {
        JList<FileList> playlists = slimTunes.getPlaylists();
        JTable fileTable = slimTunes.getFileTable();
        ListSelectionModel model = fileTable.getSelectionModel();
        int[] selections = model.getSelectedIndices();
        File[] files = new File[selections.length];

        String fileText = files.length == 1 ? "this file" : "these files";

        for (int i = 0; i < files.length; ++i)
            files[i] = playlists.getSelectedValue().getFiles().get(fileTable.convertRowIndexToModel(selections[i]));

        int answer = JOptionPane.showConfirmDialog(slimTunes, "Are you sure you want to remove " + fileText +
                " from the library and all playlists?", "Remove from Library?", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (answer == JOptionPane.OK_OPTION) {
            boolean changed = this.changed; // might already be changed
            for (File file : files) {
                for (Playlist playlist : library.getPlaylists())
                    changed = changed || playlist.remove(file);

                changed = changed || library.getFiles().remove(file);
            }
            setChanged(changed);
        }
    }

    private void addFileToLibrary() {
    }

    private boolean saveLibraryAs() {
        int result = xmlChooser.showSaveDialog(slimTunes);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = xmlChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xml"))
                file = new java.io.File(file.getParentFile(), file.getName() + ".xml");

            if (!file.exists() || JOptionPane.showConfirmDialog(slimTunes, "<html>The file " + file +
                    " already exists.<br/>Do you want to overwrite it?</html>", "Overwrite File?",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                save(file);
                return true;
            }
        }
        return false;
    }

    private void setChanged(boolean value) {
        String fileName = currentFile == null ? "" : " - " + currentFile;
        if (value) {
            slimTunes.setTitle(SlimTunes.TITLE + fileName + "*");
            slimTunes.getSaveItem().setEnabled(true);
        }
        else
            slimTunes.setTitle(SlimTunes.TITLE + fileName);
        changed = value;
    }

    private void saveLibrary() {
        if (changed && currentFile != null)
            save(currentFile);
    }

    private void save(java.io.File file) {
        try {
            Writer writer = new Writer(file);
            library.write(writer);
            writer.close();
            currentFile = file;
            setChanged(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(slimTunes, "Error saving file!", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFile() {
        if (safeToContinue("Library has unsaved changes. Do you want to save before opening a new library?")) {
            int result = xmlChooser.showOpenDialog(slimTunes);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = xmlChooser.getSelectedFile();
                try {
                    Library library = new Library();
                    Reader reader = new Reader();
                    reader.read(file.toPath(), library);

                    setLibrary(library);
                    currentFile = file;
                    slimTunes.getSaveAsItem().setEnabled(true);
                    setChanged(false);
                } catch (ParserConfigurationException | IOException | SAXException ignore) {
                    JOptionPane.showMessageDialog(slimTunes, "Error opening file: " + file, "Open Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    private void setLibrary(Library library) {
        this.library = library;
        DefaultListModel<FileList> listModel = new DefaultListModel<>();

        listModel.addElement(library);
        for (Playlist playlist : library.getPlaylists())
            listModel.addElement(playlist);

        slimTunes.getPlaylists().setModel(listModel);
        slimTunes.getPlaylists().setSelectedIndex(0);
        slimTunes.getAddFileToLibraryItem().setEnabled(true);
        slimTunes.revalidate();

        slimTunes.getSearchBar().setText("");
    }

    private void addFileTableListeners() {
        JTable fileTable = slimTunes.getFileTable();

        fileTable.getSelectionModel().addListSelectionListener(event -> updateStatus());

        fileTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                    doPop(e);
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    doPop(e);
            }

            private void doPop(MouseEvent e) {
                slimTunes.getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }

    private void updateStatus() {
        JList<FileList> playlists = slimTunes.getPlaylists();
        JLabel fileLabel = slimTunes.getFileLabel();
        JTable fileTable = slimTunes.getFileTable();
        int selectedRows = fileTable.getSelectedRowCount();

        int lines = 1;
        if (selectedRows > 1) {
            fileLabel.setText(fileTable.getSelectedRowCount() + " files selected");
        }
        else if (fileTable.getSelectedRowCount() == 1) {
            int row = fileTable.convertRowIndexToModel(fileTable.getSelectedRow());
            String information = playlists.getSelectedValue().getFiles().get(row).getInformation();
            lines = (int) information.codePoints().filter(ch -> ch == '\n').count();
            fileLabel.setText("<html>" + information.replaceAll("\n", "<br/>") + "</html>");
        }
        else
            fileLabel.setText("");


        FontMetrics metrics = fileLabel.getGraphics().getFontMetrics();
        Dimension dimension = fileLabel.getSize();
        dimension.height = metrics.getHeight() * (lines + 1);
        fileLabel.setPreferredSize(dimension);

        boolean value = selectedRows > 0;
        String s = selectedRows > 1 ? "s" : "";

        final String addToPlaylist = "Add File" + s + " to Playlists";
        slimTunes.getAddFileToPlaylistsItem().setText(addToPlaylist);
        slimTunes.getAddFileToPlaylistsButton().setText(addToPlaylist);
        slimTunes.getAddFileToPlaylistsPopupItem().setText(addToPlaylist);
        slimTunes.getAddFileToPlaylistsItem().setEnabled(value);
        slimTunes.getAddFileToPlaylistsButton().setEnabled(value);
        slimTunes.getAddFileToPlaylistsPopupItem().setEnabled(value);

        final String removeFromPlaylist = "Remove File" + s + " from Playlist";
        slimTunes.getRemoveFileFromPlaylistItem().setText(removeFromPlaylist);
        slimTunes.getRemoveFileFromPlaylistButton().setText(removeFromPlaylist);
        slimTunes.getRemoveFileFromPlaylistPopupItem().setText(removeFromPlaylist);
        slimTunes.getRemoveFileFromPlaylistItem().setEnabled(value && playlists.getSelectedValue() != library);
        slimTunes.getRemoveFileFromPlaylistButton().setEnabled(value && playlists.getSelectedValue() != library);
        slimTunes.getRemoveFileFromPlaylistPopupItem().setEnabled(value && playlists.getSelectedValue() != library);

        final String removeFromLibrary = "Remove File" + s + " from Library";
        slimTunes.getRemoveFileFromLibraryItem().setText(removeFromLibrary);
        slimTunes.getRemoveFileFromLibraryButton().setText(removeFromLibrary);
        slimTunes.getRemoveFileFromLibraryPopupItem().setText(removeFromPlaylist);
        slimTunes.getRemoveFileFromLibraryItem().setEnabled(value);
        slimTunes.getRemoveFileFromLibraryButton().setEnabled(value);
        slimTunes.getRemoveFileFromLibraryPopupItem().setEnabled(value);
    }

    private void addPlaylistListeners() {
        JList<FileList> playlists = slimTunes.getPlaylists();
        JTable fileTable = slimTunes.getFileTable();
        playlists.getSelectionModel().addListSelectionListener(event -> {
            fileTable.setModel(new FileTableModel(playlists.getSelectedValue().getFiles()));
            FileTableModel.setWidths(fileTable);
            slimTunes.getSearchBar().setText("");

            updateStatus();
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ignore) {
        }
        new Controller();
    }
}

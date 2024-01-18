package slimtunes.controller;

import org.xml.sax.SAXException;
import slimtunes.model.*;
import slimtunes.model.xml.Reader;
import slimtunes.model.xml.Writer;
import slimtunes.view.SlimTunes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class Controller {

    private final SlimTunes slimTunes;
    private Library library;

    private boolean changed = false;
    private java.io.File currentFile = null;

    final JFileChooser xmlChooser = new JFileChooser();
    final JFileChooser mediaChooser = new JFileChooser();
    public Controller(SlimTunes slimTunes) {
        this.slimTunes = slimTunes;
        this.library = new Library();

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

        setPlaylistSelection();
        setFileSelection();
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

    private void setMenus() {
        // File menu
        JMenuItem newItem = slimTunes.getNewItem();
        newItem.addActionListener(e -> setLibrary(new Library()));

        JMenuItem openItem = slimTunes.getOpenItem();
        openItem.addActionListener(e -> openFile());

        JMenuItem saveItem = slimTunes.getSaveItem();
        saveItem.addActionListener(e -> saveLibrary());
        JMenuItem saveAsItem = slimTunes.getSaveAsItem();
        saveAsItem.addActionListener(e -> saveLibraryAs());

        JMenuItem exitItem = slimTunes.getExitItem();
        exitItem.addActionListener(e -> slimTunes.dispose());

        // Media menu
        JMenuItem addFileToLibraryItem = slimTunes.getAddFileToLibraryItem();
        addFileToLibraryItem.addActionListener(e -> addFileToLibrary());

        JMenuItem removeFileFromLibraryItem = slimTunes.getRemoveFileFromLibraryItem();
        removeFileFromLibraryItem.addActionListener(e -> removeFileFromLibrary());

        JMenuItem addFileToPlaylistItem = slimTunes.getRemoveFileFromPlaylistItem();
        addFileToPlaylistItem.addActionListener(e -> addFileToPlaylist());

        JMenuItem removeFileFromPlaylistItem = slimTunes.getRemoveFileFromPlaylistItem();
        removeFileFromPlaylistItem.addActionListener(e -> removeFileFromPlaylist());
    }

    private void removeFileFromPlaylist() {
    }

    private void addFileToPlaylist() {
    }

    private void removeFileFromLibrary() {
    }

    private void addFileToLibrary() {
    }

    private void saveLibraryAs() {
        int result = xmlChooser.showSaveDialog(slimTunes);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = xmlChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xml"))
                file = new java.io.File(file.getParentFile(), file.getName() + ".xml");

            if (!file.exists() || JOptionPane.showConfirmDialog(slimTunes, "<html>The file " + file +
                    " already exists.<br/>Do you want to overwrite it?</html>", "Overwrite File?",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                save(file);
            }
        }
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


    private void setLibrary(Library library) {
        this.library = library;
        DefaultListModel<FileList> listModel = new DefaultListModel<>();

        listModel.addElement(library);
        for (Playlist playlist : library.getPlaylists())
            listModel.addElement(playlist);

        slimTunes.getPlaylists().setModel(listModel);
        slimTunes.getPlaylists().setSelectedIndex(0);
        slimTunes.getAddFileToLibraryItem().setEnabled(true);
        slimTunes.validate();

        slimTunes.getSearchBar().setText("");
    }

    private void setFileSelection() {
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

        if (selectedRows > 1) {
            fileLabel.setText(fileTable.getSelectedRowCount() + " files selected");
        }
        else if (fileTable.getSelectedRowCount() == 1) {
            int row = fileTable.convertRowIndexToModel(fileTable.getSelectedRow());
            fileLabel.setText("<html>" + playlists.getSelectedValue().getFiles().get(row).toString().replaceAll("\n", "<br/>") + "</html>");
        }
        else
            fileLabel.setText("");

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

    private void setPlaylistSelection() {
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

        SlimTunes slimTunes = new SlimTunes();
        new Controller(slimTunes);
    }
}

package slimtunes.view;
import slimtunes.model.FileTableModel;
import slimtunes.model.Library;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SlimTunes extends JFrame {

    public static final String TITLE = "SlimTunes";
    public static final int SPACING = 10;
    public static final int SMALL_SPACING = 2;
    private final JList<FileTableModel> playlists;
    private final JLabel informationLabel;
    private final JTable fileTable;

    private final JTextField searchBar;

    private final JMenuItem newItem;
    private final JMenuItem openItem;
    private final JMenuItem saveItem;
    private final JMenuItem saveAsItem;
    private final JMenuItem exitItem;
    private final JMenuItem undoItem;
    private final JMenuItem redoItem;
    private final JMenuItem selectPlaylistsForFilesItem;
    private final JMenuItem removeFileFromPlaylistItem;
    private final JMenuItem addFileToLibraryItem;
    private final JMenuItem removeFileFromLibraryItem;
    private final JMenuItem createPlaylistItem;
    private final JMenuItem removePlaylistItem;
    private final JMenuItem aboutItem;


    private final JPopupMenu popupMenu;

    private final JMenuItem removeFileFromLibraryPopupItem;
    private final JMenuItem selectPlaylistsForFilesPopupItem;
    private final JMenuItem removeFileFromPlaylistPopupItem;

    private final JButton selectPlaylistsForFilesButton;
    private final JButton removeFileFromPlaylistButton;
    private final JButton removeFileFromLibraryButton;

    public JButton getSelectPlaylistsForFilesButton() {
        return selectPlaylistsForFilesButton;
    }

    public JButton getRemoveFileFromPlaylistButton() {
        return removeFileFromPlaylistButton;
    }

    public JButton getRemoveFileFromLibraryButton() {
        return removeFileFromLibraryButton;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JMenuItem getRemoveFileFromLibraryPopupItem() {
        return removeFileFromLibraryPopupItem;
    }

    public JMenuItem getSelectPlaylistsForFilesPopupItem() {
        return selectPlaylistsForFilesPopupItem;
    }

    public JMenuItem getRemoveFileFromPlaylistPopupItem() {
        return removeFileFromPlaylistPopupItem;
    }

    public JMenuItem getNewItem() {
        return newItem;
    }

    public JMenuItem getOpenItem() {
        return openItem;
    }

    public JMenuItem getSaveItem() {
        return saveItem;
    }

    public JMenuItem getSaveAsItem() {
        return saveAsItem;
    }

    public JMenuItem getExitItem() {
        return exitItem;
    }

    public JMenuItem getUndoItem() {
        return undoItem;
    }

    public JMenuItem getRedoItem() {
        return redoItem;
    }

    public JMenuItem getAddFileToLibraryItem() {
        return addFileToLibraryItem;
    }

    public JMenuItem getRemoveFileFromLibraryItem() {
        return removeFileFromLibraryItem;
    }

    public JMenuItem getSelectPlaylistsForFilesItem() {
        return selectPlaylistsForFilesItem;
    }

    public JMenuItem getRemoveFileFromPlaylistItem() {
        return removeFileFromPlaylistItem;
    }


    public JMenuItem getCreatePlaylistItem() {
        return createPlaylistItem;
    }

    public JMenuItem getRemovePlaylistItem() {
        return removePlaylistItem;
    }

    public JMenuItem getAboutItem() {
        return aboutItem;
    }

    public JList<FileTableModel> getPlaylists() {
        return playlists;
    }

    public JLabel getInformationLabel() {
        return informationLabel;
    }

    public JTable getFileTable() {
        return fileTable;
    }

    public JTextField getSearchBar() { return searchBar; }



    public SlimTunes(Library library) {
        super(TITLE);

        fileTable = new JTable(library);
        FileTableModel.setWidths(fileTable);
        fileTable.setAutoCreateRowSorter(true);
        /* {
            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);

                try {
                    tip = "<html>" + files.get(rowIndex).toString().replaceAll("\n", "<br/>") + "</html>";
                } catch (RuntimeException ignore) {
                }

                return tip;
            }
        };
        */

        // File table
        JScrollPane pane = new JScrollPane(fileTable);
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(makeSpacedBorder("File List"));
        listPanel.add(pane, BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);

        // File information
        informationLabel = new JLabel();
        informationLabel.setPreferredSize(new Dimension(256, 256));
        informationLabel.setMinimumSize(new Dimension(256, 256));
        informationLabel.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane scrollPane = new JScrollPane(informationLabel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(SMALL_SPACING, SMALL_SPACING, SMALL_SPACING, SMALL_SPACING));

        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBorder(makeSpacedBorder("Information"));
        filePanel.add(scrollPane, BorderLayout.CENTER);

        selectPlaylistsForFilesButton = new JButton("Select Playlists for File");
        selectPlaylistsForFilesButton.setEnabled(false);
        removeFileFromPlaylistButton = new JButton("Remove File from Playlist");
        removeFileFromPlaylistButton.setEnabled(false);
        removeFileFromLibraryButton = new JButton("Remove File from Library");
        removeFileFromLibraryButton.setEnabled(false);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, SPACING, SPACING));
        buttonPanel.add(selectPlaylistsForFilesButton);
        buttonPanel.add(removeFileFromPlaylistButton);
        buttonPanel.add(removeFileFromLibraryButton);
        JPanel spacingPanel = new JPanel(new FlowLayout());
        spacingPanel.add(buttonPanel);
        filePanel.add(spacingPanel, BorderLayout.SOUTH);

        add(filePanel, BorderLayout.EAST);

        // Playlists
        playlists = new JList<>(library.getPlaylists());
        playlists.setBorder(BorderFactory.createEmptyBorder(SMALL_SPACING, SMALL_SPACING, SMALL_SPACING, SMALL_SPACING));
        playlists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlists.setSelectedIndex(0);
        JScrollPane playlistScrollPane = new JScrollPane(playlists);
        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setBorder(makeSpacedBorder("Playlists"));
        playlistPanel.add(playlistScrollPane, BorderLayout.CENTER);
        add(playlistPanel, BorderLayout.WEST);

        // Search bar
        searchBar = new JTextField();
        JPanel searchBarPanel = new JPanel(new BorderLayout());
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));
        searchBarPanel.add(searchBar, BorderLayout.CENTER);
        searchBarPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        add(searchBarPanel, BorderLayout.NORTH);



        Toolkit toolkit = Toolkit.getDefaultToolkit();

        // Menus
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open...");
        saveItem = new JMenuItem("Save");
        saveItem.setEnabled(false);
        saveAsItem = new JMenuItem("Save As...");
        exitItem = new JMenuItem("Exit");
        newItem.setAccelerator(KeyStroke.getKeyStroke('N', toolkit.getMenuShortcutKeyMaskEx()));
        newItem.setMnemonic(KeyEvent.VK_N);
        openItem.setAccelerator(KeyStroke.getKeyStroke('O', toolkit.getMenuShortcutKeyMaskEx()));
        openItem.setMnemonic(KeyEvent.VK_O);
        saveItem.setAccelerator(KeyStroke.getKeyStroke('S', toolkit.getMenuShortcutKeyMaskEx()));
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveAsItem.setAccelerator(
        KeyStroke.getKeyStroke(
            'S', toolkit.getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.setMnemonic(KeyEvent.VK_A);
        exitItem.setAccelerator(KeyStroke.getKeyStroke('Q', toolkit.getMenuShortcutKeyMaskEx()));
        exitItem.setMnemonic(KeyEvent.VK_X);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);


        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        undoItem = new JMenuItem("Undo");
        undoItem.setEnabled(false);
        redoItem = new JMenuItem("Redo");
        redoItem.setEnabled(false);
        undoItem.setAccelerator(KeyStroke.getKeyStroke('Z', toolkit.getMenuShortcutKeyMaskEx()));
        undoItem.setMnemonic(KeyEvent.VK_U);
        redoItem.setAccelerator(KeyStroke.getKeyStroke('Y', toolkit.getMenuShortcutKeyMaskEx()));
        redoItem.setMnemonic(KeyEvent.VK_R);

        editMenu.add(undoItem);
        editMenu.add(redoItem);

        JMenu mediaMenu = new JMenu("Media");
        mediaMenu.setMnemonic(KeyEvent.VK_M);
        selectPlaylistsForFilesItem = new JMenuItem("Select Playlists for File");
        selectPlaylistsForFilesItem.setEnabled(false);
        removeFileFromPlaylistItem = new JMenuItem("Remove File from Playlist");
        removeFileFromPlaylistItem.setEnabled(false);
        addFileToLibraryItem = new JMenuItem("Add File to Library");
        removeFileFromLibraryItem = new JMenuItem("Remove File from Library");
        removeFileFromLibraryItem.setEnabled(false);
        createPlaylistItem = new JMenuItem("Create Playlist");
        removePlaylistItem = new JMenuItem("Remove Playlist");
        removePlaylistItem.setEnabled(false);
        selectPlaylistsForFilesItem.setAccelerator(KeyStroke.getKeyStroke('P', toolkit.getMenuShortcutKeyMaskEx()));
        selectPlaylistsForFilesItem.setMnemonic(KeyEvent.VK_S);
        removeFileFromPlaylistItem.setAccelerator(KeyStroke.getKeyStroke('R', toolkit.getMenuShortcutKeyMaskEx()));
        removeFileFromPlaylistItem.setMnemonic(KeyEvent.VK_R);
        addFileToLibraryItem.setAccelerator(KeyStroke.getKeyStroke('F', toolkit.getMenuShortcutKeyMaskEx()));
        addFileToLibraryItem.setMnemonic(KeyEvent.VK_A);
        removeFileFromLibraryItem.setMnemonic(KeyEvent.VK_E);
        createPlaylistItem.setMnemonic(KeyEvent.VK_C);
        removePlaylistItem.setMnemonic(KeyEvent.VK_P);

        mediaMenu.add(selectPlaylistsForFilesItem);
        mediaMenu.add(removeFileFromPlaylistItem);
        mediaMenu.addSeparator();
        mediaMenu.add(addFileToLibraryItem);
        mediaMenu.add(removeFileFromLibraryItem);
        mediaMenu.addSeparator();
        mediaMenu.add(createPlaylistItem);
        mediaMenu.add(removePlaylistItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        helpMenu.add(aboutItem);


        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(mediaMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Popup stuff
        popupMenu = new JPopupMenu();
        selectPlaylistsForFilesPopupItem = new JMenuItem("Select Playlists for File");
        selectPlaylistsForFilesPopupItem.setEnabled(false);
        removeFileFromPlaylistPopupItem = new JMenuItem("Remove File from Playlist");
        removeFileFromPlaylistPopupItem.setEnabled(false);
        removeFileFromLibraryPopupItem = new JMenuItem("Remove File from Playlist");
        removeFileFromLibraryPopupItem.setEnabled(false);

        popupMenu.add(selectPlaylistsForFilesPopupItem);
        popupMenu.add(removeFileFromPlaylistPopupItem);
        popupMenu.add(removeFileFromLibraryPopupItem);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(getWidth() * 2 / 3, getHeight() / 2));
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static Border makeSpacedBorder(String title) {
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(SMALL_SPACING, SMALL_SPACING, SMALL_SPACING, SMALL_SPACING),
                BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title), BorderFactory.createEmptyBorder(SMALL_SPACING, SMALL_SPACING, SMALL_SPACING, SMALL_SPACING)));
    }
}

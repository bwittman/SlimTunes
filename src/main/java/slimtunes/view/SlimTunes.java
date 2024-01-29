package slimtunes.view;

import slimtunes.model.FileList;
import slimtunes.model.FileTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SlimTunes extends JFrame {

    public static final String TITLE = "SlimTunes";
    public static final int SPACING = 10;
    private final JList<FileList> playlists;
    private final JLabel fileLabel;
    private final JTable fileTable;

    private final JTextField searchBar;

    private final JMenuItem newItem;
    private final JMenuItem openItem;
    private final JMenuItem saveItem;
    private final JMenuItem saveAsItem;
    private final JMenuItem exitItem;
    private final JMenuItem addFileToLibraryItem;
    private final JMenuItem removeFileFromLibraryItem;
    private final JMenuItem addFileToPlaylistsItem;
    private final JMenuItem removeFileFromPlaylistItem;

    private final JPopupMenu popupMenu;



    private final JMenuItem removeFileFromLibraryPopupItem;
    private final JMenuItem addFileToPlaylistsPopupItem;
    private final JMenuItem removeFileFromPlaylistPopupItem;

    private final JButton addFileToPlaylistsButton;
    private final JButton removeFileFromPlaylistButton;
    private final JButton removeFileFromLibraryButton;

    public JButton getAddFileToPlaylistsButton() {
        return addFileToPlaylistsButton;
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

    public JMenuItem getAddFileToPlaylistsPopupItem() {
        return addFileToPlaylistsPopupItem;
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

    public JMenuItem getAddFileToLibraryItem() {
        return addFileToLibraryItem;
    }

    public JMenuItem getRemoveFileFromLibraryItem() {
        return removeFileFromLibraryItem;
    }

    public JMenuItem getAddFileToPlaylistsItem() {
        return addFileToPlaylistsItem;
    }

    public JMenuItem getRemoveFileFromPlaylistItem() {
        return removeFileFromPlaylistItem;
    }

    public JList<FileList> getPlaylists() {
        return playlists;
    }

    public JLabel getFileLabel() {
        return fileLabel;
    }

    public JTable getFileTable() {
        return fileTable;
    }

    public JTextField getSearchBar() { return searchBar; }

    public SlimTunes() {
        super(TITLE);

        FileTableModel fileTableModel = new FileTableModel(new ArrayList<>());
        fileTable = new JTable(fileTableModel);
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
        listPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "File List"));
        listPanel.add(pane, BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);

        // File information
        fileLabel = new JLabel();
        fileLabel.setPreferredSize(new Dimension(256, 256));
        fileLabel.setMinimumSize(new Dimension(256, 256));
        fileLabel.setVerticalAlignment(SwingConstants.TOP);
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected File"));
        filePanel.add(fileLabel, BorderLayout.CENTER);

        addFileToPlaylistsButton = new JButton("Add File to Playlists");
        addFileToPlaylistsButton.setEnabled(false);
        removeFileFromPlaylistButton = new JButton("Remove File from Playlist");
        removeFileFromPlaylistButton.setEnabled(false);
        removeFileFromLibraryButton = new JButton("Remove File from Library");
        removeFileFromLibraryButton.setEnabled(false);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, SPACING, SPACING));
        buttonPanel.add(addFileToPlaylistsButton);
        buttonPanel.add(removeFileFromPlaylistButton);
        buttonPanel.add(removeFileFromLibraryButton);
        JPanel spacingPanel = new JPanel(new FlowLayout());
        spacingPanel.add(buttonPanel);
        filePanel.add(spacingPanel, BorderLayout.SOUTH);

        add(filePanel, BorderLayout.EAST);

        // Playlists
        playlists = new JList<>(new DefaultListModel<>());
        playlists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane playlistScrollPane = new JScrollPane(playlists);
        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Playlists"));
        playlistPanel.add(playlistScrollPane, BorderLayout.CENTER);
        add(playlistPanel, BorderLayout.WEST);

        // Search bar
        searchBar = new JTextField();
        JPanel searchBarPanel = new JPanel(new BorderLayout());
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING));
        searchBarPanel.add(searchBar, BorderLayout.CENTER);
        searchBarPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        add(searchBarPanel, BorderLayout.NORTH);

        // Menus
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open...");
        saveItem = new JMenuItem("Save");
        saveItem.setEnabled(false);
        saveAsItem = new JMenuItem("Save As...");
        saveAsItem.setEnabled(false);
        exitItem = new JMenuItem("Exit");
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu mediaMenu = new JMenu("Media");
        addFileToPlaylistsItem = new JMenuItem("Add File to Playlist");
        addFileToPlaylistsItem.setEnabled(false);
        removeFileFromPlaylistItem = new JMenuItem("Remove File from Playlist");
        removeFileFromPlaylistItem.setEnabled(false);
        addFileToLibraryItem = new JMenuItem("Add File to Library");
        addFileToLibraryItem.setEnabled(false);
        removeFileFromLibraryItem = new JMenuItem("Remove File from Library");
        removeFileFromLibraryItem.setEnabled(false);

        mediaMenu.add(addFileToPlaylistsItem);
        mediaMenu.add(removeFileFromPlaylistItem);
        mediaMenu.addSeparator();
        mediaMenu.add(addFileToLibraryItem);
        mediaMenu.add(removeFileFromLibraryItem);

        menuBar.add(fileMenu);
        menuBar.add(mediaMenu);
        setJMenuBar(menuBar);


        // Popup stuff
        popupMenu = new JPopupMenu();
        removeFileFromLibraryPopupItem = new JMenuItem("Remove File from Playlist");
        removeFileFromLibraryPopupItem.setEnabled(false);
        addFileToPlaylistsPopupItem = new JMenuItem("Add File to Playlists");
        addFileToPlaylistsPopupItem.setEnabled(false);
        removeFileFromPlaylistPopupItem = new JMenuItem("Remove File from Playlist");
        removeFileFromPlaylistPopupItem.setEnabled(false);

        popupMenu.add(removeFileFromLibraryPopupItem);
        popupMenu.add(addFileToPlaylistsPopupItem);
        popupMenu.add(removeFileFromPlaylistPopupItem);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = device.getDisplayMode().getWidth();
        int height = device.getDisplayMode().getHeight();
        setSize(width/2, height/2);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

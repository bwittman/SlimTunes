package slimtunes.view;

import slimtunes.model.SongTableModel;
import slimtunes.model.Playlist;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SlimTunes extends JFrame {

    public static final String TITLE = "SlimTunes";
    private final JList<Playlist> playlists;
    private final JLabel songLabel;
    private final JTable songTable;

    private final JTextField searchBar;

    private final JMenuItem newItem;
    private final JMenuItem openItem;
    private final JMenuItem saveItem;
    private final JMenuItem saveAsItem;
    private final JMenuItem exitItem;

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

    public JList<Playlist> getPlaylists() {
        return playlists;
    }

    public JLabel getSongLabel() {
        return songLabel;
    }

    public JTable getSongTable() {
        return songTable;
    }

    public JTextField getSearchBar() { return searchBar; }

    public SlimTunes() {
        super(TITLE);

        SongTableModel songTableModel = new SongTableModel(new ArrayList<>());
        songTable = new JTable(songTableModel);
        SongTableModel.setWidths(songTable);
        songTable.setAutoCreateRowSorter(true);
        /* {
            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);

                try {
                    tip = "<html>" + songs.get(rowIndex).toString().replaceAll("\n", "<br/>") + "</html>";
                } catch (RuntimeException ignore) {
                }

                return tip;
            }
        };
        */

        // Song table
        JScrollPane pane = new JScrollPane(songTable);
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Song List"));
        listPanel.add(pane, BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);

        // Song information
        songLabel = new JLabel();
        songLabel.setPreferredSize(new Dimension(256, 256));
        songLabel.setVerticalAlignment(SwingConstants.TOP);
        JPanel songPanel = new JPanel(new BorderLayout());
        songPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected Song"));
        songPanel.add(songLabel, BorderLayout.CENTER);
        add(songPanel, BorderLayout.EAST);

        // Playlists
        playlists = new JList<>(new DefaultListModel<>());
        JScrollPane playlistScrollPane = new JScrollPane(playlists);
        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Playlists"));
        playlistPanel.add(playlistScrollPane, BorderLayout.CENTER);
        add(playlistPanel, BorderLayout.WEST);

        // Search bar
        searchBar = new JTextField();
        JPanel searchBarPanel = new JPanel(new BorderLayout());
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1024, 768);
        this.setLocationRelativeTo(null);
        setVisible(true);
    }
}

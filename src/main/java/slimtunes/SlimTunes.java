package slimtunes;

import org.xml.sax.SAXException;
import slimtunes.gui.SongTableModel;
import slimtunes.library.Library;
import slimtunes.library.Playlist;
import slimtunes.library.Song;
import slimtunes.library.XML;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SlimTunes extends JFrame {

    private final Library library = new Library();

    public SlimTunes() {
        super("SlimTunes");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        try {
            XML xml = new XML(library);
            xml.load(Path.of("Library.xml"));

            library.printPlaylists();

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        List<Song> songs = library.getSongs();
        //songs.add(new Song(1, "Sweet Child of Mine", "Guns'n'Roses", "Appetite for Destruction", null, -1, 215, -1, -1, -1, -1, 1984, 128, 44100, null));
        //songs.add(new Song(2, "Side to Side", "Blackalicious", "The Craft", null, -1, 176, -1, -1, -1, -1, 2005, 128, 44100, null));
        //ID, NAME, ARTIST, ALBUM, GENRE, SIZE, TIME, DISC, DISC_COUNT, TRACK, TRACK_COUNT, YEAR, BIT_RATE, SAMPLE_RATE, PATH;
        SongTableModel model = new SongTableModel(songs);
        JTable table = new JTable(model);/* {
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
        JScrollPane pane = new JScrollPane(table);
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Song List"));
        listPanel.add(pane, BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);

        // Song information
        JLabel songLabel = new JLabel();
        songLabel.setPreferredSize(new Dimension(256, 256));
        songLabel.setVerticalAlignment(SwingConstants.TOP);
        JPanel songPanel = new JPanel(new BorderLayout());
        songPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected Song"));
        songPanel.add(songLabel, BorderLayout.CENTER);
        add(songPanel, BorderLayout.EAST);

        //Playlists
        DefaultListModel<Playlist> listModel = new DefaultListModel<>();
        for (Playlist playlist : library.getPlaylists())
            listModel.addElement(playlist);

        JList<Playlist> playlists = new JList<>(listModel);
        JScrollPane playlistScrollPane = new JScrollPane(playlists);
        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Playlists"));
        playlistPanel.add(playlistScrollPane, BorderLayout.CENTER);
        add(playlistPanel, BorderLayout.WEST);


        setSize(1024, 768);
        this.setLocationRelativeTo(null);

        /*
        JTextArea area = new JTextArea(10, 80);
        add(area, BorderLayout.SOUTH);
*/
        table.getSelectionModel().addListSelectionListener(event -> {
            // do some actions here, for example
            // print first column value from selected row
            ;

            if (table.getSelectedRowCount() > 1)
                songLabel.setText(table.getSelectedRowCount() + " songs selected");
            else if (table.getSelectedRowCount() == 1)
                songLabel.setText("<html>" + songs.get(table.getSelectedRow()).toString().replaceAll("\n", "<br/>") + "</html>");
        });


        setVisible(true);


    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ignore) {
        }
        new SlimTunes();
    }
}

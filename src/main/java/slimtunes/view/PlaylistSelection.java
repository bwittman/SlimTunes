package slimtunes.view;

import com.jidesoft.swing.TristateCheckBox;
import slimtunes.model.File;
import slimtunes.model.Playlist;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSelection extends JDialog {
    private final JButton doneButton = new JButton("Done");
    private final JButton cancelButton = new JButton("Cancel");

    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private final List<Playlist> playlists;

    private final SelectionManager selectionManager;

    public JButton getDoneButton() {
        return doneButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public PlaylistSelection(SlimTunes parent, File[] files, List<Playlist> playlists) {
        super(parent, "Select Playlists", true);
        this.playlists = playlists;

        JPanel filePanel = new JPanel(new BorderLayout());
        String title;
        if (files.length > 1) {
            selectionManager = new MultipleFileSelectionManager();
            title = "Selected Files";
        }
        else {
            selectionManager = new SingleFileSelectionManager();
            title = "Selected File";
        }

        filePanel.setBorder(BorderFactory.createTitledBorder(title));

        boolean first = true;
        StringBuilder builder = new StringBuilder("<html>");
        for (File file : files) {
            if (first)
                first = false;
            else
                builder.append("<br/>");
            builder.append(file.toString());
        }

        JLabel fileLabel = new JLabel(builder.append("</html>").toString());
        fileLabel.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane scrollPane = new JScrollPane(fileLabel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(SlimTunes.SPACING, SlimTunes.SPACING,
                SlimTunes.SPACING, SlimTunes.SPACING));
        filePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel playlistPanel = new JPanel();
        playlistPanel.setLayout(new BoxLayout(playlistPanel, BoxLayout.Y_AXIS));

        int widestCheckbox = 0;

        for (Playlist playlist : playlists) {
            JCheckBox checkBox = selectionManager.createCheckBox(files, playlist);
            int width = checkBox.getWidth();
            if (width > widestCheckbox)
                widestCheckbox = width;
            checkBoxes.add(checkBox);
            playlistPanel.add(checkBox);
        }

        scrollPane = new JScrollPane(playlistPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Choose Playlists"));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, SlimTunes.SPACING, SlimTunes.SPACING));
        buttonPanel.add(doneButton);
        buttonPanel.add(cancelButton);
        JPanel spacingPanel = new JPanel(new FlowLayout());
        spacingPanel.add(buttonPanel);

        add(filePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(spacingPanel, BorderLayout.SOUTH);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int height = device.getDisplayMode().getHeight();
        scrollPane.setPreferredSize(new Dimension(widestCheckbox * 2, height /2 ));

        pack();
        setMinimumSize(new Dimension(getWidth(), height / 2));
        setLocationRelativeTo(parent);
    }

    public void updatePlaylists(List<Playlist> addLists, List<Playlist> removeLists) {
        addLists.clear();
        removeLists.clear();
        for (int i = 0; i < playlists.size(); ++i)
            selectionManager.updatePlaylists(checkBoxes.get(i), playlists.get(i), addLists, removeLists);
    }


    private static class SingleFileSelectionManager implements SelectionManager {
        @Override
        public JCheckBox createCheckBox(File[] files, Playlist playlist) {
            JCheckBox checkBox = new JCheckBox(playlist.toString());
            checkBox.setSelected(playlist.contains(files[0]));
            return checkBox;
        }

        @Override
        public void updatePlaylists(JCheckBox checkBox, Playlist playlist, List<Playlist> addLists, List<Playlist> removeLists) {
            if (checkBox.isSelected())
                addLists.add(playlist);
            else
                removeLists.add(playlist);
        }
    }
    private static class MultipleFileSelectionManager implements SelectionManager {
        @Override
        public JCheckBox createCheckBox(File[] files, Playlist playlist) {
            TristateCheckBox checkBox = new TristateCheckBox(playlist.toString());
            int count = 0;
            for (File file : files) {
                if (playlist.contains(file))
                    ++count;
            }

            if (count == 0)
                checkBox.setState(TristateCheckBox.STATE_UNSELECTED);
            else if (count == files.length)
                checkBox.setState(TristateCheckBox.STATE_SELECTED);
            else
                checkBox.setState(TristateCheckBox.STATE_MIXED);
            return checkBox;
        }

        @Override
        public void updatePlaylists(JCheckBox checkBox, Playlist playlist, List<Playlist> addLists, List<Playlist> removeLists) {
            TristateCheckBox tristateCheckBox = (TristateCheckBox) checkBox;
            if (tristateCheckBox.getState() == TristateCheckBox.STATE_SELECTED)
                addLists.add(playlist);
            else if (tristateCheckBox.getState() == TristateCheckBox.STATE_UNSELECTED)
                removeLists.add(playlist);

        }
    }
}

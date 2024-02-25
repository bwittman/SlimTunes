package slimtunes.view;

import slimtunes.model.File;
import slimtunes.model.FileTableModel;
import slimtunes.model.Playlist;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSelection extends JDialog {
    private final JButton doneButton = new JButton("Done");
    private final JButton cancelButton = new JButton("Cancel");

    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private final DefaultListModel<FileTableModel> playlists;

    public JButton getDoneButton() {
        return doneButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public PlaylistSelection(SlimTunes parent, File[] files,
                             DefaultListModel<FileTableModel> playlists) {
        super(parent, "Select Playlists", true);
        this.playlists = playlists;

        JPanel filePanel = new JPanel(new BorderLayout());
        String title = files.length > 1 ? "Selected Files" : "Selected File";
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

        // First playlist is always the library itself
        for (int i = 1; i < playlists.size(); ++i) {
            FileTableModel playlist = playlists.get(i);
            JCheckBox checkBox = createCheckBox(files, playlist);
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

    public void updatePlaylists(List<FileTableModel> addLists, List<FileTableModel> removeLists) {
        addLists.clear();
        removeLists.clear();
        for (int i = 1; i < playlists.size(); ++i)
            updatePlaylists(checkBoxes.get(i), playlists.get(i), addLists, removeLists);
    }

    private JCheckBox createCheckBox(File[] files, FileTableModel playlist) {
        int count = 0;
        for (File file : files) {
            if (playlist.contains(file))
                ++count;
        }

        if (count == 0 || count == files.length) {
            JCheckBox checkBox = new JCheckBox(playlist.toString());
            checkBox.setSelected(count == files.length);
            return checkBox;
        }
        else {
            TristateCheckBox checkBox = new TristateCheckBox(playlist.toString());
            checkBox.setIndeterminate();
            return checkBox;
        }

    }


    public void updatePlaylists(JCheckBox checkBox, FileTableModel playlist, List<FileTableModel> addLists, List<FileTableModel> removeLists) {
        if (checkBox instanceof TristateCheckBox tristateCheckBox) {
            if (tristateCheckBox.getState() == TristateButtonModel.State.SELECTED)
                addLists.add(playlist);
            else if (tristateCheckBox.getState() == TristateButtonModel.State.DESELECTED)
                removeLists.add(playlist);
        }
        else {
            if (checkBox.isSelected())
                addLists.add(playlist);
            else
                removeLists.add(playlist);
        }
    }
}

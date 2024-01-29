package slimtunes.view;

import com.jidesoft.swing.TristateCheckBox;
import slimtunes.model.File;
import slimtunes.model.Playlist;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LibrarySelection extends JDialog {
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

    public LibrarySelection(SlimTunes parent, File[] files, List<Playlist> playlists) {
        super(parent, "Select Libraries", true);
        this.playlists = playlists;

        JPanel filePanel = new JPanel(new BorderLayout());
        if (files.length > 1) {
            selectionManager = new MultipleFileSelectionManager();
            filePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected Files"));
        }
        else {
            selectionManager = new SingleFileSelectionManager();
            filePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected File"));
        }

        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        boolean first = true;
        for (File file : files) {
            if (first)
                first = false;
            else
                textArea.append(System.lineSeparator());
            textArea.append(file.toString());
        }
        textArea.setEditable(false);
        filePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel playlistPanel = new JPanel(new GridLayout(playlists.size(), 1));
        playlistPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Choose Playlists"));

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

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, SlimTunes.SPACING, SlimTunes.SPACING));
        buttonPanel.add(doneButton);
        buttonPanel.add(cancelButton);

        add(filePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = device.getDisplayMode().getWidth();
        int height = device.getDisplayMode().getHeight();
        setSize(new Dimension(width, height/2));
        setMinimumSize(new Dimension(widestCheckbox * 2, height/3));

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
            checkBox.setSelected(playlist.getFiles().contains(files[0]));
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
                if (playlist.getFiles().contains(file))
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

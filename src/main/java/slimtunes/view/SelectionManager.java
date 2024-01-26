package slimtunes.view;

import slimtunes.model.File;
import slimtunes.model.Playlist;

import javax.swing.*;
import java.util.List;

public interface SelectionManager {
    JCheckBox createCheckBox(File[] files, Playlist playlist);
    void updatePlaylists(JCheckBox checkBox, Playlist playlist, List<Playlist> addLists, List<Playlist> removeLists);
}

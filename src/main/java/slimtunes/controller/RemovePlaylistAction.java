package slimtunes.controller;

import slimtunes.model.FileTableModel;
import slimtunes.model.Library;
import slimtunes.model.Playlist;

import javax.swing.*;

public class RemovePlaylistAction implements Action {

  private final Playlist playlist;
  private final int index;

  public RemovePlaylistAction(Playlist playlist, int index) {
    this.playlist = playlist;
    this.index = index;
  }

  @Override
  public void doAction(Controller controller) {
    controller.getLibrary().removePlaylist(playlist);
    controller.getSlimTunes().revalidate();
    JList<FileTableModel> playlists = controller.getSlimTunes().getPlaylists();
    playlists.setSelectedIndex(0);
  }

  @Override
  public void undoAction(Controller controller) {
    controller.getLibrary().addPlaylist(index, playlist);
    controller.getSlimTunes().revalidate();
    JList<FileTableModel> playlists = controller.getSlimTunes().getPlaylists();
    playlists.setSelectedIndex(index);
  }

  @Override
  public String toString() {
    return "Remove Playlist " + playlist;
  }
}

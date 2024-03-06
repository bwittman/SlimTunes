package slimtunes.controller;

import slimtunes.model.FileTableModel;
import slimtunes.model.Playlist;

import javax.swing.*;

public class CreatePlaylistAction implements Action {
  private final Playlist playlist;

  public CreatePlaylistAction(Playlist playlist) {
    this.playlist = playlist;
  }

  @Override
  public void doAction(Controller controller) {
    controller.getLibrary().addPlaylist(playlist);
    controller.getSlimTunes().revalidate();
    JList<FileTableModel> playlists = controller.getSlimTunes().getPlaylists();
    playlists.setSelectedIndex(playlists.getModel().getSize() - 1);
  }

  @Override
  public void undoAction(Controller controller) {
    controller.getLibrary().removePlaylist(playlist);
    controller.getSlimTunes().revalidate();
    JList<FileTableModel> playlists = controller.getSlimTunes().getPlaylists();
    playlists.setSelectedIndex(0);
  }

  @Override
  public String toString() {
    return "Create Playlist " + playlist;
  }
}

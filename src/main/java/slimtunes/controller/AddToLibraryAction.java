package slimtunes.controller;

import slimtunes.model.File;
import slimtunes.model.FileCreationException;
import slimtunes.model.Library;
import slimtunes.view.SlimTunes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddToLibraryAction implements Action {
    final List<File> files;


    public AddToLibraryAction(Set<Path> paths) throws FileCreationException {
        this.files = new ArrayList<>(paths.size());
        for (Path path : paths)
            files.add(new File(path));
    }
    @Override
    public void doAction(Controller controller) {
        Library library = controller.getLibrary();
        SlimTunes slimTunes = controller.getSlimTunes();
        for (File file : files)
            library.add(file);
        controller.setPlaylist(slimTunes.getPlaylists().getSelectedValue());
        slimTunes.getSearchBar().setText(slimTunes.getSearchBar().getText());
    }

    @Override
    public void undoAction(Controller controller) {
        Library library = controller.getLibrary();
        SlimTunes slimTunes = controller.getSlimTunes();
        for (File file : files)
            library.remove(file);
        controller.setPlaylist(slimTunes.getPlaylists().getSelectedValue());
        slimTunes.getSearchBar().setText(slimTunes.getSearchBar().getText());
    }

    @Override
    public String toString() {
        return "Add File" + (files.size() > 1 ? "s" : "") + " to Library";
    }
}

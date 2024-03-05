package slimtunes.controller;

import slimtunes.model.File;
import slimtunes.model.FileTableModel;

import java.util.*;

public class UpdatePlaylistsAction implements Action {
    private final Map<FileTableModel, Map<Integer, File>> removeFileIndexes = new LinkedHashMap<>();
    private final List<FileTableModel> addFileTables;
    private final File[] files;

    private final boolean removeOrUpdate;

    public UpdatePlaylistsAction(File[] files, List<FileTableModel> addFileTables, List<FileTableModel> removeFileTables, boolean removeOrUpdate)  {
        this.removeOrUpdate = removeOrUpdate;
        this.files = files;
        this.addFileTables = addFileTables;

        for (FileTableModel fileTableModel : removeFileTables) {
            Map<Integer, File> indexMap = null;
            for (File file : files) {
                int index = fileTableModel.find(file);
                if (index != -1) {
                    if (indexMap == null)
                        indexMap = new TreeMap<>();
                    indexMap.put(index, file);
                }
            }
            if (indexMap != null)
                removeFileIndexes.put(fileTableModel, indexMap);
        }
    }

    @Override
    public void doAction(Controller controller) {
        // Remove first
        for (Map.Entry<FileTableModel, Map<Integer, File>> entry : removeFileIndexes.entrySet()) {
            FileTableModel fileTable = entry.getKey();
            Map<Integer, File> indexes = entry.getValue();
            for (File file : indexes.values())
                fileTable.remove(file);
        }

        // Then add to ends of file tables (playlists)
        for (FileTableModel fileTableModel : addFileTables) {
            for (File file : files) {
                fileTableModel.add(fileTableModel.getRowCount(), file);
            }
        }
    }

    @Override
    public void undoAction(Controller controller) {
        // First remove added items
        for (FileTableModel fileTableModel : addFileTables) {
            for (File file : files) {
                fileTableModel.remove(file);
            }
        }

        // Then add back all the removed files, exactly where they were
        for (Map.Entry<FileTableModel, Map<Integer, File>> entry : removeFileIndexes.entrySet()) {
            FileTableModel fileTable = entry.getKey();
            Map<Integer, File> indexes = entry.getValue();
            for (Map.Entry<Integer, File> fileEntry : indexes.entrySet())
                fileTable.add(fileEntry.getKey(), fileEntry.getValue());
        }
    }

    @Override
    public String toString() {
        boolean pluralPlaylists = removeFileIndexes.size() + addFileTables.size() > 1;
        if (removeOrUpdate)
            return "Remove File" + (files.length > 1 ? "s" : "") + " From Playlist" + (pluralPlaylists ? "s" : "");
        else
            return "Update Playlist" + (pluralPlaylists ? "s" : "");
    }
}

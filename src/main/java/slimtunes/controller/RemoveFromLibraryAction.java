package slimtunes.controller;

import slimtunes.model.File;
import slimtunes.model.FileTableModel;

import javax.swing.*;
import java.util.*;

public class RemoveFromLibraryAction implements Action {
    private final Map<FileTableModel, Map<Integer, File>> fileIndexes = new LinkedHashMap<>();
    private final boolean plural;

    public RemoveFromLibraryAction(File[] files, DefaultListModel<FileTableModel> fileTables) {
        plural = files.length > 1;
        for (int i = 0; i < fileTables.size(); ++i) {
            FileTableModel fileTable = fileTables.get(i);
            Map<Integer, File> indexes = new TreeMap<>();
            for (File file : files) {
                int index = fileTable.find(file);
                if (index != -1)
                    indexes.put(index, file);
            }
            fileIndexes.put(fileTable, indexes);
        }
    }
    @Override
    public void doAction(Controller controller) {
        for (Map.Entry<FileTableModel, Map<Integer, File>> entry : fileIndexes.entrySet()) {
            FileTableModel fileTable = entry.getKey();
            Map<Integer, File> indexes = entry.getValue();
            for (File file : indexes.values())
                fileTable.remove(file);
        }
    }

    @Override
    public void undoAction(Controller controller) {
        for (Map.Entry<FileTableModel, Map<Integer, File>> entry : fileIndexes.entrySet()) {
            FileTableModel fileTable = entry.getKey();
            Map<Integer, File> indexes = entry.getValue();
            for (Map.Entry<Integer, File> fileEntry : indexes.entrySet())
                fileTable.add(fileEntry.getKey(), fileEntry.getValue());
        }
    }

    @Override
    public String toString() {
        return "Remove File" + (plural ? "s" : "") + " from Library";
    }
}

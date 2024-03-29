package slimtunes.controller;

import org.xml.sax.SAXException;

import slimtunes.model.*;
import slimtunes.model.xml.Reader;
import slimtunes.model.xml.Writer;
import slimtunes.view.PlaylistSelection;
import slimtunes.view.SlimTunes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class Controller {

  private final SlimTunes slimTunes;
  private Library library;
  private java.io.File currentFile = null;

  private final JFileChooser xmlChooser = new JFileChooser();
  private final JFileChooser mediaChooser = new JFileChooser();

  private final List<Action> actions = new ArrayList<>();
  private int lastSavedAction = -1;
  private int currentAction = -1;

  public final String[] EXTENSIONS = {
    ".aac", ".aiff", ".aif", ".aifc", ".dsf", ".flac", ".m4a", ".mp4", ".m4b", ".m4p", ".m4r",
    ".m4v", ".mov", ".mp3", ".oga", ".ogg", ".ogx", ".wav", ".wave", ".wma"
  };

  public Controller() {
    library = new Library();
    slimTunes = new SlimTunes(library);

    xmlChooser.setFileFilter(
        new FileFilter() {
          @Override
          public boolean accept(java.io.File file) {
            if (file == null) return false;
            return file.isDirectory() || file.toString().toLowerCase().endsWith(".xml");
          }

          @Override
          public String getDescription() {
            return "XML Files (*.xml)";
          }
        });

    xmlChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    mediaChooser.setFileFilter(
        new FileFilter() {
          @Override
          public boolean accept(java.io.File file) {
            if (file == null) return false;

            String name = file.toString().toLowerCase();

            if (file.isDirectory()) return true;

            for (String extension : EXTENSIONS) if (name.endsWith(extension)) return true;

            return false;
          }

          @Override
          public String getDescription() {
            return "Media Files";
          }
        });

    mediaChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    mediaChooser.setMultiSelectionEnabled(true);

    addPlaylistListeners();
    addFileTableListeners();
    addMenuListeners();
    addButtonListeners();
    addSearchBarListeners();

    slimTunes.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            quit();
          }
        });
  }

  private boolean safeToContinue(String message) {
    if (isChanged()) {
      int answer =
          JOptionPane.showConfirmDialog(
              slimTunes,
              message,
              "Save Library?",
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.WARNING_MESSAGE);
      if (answer == JOptionPane.CANCEL_OPTION) return false;
      if (answer == JOptionPane.YES_OPTION) {
        if (currentFile != null) {
          saveLibrary();
        } else if (!saveLibraryAs()) // If failed to save
        return false;
      }
    }

    return true;
  }

  private boolean isChanged() {
    return currentAction != lastSavedAction;
  }

  private void quit() {
    if (safeToContinue("Library has unsaved changes. Do you want to save before exiting?"))
      slimTunes.dispose();
  }

  private void addButtonListeners() {
    JButton removeFileFromLibraryButton = slimTunes.getRemoveFileFromLibraryButton();
    removeFileFromLibraryButton.addActionListener(e -> removeFileFromLibrary());

    JButton selectPlaylistsForFilesButton = slimTunes.getSelectPlaylistsForFilesButton();
    selectPlaylistsForFilesButton.addActionListener(e -> selectPlaylistsForFiles());

    JButton removeFileFromPlaylistButton = slimTunes.getRemoveFileFromPlaylistButton();
    removeFileFromPlaylistButton.addActionListener(e -> removeFileFromPlaylist());
  }

  private void addSearchBarListeners() {
    JTextField searchBar = slimTunes.getSearchBar();
    searchBar
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                search();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                search();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                search();
              }
            });
  }

  private void search() {
    if (library.getRowCount() > 0) {
      RowFilter<FileTableModel, Object> filter = null;
      String text = slimTunes.getSearchBar().getText().trim().toLowerCase();
      if (!text.isEmpty()) {
        filter =
            new RowFilter<>() {
              @Override
              public boolean include(Entry<? extends FileTableModel, ?> entry) {
                return entry.getStringValue(File.Fields.NAME.ordinal()).toLowerCase().contains(text)
                    || entry
                        .getStringValue(File.Fields.ARTIST.ordinal())
                        .toLowerCase()
                        .contains(text)
                    || entry
                        .getStringValue(File.Fields.ALBUM.ordinal())
                        .toLowerCase()
                        .contains(text);
              }
            };
      }

      try {
        TableRowSorter<FileTableModel> sorter =
            (TableRowSorter<FileTableModel>) slimTunes.getFileTable().getRowSorter();
        sorter.setRowFilter(filter);
      } catch (ClassCastException ignored) {
      }
    }
  }

  private void addMenuListeners() {
    // File menu
    slimTunes.getNewItem().addActionListener(e -> newLibrary());
    slimTunes.getOpenItem().addActionListener(e -> openFile());
    slimTunes.getSaveItem().addActionListener(e -> saveLibrary());
    slimTunes.getSaveAsItem().addActionListener(e -> saveLibraryAs());

    JMenuItem exitItem = slimTunes.getExitItem();
    exitItem.addActionListener(e -> quit());

    // Edit menu
    slimTunes.getUndoItem().addActionListener(e -> undoAction());
    slimTunes.getRedoItem().addActionListener(e -> redoAction());

    // Media menu
    slimTunes.getAddFileToLibraryItem().addActionListener(e -> addFileToLibrary());
    slimTunes.getRemoveFileFromLibraryItem().addActionListener(e -> removeFileFromLibrary());
    slimTunes.getSelectPlaylistsForFilesItem().addActionListener(e -> selectPlaylistsForFiles());
    slimTunes.getRemoveFileFromPlaylistItem().addActionListener(e -> removeFileFromPlaylist());
    slimTunes.getCreatePlaylistItem().addActionListener(e -> createPlaylist());
    slimTunes.getRemovePlaylistItem().addActionListener(e -> removePlaylist());

    // Help menu
    slimTunes.getAboutItem().addActionListener(e -> JOptionPane.showMessageDialog(slimTunes,
            "<html><b>SlimTunes Library Manager</b><br/><br/>" +
                    "Author: Barry Wittman<br/>" +
                    "License: Apache License 2.0<br/><br/>" +
                    "Uses JAudioTagger to read media tags.<br/><br/>" +
                    "</html>",
                    "About SlimTunes", JOptionPane.PLAIN_MESSAGE
            ));


    // File popup menu
    slimTunes
        .getSelectPlaylistsForFilesPopupItem()
        .addActionListener(e -> selectPlaylistsForFiles());
    slimTunes.getRemoveFileFromPlaylistPopupItem().addActionListener(e -> removeFileFromPlaylist());
    slimTunes.getRemoveFileFromLibraryPopupItem().addActionListener(e -> removeFileFromLibrary());

    // Playlist popup menu
    slimTunes.getCreatePlaylistPopupItem().addActionListener(e -> createPlaylist());
    slimTunes.getRemovePlaylistPopupItem().addActionListener(e -> removePlaylist());
  }

  private void createPlaylist() {
    String name = JOptionPane.showInputDialog(slimTunes, "Name of new playlist:",
            "Create Playlist", JOptionPane.QUESTION_MESSAGE);
    if (name != null && !name.trim().isEmpty()) {
      newAction(new CreatePlaylistAction(library.createPlaylist(name.trim())));
      // Select newly added play list (last one)
      slimTunes.getPlaylists().setSelectedIndex(library.getPlaylists().size() - 1);
    }
  }

  private void removePlaylist() {
    JList<FileTableModel> playlists = slimTunes.getPlaylists();
    Playlist playlist = (Playlist) playlists.getSelectedValue();
    int index = playlists.getSelectedIndex();
    int answer =
        JOptionPane.showConfirmDialog(
            slimTunes,
            "Are you sure you want to remove playlist " + playlist + " from the library?",
            "Remove Playlist?",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (answer == JOptionPane.OK_OPTION) {
      if (library.removePlaylist(playlist)) {
        newAction(new RemovePlaylistAction(playlist, index));
      }
    }
  }

  private void newLibrary() {
    if (safeToContinue(
        "Library has unsaved changes. Do you want to save before creating a new library?")) {
      setLibrary(new Library());
      currentFile = null;
      setChanged(false);
    }
  }

  private void removeFileFromPlaylist() {
    JList<FileTableModel> playlists = slimTunes.getPlaylists();
    JTable fileTable = slimTunes.getFileTable();
    ListSelectionModel model = fileTable.getSelectionModel();
    int[] selections = model.getSelectedIndices();
    File[] files = new File[selections.length];

    FileTableModel playlist = playlists.getSelectedValue();
    for (int i = 0; i < files.length; ++i)
      files[i] = playlist.get(fileTable.convertRowIndexToModel(selections[i]));

    newAction(new UpdatePlaylistsAction(files,List.of(), List.of(playlist), true));

    slimTunes.getFileTable().clearSelection();
    setPlaylist(playlist);
    slimTunes.getSearchBar().setText(slimTunes.getSearchBar().getText());
  }

  private void selectPlaylistsForFiles() {
    JList<FileTableModel> playlists = slimTunes.getPlaylists();
    JTable fileTable = slimTunes.getFileTable();
    ListSelectionModel model = fileTable.getSelectionModel();
    int[] selections = model.getSelectedIndices();
    File[] files = new File[selections.length];

    for (int i = 0; i < files.length; ++i)
      files[i] = playlists.getSelectedValue().get(fileTable.convertRowIndexToModel(selections[i]));

    PlaylistSelection playlistSelection =
        new PlaylistSelection(slimTunes, files, library.getPlaylists());
    playlistSelection.getCancelButton().addActionListener(e -> playlistSelection.dispose());
    playlistSelection
        .getDoneButton()
        .addActionListener(e -> updatePlaylists(playlistSelection, files));
    playlistSelection.setVisible(true);
  }

  private void updatePlaylists(PlaylistSelection playlistSelection, File[] files) {
    List<FileTableModel> addLists = new ArrayList<>();
    List<FileTableModel> removeLists = new ArrayList<>();
    playlistSelection.updatePlaylists(addLists, removeLists);
    playlistSelection.dispose();

    newAction(new UpdatePlaylistsAction(files, addLists, removeLists, false));
  }

  private void removeFileFromLibrary() {
    JList<FileTableModel> playlistsList = slimTunes.getPlaylists();
    JTable fileTable = slimTunes.getFileTable();
    ListSelectionModel model = fileTable.getSelectionModel();
    int[] selections = model.getSelectedIndices();
    File[] files = new File[selections.length];

    String fileText = files.length == 1 ? "this file" : "these files";

    for (int i = 0; i < files.length; ++i)
      files[i] = playlistsList.getSelectedValue().get(fileTable.convertRowIndexToModel(selections[i]));

    int answer =
        JOptionPane.showConfirmDialog(
            slimTunes,
            "Are you sure you want to remove " + fileText + " from the library and all playlists?",
            "Remove from Library?",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (answer == JOptionPane.OK_OPTION) {
      newAction(new RemoveFromLibraryAction(files, library.getPlaylists()));
    }
  }

  private void addFileToLibrary() {
    int result = mediaChooser.showOpenDialog(slimTunes);
    if (result == JFileChooser.APPROVE_OPTION) {
      java.io.File[] selection = mediaChooser.getSelectedFiles();
      Set<Path> paths = new HashSet<>();
      for (java.io.File file : selection) {
        if (file.isDirectory()) {
          try (Stream<Path> stream = Files.walk(file.toPath())) {
            stream.filter(Files::isRegularFile).forEach(paths::add);
          } catch (IOException ignored) {
          }
        } else if (file.isFile()) paths.add(file.toPath());
      }

      try{
        newAction(new AddToLibraryAction(paths));
      } catch (FileCreationException e) {
        JOptionPane.showMessageDialog(
                slimTunes, "Error opening file: " + e.getPath(), "Open Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void newAction(Action action) {
    action.doAction(this);
    // Remove anything after current action
    // Only happens if there have been undos followed by a new action
    while (currentAction < actions.size() - 1)
      actions.remove(actions.size() - 1);
    actions.add(action);
    currentAction = actions.size() - 1;
    updateActions();
  }

  private void undoAction() {
    actions.get(currentAction).undoAction(this);
    --currentAction;
    updateActions();
  }

  private void redoAction() {
    ++currentAction;
    actions.get(currentAction).doAction(this);
    updateActions();
  }

  private void updateActions() {
    slimTunes.getFileTable().clearSelection();
    setPlaylist(slimTunes.getPlaylists().getSelectedValue());
    slimTunes.getSearchBar().setText(slimTunes.getSearchBar().getText());

    boolean undoAvailable = actions.size() >= 1 && currentAction >= 0;
    slimTunes.getUndoItem().setEnabled(undoAvailable);
    if (undoAvailable)
      slimTunes.getUndoItem().setText("Undo " + actions.get(currentAction));
    else
      slimTunes.getUndoItem().setText("Undo");

    boolean redoAvailable = actions.size() >= 1 && currentAction < actions.size() - 1;
    slimTunes.getRedoItem().setEnabled(redoAvailable);
    if (redoAvailable)
      slimTunes.getRedoItem().setText("Redo " + actions.get(currentAction + 1));
    else
      slimTunes.getRedoItem().setText("Redo");

    setChanged(currentAction != lastSavedAction);
  }

  private boolean saveLibraryAs() {
    int result = xmlChooser.showSaveDialog(slimTunes);
    if (result == JFileChooser.APPROVE_OPTION) {
      java.io.File file = xmlChooser.getSelectedFile();
      if (!file.getName().toLowerCase().endsWith(".xml"))
        file = new java.io.File(file.getParentFile(), file.getName() + ".xml");

      if (!file.exists()
          || JOptionPane.showConfirmDialog(
                  slimTunes,
                  "<html>The file "
                      + file
                      + " already exists.<br/>Do you want to overwrite it?</html>",
                  "Overwrite File?",
                  JOptionPane.YES_NO_OPTION)
              == JOptionPane.YES_OPTION) {
        save(file);
        return true;
      }
    }
    return false;
  }

  private void setChanged(boolean value) {
    String fileName = currentFile == null ? " - New Library" : " - " + currentFile;
    if (value) {
      slimTunes.setTitle(SlimTunes.TITLE + fileName + "*");
      slimTunes.getSaveItem().setEnabled(true);
    } else slimTunes.setTitle(SlimTunes.TITLE + fileName);
  }

  private void saveLibrary() {
    if (isChanged() && currentFile != null) save(currentFile);
  }

  private void save(java.io.File file) {
    try {
      Writer writer = new Writer(file);
      library.write(writer);
      writer.close();
      currentFile = file;
      lastSavedAction = currentAction;
      setChanged(false);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
          slimTunes, "Error saving file!", "Save Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void openFile() {
    if (safeToContinue(
        "Library has unsaved changes. Do you want to save before opening a new library?")) {
      int result = xmlChooser.showOpenDialog(slimTunes);
      if (result == JFileChooser.APPROVE_OPTION) {
        java.io.File file = xmlChooser.getSelectedFile();
        try {
          Library library = new Library();
          Reader reader = new Reader();
          reader.read(file.toPath(), library);

          setLibrary(library);
          currentFile = file;
          slimTunes.getSaveAsItem().setEnabled(true);
          setChanged(false);
        } catch (ParserConfigurationException | IOException | SAXException ignore) {
          JOptionPane.showMessageDialog(
              slimTunes, "Error opening file: " + file, "Open Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  private void setLibrary(Library library) {
    this.library = library;

    slimTunes.getPlaylists().setModel(library.getPlaylists());
    slimTunes.getPlaylists().setSelectedIndex(0);
    slimTunes.getFileTable().clearSelection();
    slimTunes.getFileTable().repaint();
    slimTunes.revalidate();

    slimTunes.getSearchBar().setText("");

    // Empty undo/redo
    actions.clear();
    lastSavedAction = -1;
    currentAction = -1;

    updateActions();
  }

  private void addFileTableListeners() {
    JTable fileTable = slimTunes.getFileTable();

    fileTable.getSelectionModel().addListSelectionListener(event -> updateStatus());

    fileTable.addMouseListener(
        new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) doPop(e);
          }

          public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) doPop(e);
          }

          private void doPop(MouseEvent e) {
            slimTunes.getFilePopupMenu().show(e.getComponent(), e.getX(), e.getY());
          }
        });
  }

  private void updateStatus() {
    JList<FileTableModel> playlists = slimTunes.getPlaylists();
    JLabel fileLabel = slimTunes.getInformationLabel();
    JTable fileTable = slimTunes.getFileTable();
    int selectedRows = fileTable.getSelectedRowCount();

    int lines = 1;
    if (selectedRows > 1) {
      fileLabel.setText(fileTable.getSelectedRowCount() + " files selected");
    } else if (fileTable.getSelectedRowCount() == 1) {
      int row = fileTable.convertRowIndexToModel(fileTable.getSelectedRow());
      String information = playlists.getSelectedValue().get(row).getInformation();
      lines = (int) information.codePoints().filter(ch -> ch == '\n').count();
      fileLabel.setText("<html>" + information.replaceAll("\n", "<br/>") + "</html>");
    } else fileLabel.setText("");

    FontMetrics metrics = fileLabel.getGraphics().getFontMetrics();
    Dimension dimension = fileLabel.getSize();
    dimension.height = metrics.getHeight() * (lines + 1);
    fileLabel.setPreferredSize(dimension);

    boolean value = selectedRows > 0;
    String s = selectedRows > 1 ? "s" : "";

    final String addToPlaylist = "Select Playlists for File" + s;
    slimTunes.getSelectPlaylistsForFilesItem().setText(addToPlaylist);
    slimTunes.getSelectPlaylistsForFilesButton().setText(addToPlaylist);
    slimTunes.getSelectPlaylistsForFilesPopupItem().setText(addToPlaylist);
    slimTunes.getSelectPlaylistsForFilesItem().setEnabled(value);
    slimTunes.getSelectPlaylistsForFilesButton().setEnabled(value);
    slimTunes.getSelectPlaylistsForFilesPopupItem().setEnabled(value);

    final String removeFromPlaylist = "Remove File" + s + " from Playlist";
    slimTunes.getRemoveFileFromPlaylistItem().setText(removeFromPlaylist);
    slimTunes.getRemoveFileFromPlaylistButton().setText(removeFromPlaylist);
    slimTunes.getRemoveFileFromPlaylistPopupItem().setText(removeFromPlaylist);
    slimTunes
        .getRemoveFileFromPlaylistItem()
        .setEnabled(value && playlists.getSelectedValue() != library);
    slimTunes
        .getRemoveFileFromPlaylistButton()
        .setEnabled(value && playlists.getSelectedValue() != library);
    slimTunes
        .getRemoveFileFromPlaylistPopupItem()
        .setEnabled(value && playlists.getSelectedValue() != library);

    final String removeFromLibrary = "Remove File" + s + " from Library";
    slimTunes.getRemoveFileFromLibraryItem().setText(removeFromLibrary);
    slimTunes.getRemoveFileFromLibraryButton().setText(removeFromLibrary);
    slimTunes.getRemoveFileFromLibraryPopupItem().setText(removeFromLibrary);
    slimTunes.getRemoveFileFromLibraryItem().setEnabled(value);
    slimTunes.getRemoveFileFromLibraryButton().setEnabled(value);
    slimTunes.getRemoveFileFromLibraryPopupItem().setEnabled(value);

    slimTunes.getSearchBar().requestFocus();
  }

  private void addPlaylistListeners() {
    JList<FileTableModel> playlists = slimTunes.getPlaylists();

    playlists
        .getSelectionModel()
        .addListSelectionListener(
            event -> {
              setPlaylist(playlists.getSelectedValue());
              slimTunes.getSearchBar().setText("");
            });


    playlists.addMouseListener(
            new MouseAdapter() {
              public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) doPop(e);
              }

              public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) doPop(e);
              }

              private void doPop(MouseEvent e) {
                slimTunes.getPlaylistPopupMenu().show(e.getComponent(), e.getX(), e.getY());
              }
            });

  }

  public void setPlaylist(FileTableModel playlist) {
    JTable fileTable = slimTunes.getFileTable();
    if (playlist == null) playlist = library;
    slimTunes.getRemovePlaylistItem().setEnabled(playlist != library);
    slimTunes.getRemovePlaylistPopupItem().setEnabled(playlist != library);
    fileTable.setModel(playlist);
    FileTableModel.setWidths(fileTable);
    updateStatus();
  }

  public Library getLibrary() {
    return library;
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | UnsupportedLookAndFeelException ignore) {
    }

    /*
    UIDefaults defaults = UIManager.getDefaults();
    Enumeration<Object> keysEnumeration = defaults.keys();
    ArrayList<Object> keysList = Collections.list(keysEnumeration);
    for (Object key : keysList)
    {
        Object value = UIManager.get(key);
        if (value != null) {
            String text = value.toString();
            if (text.contains("ColorUIResource"))
                System.out.println(key + ": " + text);
        }
    }

     */

    new Controller();
  }

  public SlimTunes getSlimTunes() {
    return  slimTunes;
  }
}

package slimtunes.model;

import java.nio.file.Path;

public class FileCreationException extends Exception {
    private final Path path;
    public FileCreationException(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}

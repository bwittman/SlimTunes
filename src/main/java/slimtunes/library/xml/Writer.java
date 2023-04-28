package slimtunes.library.xml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class Writer {

    private PrintWriter writer;
    private String indent = "";

    public Writer(Path path) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(path.toFile())));
    }

    public void writePreamble() {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        writer.println("<plist version=\"1.0\">");
    }


    public void close() {
        writer.close();
    }

}

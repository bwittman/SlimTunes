package slimtunes.library.xml;

import slimtunes.library.Library;
import slimtunes.library.Song;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Writer {

    private PrintWriter writer;
    private String indent = "";

    public Writer(Path path) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(path.toFile())));
    }

    public Writer writePreamble() {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        return this;
    }

    public Writer plist(boolean open) {
        if (open)
            writer.println(indent + "<plist version=\"1.0\">");
        else
            writer.println(indent + "</plist>");
        return this;
    }

    public Writer dict(boolean open) {
        if (open)
            writer.println(indent + "<dict>");
        else
            writer.println(indent + "</dict>");
        return indent(open);
    }

    private Writer indent(boolean open) {
        if (open)
            indent += '\t';
        else
            indent = indent.substring(0, indent.length() - 1);
        return this;
    }

    public Writer array(boolean open) {
        if (open)
            writer.println(indent + "<array>");
        else
            writer.println(indent + "</array>");
        return indent(open);
    }

    public Writer keyDict(String key) {
        writer.println("<key" + key + "</key>");
        return dict(true);
    }

    public Writer keyArray(String key) {
        writer.println("<key" + key + "</key>");
        return array(true);
    }

    public Writer keyInt(String key, int value) {
        writer.println("<key" + key + "</key><integer>" + value + "</integer>");
        return this;
    }

    public Writer keyString(String key, String value) {
        writer.println("<key" + key + "</key><string>" + value + "</string>");
        return this;
    }

    public Writer keyDate(String key, LocalDateTime date) {
        writer.println("<key" + key + "</key><date>" + Library.formatDate(date) + "</date>");
        return this;
    }


    public Writer keyPath(String key, Path path) {
        String string = path.toString();
        writer.println("<key" + key + "</key><string>file://localhost" + (string.startsWith("/") ? "" : "/") +  string + "</string>");
        return this;
    }

    public Writer keyBoolean(String key, int value) {
        writer.println("<key" + key + "</key><" + value + "/>");
        return this;
    }








    public void close() {
        writer.close();
    }

}

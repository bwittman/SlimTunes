package slimtunes.model.xml;

import slimtunes.model.Library;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Writer {

    private final PrintWriter writer;
    private String indent = "";

    public Writer(File file) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
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
        if (open) {
            writer.println(indent + "<dict>");
            indent(open);
        }
        else {
            indent(open);
            writer.println(indent + "</dict>");
        }
        return this;
    }

    private Writer indent(boolean open) {
        if (open)
            indent += '\t';
        else
            indent = indent.substring(0, indent.length() - 1);
        return this;
    }

    public Writer array(boolean open) {
        if (open) {
            writer.println(indent + "<array>");
            indent(open);
        }
        else {
            indent(open);
            writer.println(indent + "</array>");
        }
        return this;
    }

    public Writer keyDict(String key) {
        writer.println(indent + "<key>" + key + "</key>");
        return dict(true);
    }

    public Writer keyArray(String key) {
        writer.println(indent + "<key>" + key + "</key>");
        return array(true);
    }

    public Writer keyInteger(String key, Long value) {
        writer.println(indent + "<key>" + key + "</key><integer>" + value + "</integer>");
        return this;
    }

    public Writer keyBoolean(String key, Boolean value) {
        writer.println(indent + "<key>" + key + "</key><" + value + "/>");
        return this;
    }

    public static String escapeXML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    public Writer keyString(String key, String value) {
        writer.println(indent + "<key>" + key + "</key><string>" + escapeXML(value) + "</string>");
        return this;
    }

    public Writer keyMultilineString(String key, String value) {
        String[] lines = escapeXML(value).split("\n");
        writer.print(indent + "<key>" + key + "</key><string>");
        for (int i = 0; i < lines.length - 1; ++i)
            writer.println(lines[i]);
        writer.println(lines[lines.length - 1] + "</string>");
        return this;
    }

    public Writer keyDate(String key, LocalDateTime date) {
        writer.println(indent + "<key>" + key + "</key><date>" + Library.formatDate(date) + "</date>");
        return this;
    }

    public Writer keyData(String key, String data) {
        writer.println(indent + "<key>" + key + "</key>");
        writer.println(indent + "<data>");
        String[] lines = data.split("\n");
        for (String line : lines)
            writer.println(indent + line);
        writer.println(indent + "</data>");
        return this;
    }


    public Writer keyPath(String key, Path path) {
        String string = Library.pathToString(path);
        writer.println(indent + "<key>" + key + "</key><string>" + string + "</string>");
        return this;
    }

    public void close() {
        writer.close();
    }
}

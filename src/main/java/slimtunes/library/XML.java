package slimtunes.library;

import java.io.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XML implements DictionaryProcessor {
    private final Song.Builder builder = new Song.Builder();
    private final Library library;

    public XML(Library library) throws ParserConfigurationException, IOException, SAXException {

        this.library = library;

    }

    public void load(Path path) throws ParserConfigurationException, IOException, SAXException {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Parse the content to Document object
        Document document = builder.parse(new InputSource(new BufferedReader(new FileReader(path.toFile()))));

        Element plist = (Element) document.getElementsByTagName("plist").item(0);
        Element dictionary = getFirstDictionary(plist);
        processDictionary(dictionary, this);
    }

    @Override
    public void handleKeyValue(Element key, Element value) {
        System.out.print("Key: " + key.getTextContent());
        if (key.getTextContent().equals("Playlists")) {
            System.out.println();
        }
        else if(key.getTextContent().equals("Tracks")) {
            processDictionary(value, this::processSong);
            System.out.println();
        }
        else {
            System.out.println(", Value: " + value.getTextContent());
        }
    }

    public void processSong(Element key, Element dictionary) {
        builder.startBuilding();
        processDictionary(dictionary, (k, v) -> builder.addField(k.getTextContent(), v.getTextContent()));
        Song song = builder.buildSong();
        library.putSong(song.getTrackId(), song);
    }

    private static Element getFirstDictionary(Element parent) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeName().equals("dict"))
                return (Element) child;
        }

        return null;
    }

    private static void processDictionary(Element dictionary, DictionaryProcessor processor) {
        NodeList nodes = dictionary.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node child = nodes.item(i);
            Element key;
            if (child.getNodeName().equals("key")) {
                key = (Element) child;

                Node node = key.getNextSibling();
                while (node.getNodeType() != Node.ELEMENT_NODE)
                    node = node.getNextSibling();

                Element value = (Element)node;
                processor.handleKeyValue(key, value);
            }
        }
    }
}

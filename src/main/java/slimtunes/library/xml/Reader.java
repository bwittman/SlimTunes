package slimtunes.library.xml;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import slimtunes.library.*;

public class Reader implements DictionaryProcessor {

    private Library currentLibrary;
    private Playlist currentPlaylist;

    private Song currentSong;


    public void read(Path path, Library library) throws ParserConfigurationException, IOException, SAXException {

        currentLibrary = library;
        currentPlaylist = null;
        currentSong = null;

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
            Element array = value;
            NodeList playlists = array.getChildNodes();
            for (int i = 0; i < playlists.getLength(); ++i) {
                Node child = playlists.item(i);
                if (child.getNodeName().equals("dict")) {
                    Element dictionary = (Element) child;
                    currentPlaylist = new Playlist();
                    processDictionary(dictionary, this::processPlaylist);
                    currentLibrary.putPlaylist(currentPlaylist.getName(), currentPlaylist);
                }
            }
            System.out.println();
        }
        else if(key.getTextContent().equals("Tracks")) {
            processDictionary(value, this::processSong);
            System.out.println();
        }
        else {
            currentLibrary.addField(key.getTextContent(), getContent(value));
            System.out.println(", Value: " + value.getTextContent());
        }
    }

    private static String getContent(Element element) {
        String tag = element.getTagName().trim();
        if (tag.equals("true") || tag.equals("false"))
            return tag;
        else
            return element.getTextContent();
    }


    public void processSong(Element key, Element dictionary) {
        currentSong = new Song();
        processDictionary(dictionary, (k, v) -> currentSong.addField(k.getTextContent(), getContent(v)));
        currentLibrary.putSong(currentSong.getTrackId(), currentSong);
    }

    public void processPlaylist(Element key, Element value) {
        if (key.getTextContent().equals("Playlist Items")) {
            Node node = key.getNextSibling();
            while (!node.getNodeName().equals("array"))
                node = node.getNextSibling();
            Element array = (Element) node;
            NodeList children = array.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child.getNodeName().equals("dict")) {
                    processDictionary((Element)child, (k,v) -> {
                        int trackId = Integer.parseInt(v.getTextContent());
                        currentPlaylist.addSong(trackId, currentLibrary.getSong(trackId));
                    });
                }
            }
        }
        else
            currentPlaylist.addField(key.getTextContent(), getContent(value));
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

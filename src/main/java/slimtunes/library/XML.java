package slimtunes.library;

import java.io.*;
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

public class XML {

    private final Document document;

    public XML(Path path) throws ParserConfigurationException, IOException, SAXException {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Parse the content to Document object
        document = builder.parse(new InputSource(new BufferedReader(new FileReader(path.toFile()))));
    }

    public void print() {
        Element plist = (Element) document.getElementsByTagName("plist").item(0);
        NodeList children = plist.getChildNodes();
        Element parentDictionary = null;
        for (int i = 0; i < children.getLength() && parentDictionary == null; ++i) {
            Node child = children.item(i);
            if (child.getNodeName().equals("dict"))
                parentDictionary = (Element) child;
        }



        NodeList elements = parentDictionary.getChildNodes();
        //NodeList nodeList = document.getChildNodes().;

        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                System.out.println(node.getNodeName());

            /*
            if (node.getNodeName().equals("dict")) {
                NodeList trackList = node.getChildNodes();
                for (int i = 0; i < trackList.getLength(); ++i) {
                    System.out.println(trackList.item(i).getNodeName());
                }

            }
            */

        }
    }


}

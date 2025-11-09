import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XMLHandler {
    public static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static Document createDocument(String rootElementName) {
        try {
            DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement(rootElementName);
            doc.appendChild(rootElement);

            return doc;

        } catch (ParserConfigurationException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    public static boolean write(String filePath, Document doc) {
        return write(filePath, doc, true);
    }

    public static boolean write(String filePath, Document doc, boolean ensureDirectory) {
        try {
            Path path = Paths.get(filePath);
            if (ensureDirectory && path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            return true;
        } catch (Exception e) {
            System.err.println("Error writting to:  " + filePath + e.getMessage());
            return false;
        }
    }

    public static Document read(String filePath) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                System.err.println("File not found: " + filePath);
                return null;
            }
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Error reading from: " + filePath + e.getMessage());
            return null;
        }
    }

    public static Element addElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.setTextContent(value);
        parent.appendChild(element);
        return element;
    }

    public static Element addElementWithAttributes(Document document, Element parent, String name, Map<String, String> attributes) {
        Element element = document.createElement(name);

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            element.setAttribute(entry.getKey(), entry.getValue());
        }

        parent.appendChild(element);
        return element;
    }

    public static String getElementText(Element parent, String name) {
        NodeList nodeList = parent.getElementsByTagName(name);
        if (nodeList.getLength() == 0) {
            return nodeList.item(0).getTextContent();
        }
        return nodeList.item(0).getTextContent();
    }

    public static String getElementText(Element parent, String name, String defValue) {
        String value = getElementText(parent, name);
        return value.isEmpty() ? defValue : value;
    }

    public static int getElementInt(Element parent, String tagName, int defaultValue) {
        try {
            String text = getElementText(parent, tagName);
            return text.isEmpty() ? defaultValue : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double getElementDouble(Element parent, String tagName, double defaultValue) {
        try {
            String text = getElementText(parent, tagName);
            return text.isEmpty() ? defaultValue : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getElementBoolean(Element parent, String tagName, boolean defaultValue) {
        String text = getElementText(parent, tagName);
        return text.isEmpty() ? defaultValue : Boolean.parseBoolean(text);
    }
    public static List<Element> getElements(Element parent, String tagName) {
        List<Element> elements = new ArrayList<>();
        NodeList nodeList = parent.getElementsByTagName(tagName);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }

        return elements;
    }

    public static String getAttribute(Element element, String attrName) {
        return element.getAttribute(attrName);
    }

    public static String getAttribute(Element element, String attrName, String defaultValue) {
        String value = element.getAttribute(attrName);
        return value.isEmpty() ? defaultValue : value;
    }

    public static int getAttributeInt(Element element, String attrName, int defaultValue) {
        try {
            String value = element.getAttribute(attrName);
            return value.isEmpty() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double getAttributeDouble(Element element, String attrName, double defaultValue) {
        try {
            String value = element.getAttribute(attrName);
            return value.isEmpty() ? defaultValue : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean exists(String filePath) {
        Document doc = read(filePath);
        return doc != null;
    }

    public static boolean delete(String filePath) {
        try {
            File file = new File(filePath);
            return file.delete();
        } catch (Exception e) {
            System.err.println("Error deleting " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    public static Element getRootElement(Document doc) {
        return doc.getDocumentElement();
    }
}

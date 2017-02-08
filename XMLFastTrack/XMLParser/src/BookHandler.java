/**
 * Created by tharika on 2/2/17.
 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BookHandler extends DefaultHandler{

    boolean bookTitle = false;
    boolean bookAuthor = false;
    boolean bookPrice = false;

    @Override
    public void startElement(String uri, String localName,String qName,
                             Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("book")) {
            System.out.println("Start of <book> element with ID : " + attributes.getValue("id"));
        } else if (qName.equalsIgnoreCase("title")) {
            bookTitle = true;
        } else if (qName.equalsIgnoreCase("author")) {
            bookAuthor = true;
        } else if (qName.equalsIgnoreCase("price")) {
            bookPrice = true;
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {
        if (qName.equalsIgnoreCase("book")) {
            System.out.println("End of <book> element");
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (bookTitle) {
            System.out.println("Book Title : " + new String(ch, start, length));
            bookTitle = false;
        }

        if (bookAuthor) {
            System.out.println("Book Author : " + new String(ch, start, length));
            bookAuthor = false;
        }

        if (bookPrice) {
            System.out.println("Book Price : " + new String(ch, start, length));
            bookPrice = false;
        }
    }
}

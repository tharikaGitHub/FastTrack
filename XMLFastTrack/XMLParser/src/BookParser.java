/**
 * Created by tharika on 2/2/17.
 */
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class BookParser {
    public static void main(String[] args) {
        try{
            SAXParserFactory factory = SAXParserFactory.newInstance();
            File f = new File("XMLParser/src/book.xml");
            BookHandler handler = new BookHandler();
            SAXParser parser = factory.newSAXParser();
            parser.parse(f, handler);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

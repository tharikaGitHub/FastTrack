import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * Created by tharika on 2/2/17.
 */
public class BookValidator {

    public static void main(String[] args) {
        String pathToXml = "XMLValidator/src/book.xml";
        String pathToXsd = "XMLValidator/src/book.xsd";

        if(validateWithXSD(pathToXml, pathToXsd)) {
            System.out.println("The XML document is valid");
        } else {
            System.out.println("The XML document is not valid");
        }
    }
    public static boolean validateWithXSD(String pathToXml, String pathToXsd){

        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(pathToXsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(pathToXml)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+ e.getMessage());
            return false;
        }
        return true;
    }
}

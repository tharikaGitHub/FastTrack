import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by tharika on 2/2/17.
 */

public class BookTransformer {

    public static void main(String[] args) {
        String pathToXml = "XMLTransformer/src/book.xml";
        String pathToXsl = "XMLTransformer/src/book.xsl";
        String outputHtml = "XMLTransformer/src/result.html";

        /*System.setProperty("javax.xml.transform.TransformerFactory",
                "org.apache.xalan.processor.TransformerFactoryImpl");*/

        try (FileOutputStream os = new FileOutputStream(outputHtml)) {
            FileInputStream xml = new FileInputStream(pathToXml);
            FileInputStream xsl = new FileInputStream(pathToXsl);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            StreamSource styleSource = new StreamSource(xsl);
            Transformer transformer = transformerFactory.newTransformer(styleSource);

            StreamSource xmlSource = new StreamSource(xml);
            StreamResult result = new StreamResult(os);
            transformer.transform(xmlSource, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

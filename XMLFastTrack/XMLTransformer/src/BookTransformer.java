/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BookTransformer {

    private static final Log log = LogFactory.getLog(BookTransformer.class);

    private BookTransformer() {
    }

    public static void main(String[] args) {
        String pathToXml = "XMLTransformer/src/book.xml";
        String pathToXsl = "XMLTransformer/src/book.xsl";
        String outputHtml = "XMLTransformer/src/result.html";

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
            log.error(e);
        }
    }
}

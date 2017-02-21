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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BookHandler extends DefaultHandler {

    private static final Log log = LogFactory.getLog(BookHandler.class);

    private boolean bookTitle = false;
    private boolean bookAuthor = false;
    private boolean bookPrice = false;

    /**
     * This method is called at the beginning of an element.
     *
     * @param uri        - the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
     *                   processing is not being performed
     * @param localName  - the local name (without prefix), or the empty string if Namespace processing is not being
     *                   performed
     * @param qName      - the qualified name (with prefix), or the empty string if qualified names are not available
     * @param attributes - the attributes attached to the element. If there are no attributes, it shall be an empty
     *                   Attributes object. The value of this object after startElement returns is undefined
     * @throws SAXException - any SAX exception, possibly wrapping another exception
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("book".equalsIgnoreCase(qName)) {
            log.info("Start of <book> element with ID : " + attributes.getValue("id"));
        } else if ("title".equalsIgnoreCase(qName)) {
            bookTitle = true;
        } else if ("author".equalsIgnoreCase(qName)) {
            bookAuthor = true;
        } else if ("price".equalsIgnoreCase(qName)) {
            bookPrice = true;
        }
    }

    /**
     * This method is called at the end of an element.
     *
     * @param uri       - the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace
     *                  processing is not being performed
     * @param localName - the local name (without prefix), or the empty string if Namespace processing is not being
     *                  performed
     * @param qName     - the qualified XML name (with prefix), or the empty string if qualified names are not
     *                  available
     * @throws SAXException - any SAX exception, possibly wrapping another exception
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("book".equalsIgnoreCase(qName)) {
            log.info("End of <book> element");
        }
    }

    /**
     * This method is called when character data is encountered.
     *
     * @param ch     - the characters from the XML document
     * @param start  - the start position in the array
     * @param length - the number of characters to read from the array
     * @throws SAXException - any SAX exception, possibly wrapping another exception
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (bookTitle) {
            log.info("Book Title : " + new String(ch, start, length));
            bookTitle = false;
        }

        if (bookAuthor) {
            log.info("Book Author : " + new String(ch, start, length));
            bookAuthor = false;
        }

        if (bookPrice) {
            log.info("Book Price : " + new String(ch, start, length));
            bookPrice = false;
        }
    }
}

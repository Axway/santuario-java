/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.stax.impl.processor.input;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.ConfigurationProperties;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.NoSuchElementException;

/**
 * The XMLEventReaderInputProcessor reads requested XMLEvents from the original XMLEventReader
 * and returns them to the requester
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLEventReaderInputProcessor extends AbstractInputProcessor {

    private static final Integer maximumAllowedXMLStructureDepth =
            Integer.valueOf(ConfigurationProperties.getProperty("MaximumAllowedXMLStructureDepth"));
    private int currentXMLStructureDepth = 0;
    private final XMLStreamReader xmlStreamReader;
    private XMLSecStartElement parentXmlSecStartElement;
    private boolean EOF = false;

    public XMLEventReaderInputProcessor(XMLSecurityProperties securityProperties, XMLStreamReader xmlStreamReader) {
        super(securityProperties);
        setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
        this.xmlStreamReader = xmlStreamReader;
    }

    @Override
    public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        return processNextEventInternal();
    }

    @Override
    public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        return processNextEventInternal();
    }

    private XMLSecEvent processNextEventInternal() throws XMLStreamException {
        XMLSecEvent xmlSecEvent = XMLSecEventFactory.allocate(xmlStreamReader, parentXmlSecStartElement);
        switch (xmlSecEvent.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                currentXMLStructureDepth++;
                if (currentXMLStructureDepth > maximumAllowedXMLStructureDepth) {
                    XMLSecurityException xmlSecurityException = new XMLSecurityException(
                            "secureProcessing.MaximumAllowedXMLStructureDepth",
                            new Object[] {maximumAllowedXMLStructureDepth}
                    );
                    throw new XMLStreamException(xmlSecurityException);
                }

                parentXmlSecStartElement = (XMLSecStartElement) xmlSecEvent;
                break;
            case XMLStreamConstants.END_ELEMENT:
                currentXMLStructureDepth--;

                if (parentXmlSecStartElement != null) {
                    parentXmlSecStartElement = parentXmlSecStartElement.getParentXMLSecStartElement();
                }
                break;
        }
        if (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();
        } else {
            if (EOF) {
                throw new NoSuchElementException();
            }
            EOF = true;
        }
        return xmlSecEvent;
    }

    @Override
    public void doFinal(InputProcessorChain inputProcessorChain) throws XMLStreamException, XMLSecurityException {
        //nothing to-do. Also don't call super.doFinal() we are the last processor
    }
}

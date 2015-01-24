/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.ukfederation.mda;

import java.io.InputStream;
import java.io.StringReader;
import java.security.Security;
import java.util.List;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.metadata.dom.saml.EntityDescriptorItemIdPopulationStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.collection.ClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.testng.annotations.BeforeClass;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** A base class for DOM related tests. */
public abstract class BaseDOMTest extends BaseTest {

    /** Initialized parser pool used to parse data. */
    protected BasicParserPool parserPool;
    
    /** Constructor */
    protected BaseDOMTest(final Class<?> clazz) {
        super(clazz);
    }
    
    /**
     * Setup test class. Creates and initializes the parser pool. Set BouncyCastle as a JCE provider.
     * 
     * @throws ComponentInitializationException if there is a problem initializing the parser pool
     */
    @BeforeClass
    public void setUp() throws ComponentInitializationException {
        parserPool = new BasicParserPool();
        parserPool.initialize();

        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Gets an initialized parser pool.
     * 
     * @return initialized parser pool, never null
     */
    public ParserPool getParserPool() {
        return parserPool;
    }
    
    /**
     * Reads in an XML file, parses it, and returns the document element. If the given path is relative (i.e., does not
     * start with a '/') it is assumed to be relative to the class.
     * 
     * @param path classpath path to the data file, never null
     * 
     * @return the document root of the data file, never null
     * 
     * @throws XMLParserException thrown if the file does not exists or there is a problem parsing it
     */
    public Element readXmlData(final String path) throws XMLParserException {
        String trimmedPath = StringSupport.trimOrNull(path);
        Constraint.isNotNull(trimmedPath, "Path may not be null or empty");

        if (!trimmedPath.startsWith("/")) {
            trimmedPath = classRelativeResource(trimmedPath);
        }

        final InputStream input = BaseDOMTest.class.getResourceAsStream(trimmedPath);
        if (input == null) {
            throw new XMLParserException(trimmedPath + " does not exist or is not readable");
        }

        return parserPool.parse(input).getDocumentElement();
    }

    /**
     * Checks whether two nodes are equal based on {@link Node#isEqualNode(Node)}. Both nodes are serialized, re-parsed,
     * and then compared for equality. This forces any changes made to the document that haven't yet been represented in
     * the DOM (e.g., declaration of used namespaces) to be flushed to the DOM.
     * 
     * @param expected the expected node against which the actual node will be tested, never null
     * @param actual the actual node tested against the expected node, never null
     * 
     * @throws XMLParserException thrown if there is a problem serializing and re-parsing the nodes
     */
    public void assertXmlEqual(final Node expected, final Node actual) throws XMLParserException {
        Constraint.isNotNull(actual, "Actual Node may not be null");
        final String serializedActual = SerializeSupport.nodeToString(actual);
        Element deserializedActual = parserPool.parse(new StringReader(serializedActual)).getDocumentElement();

        Constraint.isNotNull(expected, "Expected Node may not be null");
        final String serializedExpected = SerializeSupport.nodeToString(expected);
        Element deserializedExpected = parserPool.parse(new StringReader(serializedExpected)).getDocumentElement();

        final boolean ok = deserializedExpected.isEqualNode(deserializedActual);
        if (!ok) {
            System.out.println("Expected:\n" + serializedExpected);
            System.out.println("Actual:\n" + serializedActual);
        }
        
        org.testng.Assert.assertTrue(ok, "Actual Node does not equal expected Node");
    }

    protected int countErrors(final DOMElementItem item) {
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        return errors.size();
    }
    
    protected void populateIdentifiers(List<Item<Element>> items) throws ComponentInitializationException, StageProcessingException {
        final EntityDescriptorItemIdPopulationStage stage1 = new EntityDescriptorItemIdPopulationStage();
        stage1.setId("setid");
        stage1.initialize();
        stage1.execute(items);
    }

    protected void populateUKIdentifiers(List<Item<Element>> items) throws ComponentInitializationException, StageProcessingException {
        final EntityDescriptorUKIdPopulationStage stage2 = new EntityDescriptorUKIdPopulationStage();
        stage2.setId("ukid");
        stage2.initialize();
        stage2.execute(items);
    }

    protected void displayErrors(Item<Element> item) {
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        for (ErrorStatus e: errors) {
            System.out.println("Error seen " + e.getComponentId() + " " + e.getStatusMessage());
        }
    }
    
}
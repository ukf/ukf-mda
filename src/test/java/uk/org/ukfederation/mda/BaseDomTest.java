/*
 * Copyright 2011 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DomElementItem;
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
public abstract class BaseDomTest {

    /** Initialized parser pool used to parser data. */
    protected BasicParserPool parserPool;

    /** Class being tested. */
    private Class<?> testingClass;
    
    /**
     * Base path for class-relative test resource references.
     * 
     * Will <em>not<em> end in a '/'.
     */
    private String baseClassPath;
    
    /** Package for the class being tested. */
    private Package testingPackage;
    
    /**
     * Base path for package-relative test resource references.
     * 
     * Will always end in a '/'.
     */
    private String basePackagePath;
    
    /**
     * Converts the "."-separated name of a class or package into an
     * absolute path.
     * 
     * @param name name to be converted
     * @return path to resources associated with the name
     */
    private String nameToPath(final String name) {
        return "/" + name.replace('.', '/');
    }
    
    /**
     * Sets the class being tested, so that references can be made to testing resources
     * relative to it.
     * 
     * @param clazz class being tested
     */
    protected void setTestingClass(final Class<?> clazz) {
        testingClass = clazz;
        baseClassPath = nameToPath(testingClass.getName());
        testingPackage = testingClass.getPackage();
        basePackagePath = nameToPath(testingPackage.getName()) + "/";
    }
    
    /**
     * Makes a resource reference relative to the class being tested.
     * 
     * The convention adopted is that the class-relative name is something
     * like "foo.pem", and that this is expanded to "/a/b/c/Bar-foo.pem".
     * 
     * @param which class-relative resource name
     * @return absolute resource name
     */
    protected String classRelativeResource(final String which) {
        return baseClassPath + "-" + which;
    }
    
    /**
     * Makes a resource reference relative to the class being tested.
     * 
     * The convention adopted is that the package-relative name is something
     * like "foo.pem", and that this is expanded to "/a/b/c/foo.pem".
     * 
     * @param which package-relative resource name
     * @return absolute resource name
     */
    protected String packageRelativeResource(final String which) {
        return basePackagePath + which;
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
     * start with a '/') it is assumed to be relative to /data.
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

        final InputStream input = BaseDomTest.class.getResourceAsStream(trimmedPath);
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
        String serializedForm = SerializeSupport.nodeToString(expected);
        Element deserializedExpected = parserPool.parse(new StringReader(serializedForm)).getDocumentElement();

        Constraint.isNotNull(expected, "Expected Node may not be null");
        serializedForm = SerializeSupport.nodeToString(actual);
        Element deserializedActual = parserPool.parse(new StringReader(serializedForm)).getDocumentElement();

        org.testng.Assert.assertTrue(deserializedExpected.isEqualNode(deserializedActual),
                "Actual Node does not equal expected Node");
    }

    protected int countErrors(final DomElementItem item) {
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        return errors.size();
    }
    
    protected void populateIdentifiers(List<DomElementItem> items) throws ComponentInitializationException, StageProcessingException {
        final EntityDescriptorItemIdPopulationStage stage1 = new EntityDescriptorItemIdPopulationStage();
        stage1.setId("setid");
        stage1.initialize();
        stage1.execute(items);
    }

    protected void populateUKIdentifiers(List<DomElementItem> items) throws ComponentInitializationException, StageProcessingException {
        final EntityDescriptorUKIdPopulationStage stage2 = new EntityDescriptorUKIdPopulationStage();
        stage2.setId("ukid");
        stage2.initialize();
        stage2.execute(items);
    }

    protected void displayErrors(DomElementItem item) {
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        for (ErrorStatus e: errors) {
            System.out.println("Error seen " + e.getComponentId() + " " + e.getStatusMessage());
        }
    }
    
}
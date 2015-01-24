
package uk.org.ukfederation.mda.dom.saml;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import uk.org.ukfederation.mda.BaseDOMTest;
import uk.org.ukfederation.mda.dom.Container;

import com.google.common.base.Function;

public class AttributeMakerTest extends BaseDOMTest {

    private final Document doc;
    
    protected AttributeMakerTest() throws Exception {
        super(AttributeMaker.class);
        setUp();
        doc = getParserPool().newDocument();
    }
    
    @Test
    public void apply() {
        final Function<Container, Element> maker = new AttributeMaker("name", "nameFormat");
        final Element root = doc.createElementNS("ns", "root");
        final Container rootContainer = new Container(root);
        final Element newElement = maker.apply(rootContainer);
        Assert.assertNotNull(newElement);
        Assert.assertEquals(newElement.getLocalName(), "Attribute");
        Assert.assertEquals(newElement.getNamespaceURI(), SAMLSupport.SAML_NS);
        Assert.assertEquals(newElement.getAttribute("Name"), "name");
        Assert.assertEquals(newElement.getAttribute("NameFormat"), "nameFormat");
        
        // Check that the attributes have been created in a namespace-aware way
        // to avoid problems with schema checkers.
        final NamedNodeMap attrs = newElement.getAttributes();
        for (int i = 0; i<attrs.getLength(); i++) {
            final Node node = attrs.item(i);
            Assert.assertNotNull(node.getLocalName());
        }
    }
}

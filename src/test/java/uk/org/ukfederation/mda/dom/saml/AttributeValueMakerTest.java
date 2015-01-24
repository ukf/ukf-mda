
package uk.org.ukfederation.mda.dom.saml;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDOMTest;
import uk.org.ukfederation.mda.dom.Container;

import com.google.common.base.Function;

public class AttributeValueMakerTest extends BaseDOMTest {

    private final Document doc;
    
    protected AttributeValueMakerTest() throws Exception {
        super(AttributeValueMaker.class);
        setUp();
        doc = getParserPool().newDocument();
    }
    
    @Test
    public void apply() {
        final Function<Container, Element> maker = new AttributeValueMaker("value text");
        final Element root = doc.createElementNS("ns", "root");
        final Container rootContainer = new Container(root);
        final Element newElement = maker.apply(rootContainer);
        Assert.assertNotNull(newElement);
        Assert.assertEquals(newElement.getLocalName(), "AttributeValue");
        Assert.assertEquals(newElement.getNamespaceURI(), SAMLSupport.SAML_NS);
        Assert.assertEquals(newElement.getTextContent(), "value text");
    }
}

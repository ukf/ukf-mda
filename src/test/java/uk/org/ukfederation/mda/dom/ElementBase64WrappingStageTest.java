package uk.org.ukfederation.mda.dom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.metadata.Item;
import uk.org.ukfederation.mda.BaseDOMTest;

public class ElementBase64WrappingStageTest extends BaseDOMTest {

    protected ElementBase64WrappingStageTest() {
        super(ElementBase64WrappingStage.class);
    }

    private final QName certQName =
            new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");

    /*
     * Test that a simple example with an indented, wrapped, certificate ends
     * up with that wrapped data at the start of the line, and the closing tag
     * at the start of the line as well.
     */
    @Test
    public void testBasic() throws Exception {

        final Item<Element> data = readDOMItem("in-1.xml");
        final List<Item<Element>> coll = new ArrayList<>();
        coll.add(data);

        final ElementBase64WrappingStage stage = new ElementBase64WrappingStage();
        stage.setId("test");
        stage.setElementName(certQName);
        stage.initialize();
        stage.execute(coll);
        
        final Element expected = readXMLData("out-1.xml");
        assertXMLEqual(expected, data.unwrap());
    }

    /*
     * Test that glommed certificates end up correctly wrapped at the start of the line,
     * with the closing tag there as well.
     */
    @Test
    public void testGlommed() throws Exception {

        final Item<Element> data = readDOMItem("in-2.xml");
        final List<Item<Element>> coll = new ArrayList<>();
        coll.add(data);

        final ElementBase64WrappingStage stage = new ElementBase64WrappingStage();
        stage.setId("test");
        stage.setElementName(certQName);
        stage.initialize();
        stage.execute(coll);
        
        final Element expected = readXMLData("out-2.xml");
        assertXMLEqual(expected, data.unwrap());
    }

    /*
     * Test that lots of random white space and line breaks don't affect the wrapping process.
     */
    @Test
    public void testWhitespace() throws Exception {

        final Item<Element> data = readDOMItem("in-3.xml");
        final List<Item<Element>> coll = new ArrayList<>();
        coll.add(data);

        final ElementBase64WrappingStage stage = new ElementBase64WrappingStage();
        stage.setId("test");
        stage.setElementName(certQName);
        stage.initialize();
        stage.execute(coll);
        
        final Element expected = readXMLData("out-3.xml");
        assertXMLEqual(expected, data.unwrap());
    }
}

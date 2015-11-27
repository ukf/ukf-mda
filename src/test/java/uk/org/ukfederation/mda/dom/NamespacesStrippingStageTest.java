package uk.org.ukfederation.mda.dom;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.DOMElementItem;
import uk.org.ukfederation.mda.BaseDOMTest;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

public class NamespacesStrippingStageTest extends BaseDOMTest {

    /** Constructor sets class under test. */
    public NamespacesStrippingStageTest() {
        super(NamespacesStrippingStage.class);
    }

    /**
     * Test the simple case that is already handled by NamespaceStrippingStage,
     * of blacklisting a single namespace.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void simple() throws Exception {
        final Element doc = readXmlData("1-in.xml");
        final Item<Element> item = new DOMElementItem(doc);
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        
        final NamespacesStrippingStage stage = new NamespacesStrippingStage();
        stage.setId("stripTest");
        final List<String> namespaces = new ArrayList<>();
        namespaces.add("urn:namespace:beta");
        stage.setNamespaces(namespaces);
        stage.initialize();
        
        stage.execute(items);
        
        final Element out = readXmlData("1-out.xml");
        assertXmlEqual(out, item.unwrap());
    }

    /**
     * Test blacklisting.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void blacklist() throws Exception {
        final Element doc = readXmlData("2-in.xml");
        final Item<Element> item = new DOMElementItem(doc);
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        
        final NamespacesStrippingStage stage = new NamespacesStrippingStage();
        stage.setId("stripTest");
        final List<String> namespaces = new ArrayList<>();
        namespaces.add("urn:namespace:bravo");
        namespaces.add("urn:namespace:charlie");
        stage.setNamespaces(namespaces);
        stage.initialize();
        
        stage.execute(items);
        
        final Element out = readXmlData("2-bl.xml");
        assertXmlEqual(out, item.unwrap());
    }

    /**
     * Test whitelisting.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void whitelist() throws Exception {
        final Element doc = readXmlData("2-in.xml");
        final Item<Element> item = new DOMElementItem(doc);
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        
        final NamespacesStrippingStage stage = new NamespacesStrippingStage();
        stage.setId("stripTest");
        final List<String> namespaces = new ArrayList<>();
        namespaces.add("urn:namespace:alfa"); // root element
        namespaces.add("urn:namespace:bravo");
        namespaces.add("urn:namespace:charlie");
        stage.setNamespaces(namespaces);
        stage.setWhitelisting(true);
        stage.initialize();
        
        stage.execute(items);
        
        final Element out = readXmlData("2-wl.xml");
        assertXmlEqual(out, item.unwrap());
    }
}

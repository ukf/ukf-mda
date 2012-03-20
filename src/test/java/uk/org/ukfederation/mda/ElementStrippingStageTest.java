package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.dom.DomElementItem;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

public class ElementStrippingStageTest extends BaseDomTest {

    @Test
    public void doExecute() throws Exception {
        final Element doc = readXmlData("elementStripIn.xml");
        final DomElementItem item = new DomElementItem(doc);
        final List<DomElementItem> items = new ArrayList<DomElementItem>();
        items.add(item);
        
        final ElementStrippingStage stage = new ElementStrippingStage();
        stage.setId("stripTest");
        stage.setElementNamespace("urn:namespace:beta");
        stage.setElementName("StripMe");
        stage.initialize();
        
        stage.execute(items);
        
        final Element out = readXmlData("elementStripOut.xml");
        assertXmlEqual(out, item.unwrap());
    }
}

package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.dom.DomElementItem;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

public class ElementStrippingStageTest extends BaseDomTest {

    @Test
    public void doExecute() throws Exception {
        Element doc = readXmlData("elementStripIn.xml");
        DomElementItem item = new DomElementItem(doc);
        List<DomElementItem> items = new ArrayList<DomElementItem>();
        items.add(item);
        
        ElementStrippingStage stage = new ElementStrippingStage();
        stage.setId("stripTest");
        stage.setElementNamespace("urn:namespace:beta");
        stage.setElementName("StripMe");
        stage.initialize();
        
        stage.execute(items);
        
        Element out = readXmlData("elementStripOut.xml");
        assertXmlEqual(out, item.unwrap());
    }
}

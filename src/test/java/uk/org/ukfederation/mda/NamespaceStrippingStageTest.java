package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.dom.DomElementItem;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

public class NamespaceStrippingStageTest extends BaseDomTest {

    @Test
    public void doExecute() throws Exception {
        Element doc = readXmlData("namespaceStripIn.xml");
        DomElementItem item = new DomElementItem(doc);
        List<DomElementItem> items = new ArrayList<DomElementItem>();
        items.add(item);
        
        NamespaceStrippingStage stage = new NamespaceStrippingStage();
        stage.setId("stripTest");
        stage.setNamespace("urn:namespace:beta");
        stage.initialize();
        
        stage.execute(items);
        
        Element out = readXmlData("namespaceStripOut.xml");
        assertXmlEqual(out, item.unwrap());
    }
}

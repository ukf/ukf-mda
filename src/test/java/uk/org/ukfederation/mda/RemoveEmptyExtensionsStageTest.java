package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.NamespaceStrippingStage;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

public class RemoveEmptyExtensionsStageTest extends BaseDomTest {

    @Test
    public void doExecute() throws Exception {
        final Element doc = readXmlData("emptyExtensionsIn.xml");
        final DomElementItem item = new DomElementItem(doc);
        final List<DomElementItem> items = new ArrayList<DomElementItem>();
        items.add(item);
        
        final NamespaceStrippingStage removeMdui = new NamespaceStrippingStage();
        removeMdui.setId("removeMdui");
        removeMdui.setNamespace("urn:oasis:names:tc:SAML:metadata:ui");
        removeMdui.initialize();
        removeMdui.execute(items);
        
        final NamespaceStrippingStage removeUk = new NamespaceStrippingStage();
        removeUk.setId("removeUk");
        removeUk.setNamespace("http://ukfederation.org.uk/2006/11/label");
        removeUk.initialize();
        removeUk.execute(items);
        
        final RemoveEmptyExtensionsStage stage = new RemoveEmptyExtensionsStage();
        stage.setId("emptyExtensionsTest");
        stage.initialize();
        stage.execute(items);
        
        final Element out = readXmlData("emptyExtensionsOut.xml");
        assertXmlEqual(out, item.unwrap());
    }
}

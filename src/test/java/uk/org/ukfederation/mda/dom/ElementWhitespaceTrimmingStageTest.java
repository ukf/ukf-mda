
package uk.org.ukfederation.mda.dom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.saml.SamlMetadataSupport;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDomTest;
import uk.org.ukfederation.mda.validate.mdui.MDUISupport;

public class ElementWhitespaceTrimmingStageTest extends BaseDomTest {
    
    private ElementWhitespaceTrimmingStage makeStage() throws ComponentInitializationException {
        final ElementWhitespaceTrimmingStage stage = new ElementWhitespaceTrimmingStage();
        stage.setId("test");
        return stage; 
    }
    
    private String relativeFilename(final String which) {
        return "/uk/org/ukfederation/mda/dom/" + which + ".xml";
    }
    
    private DomElementItem makeItem(final String which) throws XMLParserException {
        final Element doc = readXmlData(relativeFilename(which));
        return new DomElementItem(doc);
    }
    
    @Test
    public void simpleTest() throws Exception {
        final DomElementItem item = makeItem("ElementWhitespaceTrimmingStage-1-in");
        final Element expected = readXmlData(relativeFilename("ElementWhitespaceTrimmingStage-1-out"));
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item);
        
        final Set<QName> names = new HashSet<>();
        names.add(new QName(MDUISupport.MDUI_NS, "DisplayName"));
        names.add(new QName(SamlMetadataSupport.MD_NS, "NameIDFormat"));
                
        final ElementWhitespaceTrimmingStage stage = makeStage();
        stage.setElementNames(names);
        stage.initialize();
        
        stage.execute(items);
        
        assertXmlEqual(expected, item.unwrap());
    }
    
    @Test
    public void testSingleton() throws Exception {
        final DomElementItem item = makeItem("ElementWhitespaceTrimmingStage-2-in");
        final Element expected = readXmlData(relativeFilename("ElementWhitespaceTrimmingStage-2-out"));
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item);
                        
        final ElementWhitespaceTrimmingStage stage = makeStage();
        stage.setElementName(new QName(SamlMetadataSupport.MD_NS, "NameIDFormat"));
        stage.initialize();
        
        stage.execute(items);
        
        assertXmlEqual(expected, item.unwrap());
    }
    
}

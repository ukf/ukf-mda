
package uk.org.ukfederation.mda.dom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDOMTest;
import uk.org.ukfederation.mda.validate.mdui.MDUISupport;

public class ElementWhitespaceTrimmingStageTest extends BaseDOMTest {
    
    @BeforeClass
    private void init() {
        setTestingClass(ElementWhitespaceTrimmingStage.class);
    }
    
    private ElementWhitespaceTrimmingStage makeStage() throws ComponentInitializationException {
        final ElementWhitespaceTrimmingStage stage = new ElementWhitespaceTrimmingStage();
        stage.setId("test");
        return stage; 
    }
    
    private DOMElementItem makeItem(final String which) throws XMLParserException {
        final Element doc = readXmlData(classRelativeResource(which));
        return new DOMElementItem(doc);
    }
    
    @Test
    public void simpleTest() throws Exception {
        final DOMElementItem item = makeItem("1-in.xml");
        final Element expected = readXmlData(classRelativeResource("1-out.xml"));
        
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        
        final Set<QName> names = new HashSet<>();
        names.add(new QName(MDUISupport.MDUI_NS, "DisplayName"));
        names.add(new QName(SAMLMetadataSupport.MD_NS, "NameIDFormat"));
                
        final ElementWhitespaceTrimmingStage stage = makeStage();
        stage.setElementNames(names);
        stage.initialize();
        
        stage.execute(items);
        
        assertXmlEqual(expected, item.unwrap());
    }
    
    @Test
    public void testSingleton() throws Exception {
        final DOMElementItem item = makeItem("2-in.xml");
        final Element expected = readXmlData(classRelativeResource("2-out.xml"));
        
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
                        
        final ElementWhitespaceTrimmingStage stage = makeStage();
        stage.setElementName(new QName(SAMLMetadataSupport.MD_NS, "NameIDFormat"));
        stage.initialize();
        
        stage.execute(items);
        
        assertXmlEqual(expected, item.unwrap());
    }
    
}

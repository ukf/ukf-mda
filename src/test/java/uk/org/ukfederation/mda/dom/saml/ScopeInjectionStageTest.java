
package uk.org.ukfederation.mda.dom.saml;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.metadata.Item;
import uk.org.ukfederation.mda.BaseDOMTest;
import uk.org.ukfederation.members.Members;

public class ScopeInjectionStageTest extends BaseDOMTest {

    protected ScopeInjectionStageTest() {
        super(ScopeInjectionStage.class);
    }

    private ScopeInjectionStage makeStage(final String membersPath) throws Exception {
        final Element membersElement = readXMLData(membersPath);
        final ScopeInjectionStage stage = new ScopeInjectionStage();
        stage.setId("test");
        stage.setMembers(new Members(membersElement.getOwnerDocument()));
        stage.initialize();
        return stage;
    }
    
    private ScopeInjectionStage makeStage() throws Exception {
        return makeStage("members.xml");
    }
    
    @Test
    public void testInjection() throws Exception {
        final ScopeInjectionStage stage = makeStage();
        final Item<Element> item = readDOMItem("in1.xml");
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        stage.execute(items);
        
        final Element out = readXMLData("out1.xml");
        assertXMLEqual(out, item.unwrap());
    }

}

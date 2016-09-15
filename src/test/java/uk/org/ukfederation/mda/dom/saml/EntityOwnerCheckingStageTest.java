
package uk.org.ukfederation.mda.dom.saml;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

import junit.framework.Assert;
import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import uk.org.ukfederation.mda.BaseDOMTest;
import uk.org.ukfederation.members.Members;

public class EntityOwnerCheckingStageTest extends BaseDOMTest {

    protected EntityOwnerCheckingStageTest() {
        super(EntityOwnerCheckingStage.class);
    }

    private Members getMembers() throws Exception {
        final Element membersElement = readXmlData("members.xml");
        return new Members(membersElement.getOwnerDocument());
    }
    
    private void runSingle(@Nonnull final Item<Element> item) throws Exception {
        final Members members = getMembers();
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        final EntityOwnerCheckingStage stage = new EntityOwnerCheckingStage();
        stage.setId("test");
        stage.setMembers(members);
        stage.initialize();
        stage.execute(items);
        stage.destroy();
    }
    
    private void checkError(@Nonnull final Item<Element> item, @Nonnull final String content) throws Exception {
        final List<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals("should have had exactly one error", 1, errors.size());
        final ErrorStatus error = errors.get(0);
        Assert.assertTrue("error '" + error.getStatusMessage() + "' should have contained " + content,
                error.getStatusMessage().contains(content));
    }

    @Test
    public void ok() throws Exception {
        final Item<Element> item = readDOMItem("ok.xml");
        runSingle(item);
        Assert.assertEquals(0, countErrors(item));
    }
    
    @Test
    public void enGB() throws Exception {
        final Item<Element> item = readDOMItem("en-GB.xml");
        runSingle(item);
        Assert.assertEquals(0, countErrors(item));
    }
    
    @Test
    public void notEntity() throws Exception {
        final Item<Element> item = readDOMItem("members.xml");
        runSingle(item);
        checkError(item, "item is not an EntityDescriptor");
    }
    
    @Test
    public void noOrg() throws Exception {
        final Item<Element> item = readDOMItem("noOrg.xml");
        runSingle(item);
        checkError(item, "entity has no Organization element");
    }
    
    @Test
    public void noOrgName() throws Exception {
        final Item<Element> item = readDOMItem("noOrgName.xml");
        runSingle(item);
        checkError(item, "entity has no OrganizationName with xml:lang='en'");
    }
    
    @Test
    public void noOrgNameEN() throws Exception {
        final Item<Element> item = readDOMItem("noOrgNameEN.xml");
        runSingle(item);
        checkError(item, "entity has no OrganizationName with xml:lang='en'");
    }
    
    @Test
    public void unknownName() throws Exception {
        final Item<Element> item = readDOMItem("unknown.xml");
        runSingle(item);
        checkError(item, "unknown owner name:");
        checkError(item, "Unknown Organization");
    }
}

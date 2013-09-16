package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.utilities.java.support.collection.ClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

public class IdPDisplayNameDuplicateDetectingStageTest extends BaseDomTest {

    private IdPDisplayNameDuplicateDetectingStage makeStage() throws ComponentInitializationException {
        final IdPDisplayNameDuplicateDetectingStage stage = new IdPDisplayNameDuplicateDetectingStage();
        stage.setId("test");
        stage.initialize();
        return stage; 
    }
    
    private DomElementItem makeItem(final String which) throws XMLParserException {
        final String fileName = "dupodn/" + which + ".xml";
        final Element doc = readXmlData(fileName);
        return new DomElementItem(doc);
    }
    
    /**
     * Test that an entity with multi-language ODN does not clash with itself.
     * 
     * @throws Exception
     */
    @Test
    public void noClashWithSelfODN1() throws Exception {
        final DomElementItem item = makeItem("sv-and-en-1");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();
        
        stage.execute(items);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
    }

    /**
     * Test that an entity with multi-language ODN *and* multi-language
     * mdui:DisplayName does not clash with itself.
     * 
     * @throws Exception
     */
    @Test
    public void noClashWithSelfODN2() throws Exception {
        final DomElementItem item = makeItem("sv-and-en-2");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();
        
        stage.execute(items);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
    }

    @Test
    public void noClashWithSP() throws Exception {
        final DomElementItem item1 = makeItem("sv-and-en-1");
        final DomElementItem item2 = makeItem("sv-and-en-2");
        final DomElementItem dup   = makeItem("sv-and-en-sp");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(dup);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();

        populateIdentifiers(items);
        stage.execute(items);
        
        Assert.assertEquals(countErrors(item1), 0, "first item");
        Assert.assertEquals(countErrors(item2), 0, "second item");
        Assert.assertEquals(countErrors(dup), 0, "service provider");
    }

    @Test
    public void duplicateMDUI() throws Exception {
        final DomElementItem item1 = makeItem("sv-and-en-1");
        final DomElementItem item2 = makeItem("sv-and-en-2");
        final DomElementItem dup   = makeItem("dup-mdui");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(dup);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();

        populateIdentifiers(items);
        stage.execute(items);
        
        Assert.assertEquals(countErrors(item1), 0, "first item");
        Assert.assertEquals(countErrors(item2), 1, "second item");
        Assert.assertEquals(countErrors(dup), 1, "deliberate duplicate");
    }

    @Test
    public void duplicateODN() throws Exception {
        final DomElementItem item1 = makeItem("sv-and-en-1");
        final DomElementItem item2 = makeItem("sv-and-en-2");
        final DomElementItem dup   = makeItem("dup-odn");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(dup);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();

        populateIdentifiers(items);
        stage.execute(items);
        
        Assert.assertEquals(countErrors(item1), 0, "first item");
        Assert.assertEquals(countErrors(item2), 1, "second item");
        Assert.assertEquals(countErrors(dup), 1, "deliberate duplicate");
    }

    /**
     * A duplicated OrganizationDisplayName, if you allow for white space at start and end of names.
     * 
     * @throws Exception
     */
    @Test
    public void duplicateODNWhitespace() throws Exception {
        final DomElementItem item1 = makeItem("sv-and-en-1");
        final DomElementItem item2 = makeItem("sv-and-en-2");
        final DomElementItem dup   = makeItem("dup-whitespace");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(dup);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();

        populateIdentifiers(items);
        stage.execute(items);
        
        Assert.assertEquals(countErrors(item1), 0, "first item");
        Assert.assertEquals(countErrors(item2), 1, "second item");
        Assert.assertEquals(countErrors(dup), 1, "deliberate duplicate");
    }
    
    /**
     * A duplicated OrganizationDisplayName, if you allow for case variation.
     * 
     * @throws Exception
     */
    @Test
    public void duplicateODNCase() throws Exception {
        final DomElementItem item1 = makeItem("sv-and-en-1");
        final DomElementItem item2 = makeItem("sv-and-en-2");
        final DomElementItem dup   = makeItem("dup-case");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(dup);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();

        populateIdentifiers(items);
        stage.execute(items);
        
        Assert.assertEquals(countErrors(item1), 0, "first item");
        Assert.assertEquals(countErrors(item2), 1, "second item");
        Assert.assertEquals(countErrors(dup), 1, "deliberate duplicate");
    }
    
    /**
     * In this test, the duplicate clashes against both of the previous examples so should
     * be reported one for each independent clash.
     * 
     * @throws Exception
     */
    @Test
    public void duplicateBoth() throws Exception {
        final DomElementItem item1 = makeItem("sv-and-en-1");
        final DomElementItem item2 = makeItem("sv-and-en-2");
        final DomElementItem dup   = makeItem("dup-both");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(dup);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();

        populateIdentifiers(items);
        stage.execute(items);
        
        Assert.assertEquals(countErrors(item1), 1, "first item");
        Assert.assertEquals(countErrors(item2), 1, "second item");
        Assert.assertEquals(countErrors(dup), 2, "deliberate duplicate");
    }

    /**
     * In this test, several duplicates clash against multiple originals, each entity should only
     * have independent clashes.
     * 
     * @throws Exception
     */
    @Test
    public void duplicateAll() throws Exception {
        final DomElementItem item1 = makeItem("sv-and-en-1");
        final DomElementItem item2 = makeItem("sv-and-en-2");
        final DomElementItem dupA  = makeItem("dup-mdui");
        final DomElementItem dupB  = makeItem("dup-odn");
        final DomElementItem dupC  = makeItem("dup-both");
        
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(dupA);
        items.add(dupB);
        items.add(dupC);
        
        final IdPDisplayNameDuplicateDetectingStage stage = makeStage();

        populateIdentifiers(items);
        stage.execute(items);
        
        Assert.assertEquals(countErrors(item1), 1, "first item");
        Assert.assertEquals(countErrors(item2), 1, "second item");
        Assert.assertEquals(countErrors(dupA), 1, "deliberate duplicate 1");
        Assert.assertEquals(countErrors(dupB), 1, "deliberate duplicate 2");
        Assert.assertEquals(countErrors(dupC), 2, "deliberate duplicate 3");
    }


}

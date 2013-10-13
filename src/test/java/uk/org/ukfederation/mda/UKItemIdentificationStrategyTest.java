
package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemId;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.metadata.dom.saml.EntityDescriptorItemIdPopulationStage;
import net.shibboleth.utilities.java.support.collection.ClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.validate.mdrpi.RegistrationAuthorityPopulationStage;

public class UKItemIdentificationStrategyTest extends BaseDOMTest {

    @BeforeClass
    private void init() {
        setTestingClass(UKItemIdentificationStrategy.class);
    }
    
    private UKItemIdentificationStrategy makeStrat() {
        final UKItemIdentificationStrategy strat = new UKItemIdentificationStrategy();
        strat.setNoItemIdIdentifier("mu");
        return strat;
    }
    
    private void performExtractions(DOMElementItem item) throws Exception {
        final List<DOMElementItem> items = new ArrayList<>();
        items.add(item);
        
        final RegistrationAuthorityPopulationStage stage1 = new RegistrationAuthorityPopulationStage();
        stage1.setId("test");
        stage1.initialize();
        stage1.execute(items);
        
        final EntityDescriptorItemIdPopulationStage stage2 = new EntityDescriptorItemIdPopulationStage();
        stage2.setId("test");
        stage2.initialize();
        stage2.execute(items);

        final EntityDescriptorUKIdPopulationStage stage3 = new EntityDescriptorUKIdPopulationStage();
        stage3.setId("test");
        stage3.initialize();
        stage3.execute(items);   
    }
    
    private DOMElementItem makeItem(final String which) throws XMLParserException {
        final Element doc = readXmlData(classRelativeResource(which + ".xml"));
        return new DOMElementItem(doc);
    }
    
    @Test
    public void getItemIdentifier() {
        final UKItemIdentificationStrategy strat = makeStrat();
        
        final Item<?> item1 = new MockItem("item 1");
        Assert.assertEquals(strat.getItemIdentifier(item1), "mu");
        item1.getItemMetadata().put(new UKId("uk-id"));
        Assert.assertEquals(strat.getItemIdentifier(item1), "uk-id");
        item1.getItemMetadata().put(new ItemId("item-id"));
        Assert.assertEquals(strat.getItemIdentifier(item1), "uk-id");

        final Item<?> item2 = new MockItem("item 2");
        Assert.assertEquals(strat.getItemIdentifier(item2), "mu");
        item2.getItemMetadata().put(new ItemId("item-id"));
        Assert.assertEquals(strat.getItemIdentifier(item2), "item-id");
        item2.getItemMetadata().put(new UKId("uk-id"));
        Assert.assertEquals(strat.getItemIdentifier(item2), "uk-id");
    }
    
    @Test
    public void withRegistrationAuthority() throws Exception {
        final UKItemIdentificationStrategy strat = makeStrat();
        final DOMElementItem item = makeItem("present");

        performExtractions(item);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
        
        Assert.assertEquals(strat.getItemIdentifier(item), "uk002232 (http://ukfederation.org.uk)");
    }
    
    @Test
    public void withoutRegistrationAuthority() throws Exception {
        final UKItemIdentificationStrategy strat = makeStrat();
        final DOMElementItem item = makeItem("absent");

        performExtractions(item);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
        
        Assert.assertEquals(strat.getItemIdentifier(item), "uk002232");
    }
    
    @Test
    public void ignoredAuthority() throws Exception {
        final UKItemIdentificationStrategy strat = makeStrat();
        final Set<String> auths = new HashSet<>();
        auths.add("http://ukfederation.org.uk");
        strat.setIgnoredAuthorities(auths);
        
        final DOMElementItem item = makeItem("present");

        performExtractions(item);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
        
        Assert.assertEquals(strat.getItemIdentifier(item), "uk002232");
    }
    
    @Test
    public void mappedAuthority() throws Exception {
        final UKItemIdentificationStrategy strat = makeStrat();
        final Map<String, String> nameMap = new HashMap<>();
        nameMap.put("http://ukfederation.org.uk", "UKf");
        strat.setDisplayNames(nameMap);
        
        final DOMElementItem item = makeItem("present");

        performExtractions(item);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
        
        Assert.assertEquals(strat.getItemIdentifier(item), "uk002232 (UKf)");
    }
    
}

package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemId;
import net.shibboleth.metadata.dom.DOMElementItem;

/** Unit tests for the {@link UKEntityOrderingStrategy} class. */
public class UKEntityOrderingStrategyTest extends BaseDOMTest {
    
    /** Constructor sets class under test. */
    public UKEntityOrderingStrategyTest() {
        super(UKEntityOrderingStrategy.class);
    }

    /**
     * Test the ordering strategy.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testOrder() throws Exception {
        // Create a trivial DOM Document as a placeholder
        final Element trivialDoc = readXMLData("trivial.xml");
        
        // Create some items in the order they will end up
        
        final DOMElementItem i00 = new DOMElementItem(readXMLData("named.xml"));
        
        final DOMElementItem i01 = new DOMElementItem(readXMLData("unnamed.xml"));
        
        final DOMElementItem i0 = new DOMElementItem(trivialDoc);
        i0.getItemMetadata().put(new UKId("uk000000"));
        
        final DOMElementItem i1 = new DOMElementItem(trivialDoc);
        i1.getItemMetadata().put(new UKId("uk000005"));
        i1.getItemMetadata().put(new ItemId("this has no effect"));
        
        final DOMElementItem i2 = new DOMElementItem(trivialDoc);
        i2.getItemMetadata().put(new UKId("uk123456"));
        
        final DOMElementItem i3 = new DOMElementItem(trivialDoc);
        i3.getItemMetadata().put(new ItemId("https://example.com/path0"));
        
        final DOMElementItem i4 = new DOMElementItem(trivialDoc);
        i3.getItemMetadata().put(new ItemId("https://example.com/path1"));
        
        final DOMElementItem i5 = new DOMElementItem(trivialDoc);
        // nothing at all on i5
        
        // Make a collection containing those items in an arbitrary order
        final List<Item<Element>> items = new ArrayList<>();
        items.add(i4);
        items.add(i3);
        items.add(i1);
        items.add(i0);
        items.add(i00);
        items.add(i5);
        items.add(i01);
        items.add(i2);
        
        // Order the collection
        final UKEntityOrderingStrategy strat = new UKEntityOrderingStrategy();
        final List<Item<Element>>items2 = strat.order(items);
        
        // Check that everything is in the right place afterwards
        Assert.assertEquals(items2.get(0), i00);
        Assert.assertEquals(items2.get(1), i01);
        Assert.assertEquals(items2.get(2), i0);
        Assert.assertEquals(items2.get(3), i1);
        Assert.assertEquals(items2.get(4), i2);
        Assert.assertEquals(items2.get(5), i3);
        Assert.assertEquals(items2.get(6), i4);
        Assert.assertEquals(items2.get(7), i5);
    }

}

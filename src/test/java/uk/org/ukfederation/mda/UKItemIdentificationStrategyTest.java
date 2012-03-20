
package uk.org.ukfederation.mda;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemId;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UKItemIdentificationStrategyTest {

    @Test
    public void getItemIdentifier() {
        final UKItemIdentificationStrategy strat = new UKItemIdentificationStrategy();
        strat.setNoItemIdIdentifier("mu");
        
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
}

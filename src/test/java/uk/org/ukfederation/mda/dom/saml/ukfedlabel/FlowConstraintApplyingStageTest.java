
package uk.org.ukfederation.mda.dom.saml.ukfedlabel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.pipeline.Stage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.testing.MockItem;
import net.shibboleth.shared.component.ComponentInitializationException;

public class FlowConstraintApplyingStageTest {

    private Stage<String> makeStage(@Nonnull final String flowName) throws ComponentInitializationException {
        final var stage = new FlowConstraintApplyingStage<String>();
        stage.setId("test");
        stage.setFlowName(flowName);
        stage.initialize();
        return stage;
    }

    private List<Item<String>> makeCollection() {
        final List<Item<String>> items = new ArrayList<>();
        final Item<String> item = new MockItem("test");
        items.add(item);
        return items;
    }
    
    private List<Item<String>> makeCollection(@Nonnull final FlowConstraint constraint) {
        final List<Item<String>> items = makeCollection();
        items.get(0).getItemMetadata().put(constraint);
        return items;
    }

    private List<Item<String>> makeCollection(@Nonnull final FlowConstraint c1,
            @Nonnull final FlowConstraint c2) {
        final List<Item<String>> items = makeCollection();
        items.get(0).getItemMetadata().put(c1);
        items.get(0).getItemMetadata().put(c2);
        return items;
    }

    @Test(expectedExceptions = {StageProcessingException.class},
            expectedExceptionsMessageRegExp = ".*has both enables and disables.*")
    public void testHasBothConstraints() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items = makeCollection(new EnableFlow("enable"),
                new DisableFlow("disable"));
        stage.execute(items); // should throw
    }

    @Test(expectedExceptions = {ComponentInitializationException.class})
    public void testNoFlowName() throws Exception {
        final var stage = new FlowConstraintApplyingStage<String>();
        stage.setId("test");
        stage.initialize();
    }

    @Test
    public void testNoConstraints() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items = makeCollection();
        stage.execute(items);
        // An item with no constraints is always allowed through
        Assert.assertEquals(1, items.size());
    }
    
    @Test
    public void testEnableMatch() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items = makeCollection(new EnableFlow("test"));
        stage.execute(items);
        // An item matching an enable constraint is allowed through
        Assert.assertEquals(1, items.size());
    }
    
    @Test
    public void testEnableNoMatch() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items = makeCollection(new EnableFlow("other"));
        stage.execute(items);
        // An item NOT matching an enable constraint is removed
        Assert.assertEquals(0, items.size());
    }
    
    @Test
    public void testEnableSecondMatch() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items =
                makeCollection(new EnableFlow("other"), new EnableFlow("test"));
        stage.execute(items);
        // An item whose second enable constraint matches the flow is preserved
        Assert.assertEquals(1, items.size());
    }
    
    @Test
    public void testEnableNeitherMatch() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items =
                makeCollection(new EnableFlow("one"), new EnableFlow("two"));
        stage.execute(items);
        // An item with multiple enables neither of which matches is removed
        Assert.assertEquals(0, items.size());
    }
    
    @Test
    public void testDisableMatch() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items = makeCollection(new DisableFlow("test"));
        stage.execute(items);
        // An item matching an disable constraint is removed
        Assert.assertEquals(0, items.size());
    }
    
    @Test
    public void testDisableNoMatch() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items = makeCollection(new DisableFlow("other"));
        stage.execute(items);
        // An item NOT matching an disable constraint is retained
        Assert.assertEquals(1, items.size());
    }
    
    @Test
    public void testDisableSecondMatch() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items =
                makeCollection(new DisableFlow("other"), new DisableFlow("test"));
        stage.execute(items);
        // An item whose second disable constraint matches the flow is removed
        Assert.assertEquals(0, items.size());
    }
    
    @Test
    public void testDisableNeitherMatch() throws Exception {
        final Stage<String> stage = makeStage("test");
        final List<Item<String>> items =
                makeCollection(new DisableFlow("one"), new DisableFlow("two"));
        stage.execute(items);
        // An item with multiple disables neither of which matches is retained
        Assert.assertEquals(1, items.size());
    }

}


package uk.org.ukfederation.mda.dom.saml.ukfedlabel;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.metadata.Item;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.XMLParserException;
import uk.org.ukfederation.mda.BaseDOMTest;

public class EntityDescriptorFlowConstraintPopulationStageTest extends BaseDOMTest {

    final EntityDescriptorFlowConstraintPopulationStage stage;

    protected EntityDescriptorFlowConstraintPopulationStageTest() throws ComponentInitializationException {
        super(EntityDescriptorFlowConstraintPopulationStage.class);
        stage = new EntityDescriptorFlowConstraintPopulationStage();
        stage.setId("test");
        stage.initialize();
    }

    private List<Item<Element>> readSingleItem(final String name) throws XMLParserException {
        final List<Item<Element>> items = new ArrayList<>();
        final Item<Element> item = readDOMItem(name);
        items.add(item);
        return items;
    }

    @Test
    public void testNone() throws Exception {
        final List<Item<Element>> items = readSingleItem("none.xml");
        stage.execute(items);
        final Item<Element> item = items.get(0);
        final List<DisableFlow> disables = item.getItemMetadata().get(DisableFlow.class);
        final List<EnableFlow> enables = item.getItemMetadata().get(EnableFlow.class);
        final List<FlowConstraint> constraints = item.getItemMetadata().get(FlowConstraint.class);
        Assert.assertEquals(disables.size(), 0);
        Assert.assertEquals(enables.size(), 0);
        Assert.assertEquals(constraints.size(), 0);
    }

    @Test
    public void testEnable() throws Exception {
        final List<Item<Element>> items = readSingleItem("enable.xml");
        stage.execute(items);
        final Item<Element> item = items.get(0);
        final List<DisableFlow> disables = item.getItemMetadata().get(DisableFlow.class);
        final List<EnableFlow> enables = item.getItemMetadata().get(EnableFlow.class);
        final List<FlowConstraint> constraints = item.getItemMetadata().get(FlowConstraint.class);
        Assert.assertEquals(disables.size(), 0);
        Assert.assertEquals(enables.size(), 2);
        Assert.assertEquals(constraints.size(), 2);
        Assert.assertEquals(enables.get(0).getFlowName(), "wibble");
        Assert.assertEquals(enables.get(1).getFlowName(), "wobble");
    }

    @Test
    public void testDisable() throws Exception {
        final List<Item<Element>> items = readSingleItem("disable.xml");
        stage.execute(items);
        final Item<Element> item = items.get(0);
        final List<DisableFlow> disables = item.getItemMetadata().get(DisableFlow.class);
        final List<EnableFlow> enables = item.getItemMetadata().get(EnableFlow.class);
        final List<FlowConstraint> constraints = item.getItemMetadata().get(FlowConstraint.class);
        Assert.assertEquals(disables.size(), 2);
        Assert.assertEquals(enables.size(), 0);
        Assert.assertEquals(constraints.size(), 2);
        Assert.assertEquals(disables.get(0).getFlowName(), "wibble");
        Assert.assertEquals(disables.get(1).getFlowName(), "wobble");
    }

    @Test
    public void testBoth() throws Exception {
        final List<Item<Element>> items = readSingleItem("both.xml");
        stage.execute(items);
        final Item<Element> item = items.get(0);
        final List<DisableFlow> disables = item.getItemMetadata().get(DisableFlow.class);
        final List<EnableFlow> enables = item.getItemMetadata().get(EnableFlow.class);
        final List<FlowConstraint> constraints = item.getItemMetadata().get(FlowConstraint.class);
        Assert.assertEquals(disables.size(), 2);
        Assert.assertEquals(enables.size(), 2);
        Assert.assertEquals(constraints.size(), 4);
        Assert.assertEquals(enables.get(0).getFlowName(), "wibble");
        Assert.assertEquals(enables.get(1).getFlowName(), "wobble");
        Assert.assertEquals(disables.get(0).getFlowName(), "wibble");
        Assert.assertEquals(disables.get(1).getFlowName(), "wobble");
    }
    
    // Check that two bad cases (which would be detected by schema validation
    // in practice) operate as expected:
    //    * an invalid value "   " is passed through
    //    * a missing flow name results in a flow name of "".
    @Test
    public void testBad() throws Exception {
        final List<Item<Element>> items = readSingleItem("bad.xml");
        stage.execute(items);
        final Item<Element> item = items.get(0);
        final List<DisableFlow> disables = item.getItemMetadata().get(DisableFlow.class);
        final List<EnableFlow> enables = item.getItemMetadata().get(EnableFlow.class);
        final List<FlowConstraint> constraints = item.getItemMetadata().get(FlowConstraint.class);
        Assert.assertEquals(disables.size(), 1);
        Assert.assertEquals(enables.size(), 1);
        Assert.assertEquals(constraints.size(), 2);
        Assert.assertEquals(disables.get(0).getFlowName(), "   ");
        Assert.assertEquals(enables.get(0).getFlowName(), "");
    }
    
}

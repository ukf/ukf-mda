package uk.org.ukfederation.mda.statistics;

import java.util.ArrayList;
import java.util.Collection;

import net.shibboleth.metadata.Item;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDOMTest;

/** Unit tests for the StatisticsVelocityStage class. */
public class StatisticsVelocityStageTest extends BaseDOMTest {

    /** Constructor sets class under test. */
    public StatisticsVelocityStageTest() {
        super(StatisticsVelocityStage.class);
    }

    /**
     * Simple "hello, world" test.
     * 
     * @throws Exception if anything goes wrong.
     */
    @Test
    public void testHello() throws Exception {
        final StatisticsVelocityStage stage = new StatisticsVelocityStage();
        stage.setId("test");
        stage.setTemplateName(classRelativeResource("hello.vm"));
        stage.setParserPool(parserPool);
        stage.initialize();

        final Collection<Item<Element>> items = new ArrayList<>();
        stage.execute(items);
        Assert.assertEquals(items.size(), 1);
        final Element e = items.iterator().next().unwrap();
        Assert.assertEquals("hello", e.getLocalName());
    }
}

package uk.org.ukfederation.mda.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.validate.BaseValidator;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import uk.org.ukfederation.mda.BaseDOMTest;

public class StringXPathValidationStageTest extends BaseDOMTest {

    protected StringXPathValidationStageTest() {
        super(StringXPathValidationStage.class);
    }

    @Test
    public void testNoExpression() throws Exception {
        final StringXPathValidationStage stage = new StringXPathValidationStage();
        stage.setId("test");
        try {
            stage.initialize();
            Assert.fail("Expected initialization failure");
        } catch (final ComponentInitializationException e) {
            Assert.assertTrue(e.getMessage().contains("can not be null or empty"));
        }
    }
    
    @Test
    public void testNoValidators() throws Exception {
        final StringXPathValidationStage stage = new StringXPathValidationStage();
        stage.setId("test");
        stage.setXPathExpression("foo/@bar");
        stage.initialize();
        
        final Item<Element> item = readDOMItem("input.xml");
        final Collection<Item<Element>> items = new ArrayList<>();
        items.add(item);
        
        stage.execute(items);
        Assert.assertEquals(item.getItemMetadata().get(ErrorStatus.class).size(), 0);
    }
    
    private class SayBadString extends BaseValidator implements Validator<String> {

        @Override
        public Action validate(String e, Item<?> item, String stageId) throws StageProcessingException {
            addError("bad string " + e, item, stageId);
            return Action.DONE;
        }
        
    }
    
    @Test
    public void testCountElements() throws Exception {
        final SayBadString sayBad = new SayBadString();
        sayBad.setId("bad");
        sayBad.initialize();
        
        final List<Validator<String>> validators = new ArrayList<>();
        validators.add(sayBad);
        
        final StringXPathValidationStage stage = new StringXPathValidationStage();
        stage.setId("test");
        stage.setXPathExpression("//next"); // "next" element anywhere in the document
        stage.setValidators(validators);
        stage.initialize();
        
        final Item<Element> item = readDOMItem("input.xml");
        final Collection<Item<Element>> items = new ArrayList<>();
        items.add(item);

        stage.execute(items);
        Assert.assertEquals(item.getItemMetadata().get(ErrorStatus.class).size(), 2);
    }
    
    @Test
    public void testCountAttributes() throws Exception {
        final SayBadString sayBad = new SayBadString();
        sayBad.setId("bad");
        sayBad.initialize();
        
        final List<Validator<String>> validators = new ArrayList<>();
        validators.add(sayBad);
        
        final StringXPathValidationStage stage = new StringXPathValidationStage();
        stage.setId("test");
        stage.setXPathExpression("//@bar"); // @bar anywhere in the document
        stage.setValidators(validators);
        stage.initialize();
        
        final Item<Element> item = readDOMItem("input.xml");
        final Collection<Item<Element>> items = new ArrayList<>();
        items.add(item);

        stage.execute(items);
        final List<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 2);
        
        // Check the values in the error messages
        final ErrorStatus e1 = errors.get(0);
        Assert.assertTrue(e1.getStatusMessage().contains("hello"));
        Assert.assertFalse(e1.getStatusMessage().contains("howdy"));
        final ErrorStatus e2 = errors.get(1);
        Assert.assertTrue(e2.getStatusMessage().contains("howdy"));
        Assert.assertFalse(e2.getStatusMessage().contains("hello"));
    }
    
    @Test
    public void testOneBar() throws Exception {
        final SayBadString sayBad = new SayBadString();
        sayBad.setId("bad");
        sayBad.initialize();
        
        final List<Validator<String>> validators = new ArrayList<>();
        validators.add(sayBad);
        
        final StringXPathValidationStage stage = new StringXPathValidationStage();
        stage.setId("test");
        stage.setXPathExpression("//foo/@bar"); // @bar only on foo element
        stage.setValidators(validators);
        stage.initialize();
        
        final Item<Element> item = readDOMItem("input.xml");
        final Collection<Item<Element>> items = new ArrayList<>();
        items.add(item);

        stage.execute(items);
        Assert.assertEquals(item.getItemMetadata().get(ErrorStatus.class).size(), 1);
    }
    
}

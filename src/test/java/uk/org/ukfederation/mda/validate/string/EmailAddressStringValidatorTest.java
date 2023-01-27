
package uk.org.ukfederation.mda.validate.string;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.MockItem;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.metadata.validate.Validator.Action;
import net.shibboleth.shared.component.ComponentInitializationException;

public class EmailAddressStringValidatorTest {

    private Validator<String> validator;
    
    @BeforeClass
    private void setUpClass() throws ComponentInitializationException {
        validator = new EmailAddressStringValidator();
        validator.setId("email");
        validator.initialize();
    }

    private void testGood(final String value) throws StageProcessingException {
        final MockItem item = new MockItem(value);
        Assert.assertEquals(validator.validate(item.unwrap(), item, "test"), Action.CONTINUE);
        Assert.assertTrue(item.getItemMetadata().isEmpty());
    }

    private void testBad(final String value) throws StageProcessingException {
        final MockItem item = new MockItem(value);
        Assert.assertEquals(validator.validate(item.unwrap(), item, "test"), Action.DONE);
        Assert.assertFalse(item.getItemMetadata().isEmpty());
        final List<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 1);
        final ErrorStatus error = errors.get(0);
        Assert.assertEquals(error.getComponentId(), "test/email");
        Assert.assertTrue(error.getStatusMessage().contains(value));
        Assert.assertTrue(error.getStatusMessage().contains("badly formatted"));
    }

    @Test
    public void test() throws Exception {
        testGood("mailto:ian@iay.org.uk");
        testGood("mailto:First.O'Last@example.com");
        testGood("mailto:address+sub@example.org");

        testBad("");                            // empty element
        testBad("ian@iay.org.uk");              // no "mailto:"
        testBad(" mailto:ian@iay.org.uk");      // leading space
        testBad("mailto:ian@iay.org.uk ");      // trailing space
        testBad("mailto:ian.iay.org.uk");       // no '@'
    }

}

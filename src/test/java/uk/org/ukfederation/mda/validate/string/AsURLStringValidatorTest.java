
package uk.org.ukfederation.mda.validate.string;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.MockItem;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.validate.BaseValidator;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

public class AsURLStringValidatorTest {

    /**
     * A validator that simply captures the value provided to it.
     *
     * @param <T> type of value to be captured
     */
    private static class CapturingValidator<T> extends BaseValidator implements Validator<T> {
        private T savedValue;
        
        T getSavedValue() {
            return savedValue;
        }

        @Override
        public Action validate(T e, Item<?> item, String stageId) throws StageProcessingException {
            // We don't expect this to be called more than once
            Assert.assertNull(savedValue);
            savedValue = e;
            return Action.CONTINUE;
        }
        
    }
    
    /*
     * Test to see if the string is correctly converted to a URL with all the appropriate components.
     */
    @Test
    public void testSuccess() throws Exception {
        final Item<String> item = new MockItem("test");
        final CapturingValidator<URL> urlCapture = new CapturingValidator<>();
        urlCapture.setId("capture");
        urlCapture.initialize();
        final List<Validator<URL>> urlValidators = Collections.singletonList((Validator<URL>)urlCapture);
        final AsURLStringValidator asValidator = new AsURLStringValidator();
        asValidator.setId("test");
        asValidator.setConversionRequired(true);
        asValidator.setValidators(urlValidators);
        asValidator.initialize();
        asValidator.validate("https://example.com:1234/a/b/c/d", item, "stage");
        Assert.assertEquals(item.getItemMetadata().get(ErrorStatus.class).size(), 0);
        final URL u = urlCapture.getSavedValue();
        Assert.assertNotNull(u);
        Assert.assertEquals(u.getProtocol(), "https");
        Assert.assertEquals(u.getAuthority(), "example.com:1234");
        Assert.assertEquals(u.getHost(), "example.com");
        Assert.assertEquals(u.getPort(), 1234);
        Assert.assertEquals(u.getPath(), "/a/b/c/d");
    }
    
    private void badURL(final String bad) {
        final Item<String> item = new MockItem("test");
        final CapturingValidator<URL> urlCapture = new CapturingValidator<>();
        urlCapture.setId("capture");
        try {
            urlCapture.initialize();
        } catch (ComponentInitializationException e1) {
            Assert.fail("can't initialise capturing validator", e1);
        }
        final List<Validator<URL>> urlValidators = Collections.singletonList((Validator<URL>)urlCapture);
        final AsURLStringValidator asValidator = new AsURLStringValidator();
        asValidator.setId("test");
        asValidator.setConversionRequired(true);
        asValidator.setValidators(urlValidators);
        try {
            asValidator.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail("component init", e);
        }
        try {
            asValidator.validate(bad, item, "stage");
            final List<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
            if (errors.size() != 1) {
                final URL u = urlCapture.getSavedValue();
                System.out.println("Captured URL: '" + u + "'");
                System.out.println("   authority: '" + u.getAuthority() + "'");
                Assert.assertEquals(errors.size(), 1);
            } else {
                System.out.println("bad \"" + bad + "\" result \"" + errors.get(0).getStatusMessage() + "\"");
            }
        } catch (StageProcessingException e) {
            Assert.fail("stage processing", e);
        }
    }
    
    @Test
    public void testEmptyString() {
        badURL("");
    }

    @Test
    public void testBadPort() {
        badURL("https://example.org:port/example");
    }

    @Test
    public void testBareDomain() {
        badURL("www.example.org");
    }

    @Test
    public void testData() {
        badURL("data:foo");
    }
}

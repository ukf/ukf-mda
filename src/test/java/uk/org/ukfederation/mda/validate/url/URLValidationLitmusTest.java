package uk.org.ukfederation.mda.validate.url;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.MockItem;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.metadata.validate.Validator.Action;
import uk.org.iay.incommon.mda.validate.ValidatorSequence;

/**
 * A litmus test for the combination of components used to validate URL values in SAML metadata.
 */
@ContextConfiguration("URLValidationLitmusTest-config.xml")
public class URLValidationLitmusTest extends AbstractTestNGSpringContextTests {

    private ValidatorSequence<String> validators;
    
    @BeforeClass
    private void setUpClass() throws Exception {
        final List<Validator<String>> validatorList = applicationContext.getBean("validators", List.class);
        this.validators = new ValidatorSequence<>();
        validators.setId("seq");
        validators.setValidators(validatorList);
        validators.initialize();
    }

    private void badURL(@Nonnull final String bad) {
        final Item<String> item = new MockItem("test");
        try {
            final Action action = validators.validate(bad, item, "stage");
            Assert.assertEquals(action, Action.DONE);
            final List<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
            Assert.assertEquals(errors.size(), 1);
        } catch (StageProcessingException e) {
            Assert.fail("stage processing", e);
        }
    }

    @Test
    public void testEmptyString() throws Exception {
        badURL("");
    }

    @Test
    public void testEmptyAuthority() {
        badURL("http:///foo/");
    }
    
    @Test
    public void testBadPort() {
        badURL("https://example.org:port/example");
    }

    /**
     * Test the case where the authority's port field is present but empty.
     * 
     * This is valid by the specification, but is regarded as invalid by
     * libxml2's xs:anyURI checker.
     */
    @Test
    public void testEmptyPort() {
        badURL("http://example.org:/example/");
    }

    @Test
    public void testDoubleScheme() {
        badURL("http://http://example.org/example/");
    }

    @Test
    public void testBareDomain() {
        badURL("www.example.org");
    }

    @Test
    public void testFillInHostName() {
        badURL("http://*** FILL IN ***/");
    }


}

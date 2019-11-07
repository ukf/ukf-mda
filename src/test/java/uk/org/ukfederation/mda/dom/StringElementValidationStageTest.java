
package uk.org.ukfederation.mda.dom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.testng.Assert;
import org.testng.Assert.ThrowingRunnable;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import uk.org.ukfederation.mda.BaseDOMTest;
import uk.org.ukfederation.mda.validate.string.EmailAddressStringValidator;

public class StringElementValidationStageTest extends BaseDOMTest {


    protected StringElementValidationStageTest() {
        super(StringElementValidationStage.class);
    }

    @Test
    public void testNoElementNames() throws Exception {
        final StringElementValidationStage stage = new StringElementValidationStage();
        stage.setId("test");
        Assert.assertThrows(ComponentInitializationException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() throws Throwable {
                        stage.initialize();
                    }
                }
        );
    }
    
    @Test
    public void testNoValidators() throws Exception {
        final Item<Element> item = readDOMItem("two-bad-addrs.xml");
        final List<Item<Element>> items = new ArrayList<>();

        final StringElementValidationStage stage = new StringElementValidationStage();
        stage.setId("test");
        stage.setElementName(new QName(SAMLMetadataSupport.MD_NS, "EmailAddress"));
        // don't set any validators
        stage.initialize();
        
        stage.execute(items);
        
        final List<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertTrue(errors.isEmpty());
    }
    
    @Test
    public void testEmail() throws Exception {
        final Item<Element> item = readDOMItem("two-bad-addrs.xml");
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);

        final EmailAddressStringValidator val = new EmailAddressStringValidator();
        val.setId("email");
        val.initialize();

        final List<Validator<String>> validators = new ArrayList<>();
        validators.add(val);
        
        final StringElementValidationStage stage = new StringElementValidationStage();
        stage.setId("test");
        stage.setElementName(new QName(SAMLMetadataSupport.MD_NS, "EmailAddress"));
        stage.setValidators(validators);
        stage.initialize();
        
        stage.execute(items);
        
        final List<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 2);
    }
}

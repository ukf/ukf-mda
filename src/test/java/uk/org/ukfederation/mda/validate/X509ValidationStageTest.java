
package uk.org.ukfederation.mda.validate;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.WarningStatus;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDOMTest;

public class X509ValidationStageTest extends BaseDOMTest {

    /** Constructor sets class under test. */
    public X509ValidationStageTest() throws Exception {
        super(X509ValidationStage.class);
    }
    
    private X509ValidationStage makeStage() throws ComponentInitializationException {
        final X509ValidationStage stage = new X509ValidationStage();
        stage.setId("test");
        return stage; 
    }
    
    private DOMElementItem makeItem(final String which) throws XMLParserException {
        final Element doc = readXmlData(classRelativeResource(which));
        return new DOMElementItem(doc);
    }
    
    private void errorsAndWarnings(final Item<Element> item,
            final int expectedErrors, final int expectedWarnings) {
        final Collection<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(expectedErrors, errors.size());
        final Collection<WarningStatus> warnings = item.getItemMetadata().get(WarningStatus.class);
        Assert.assertEquals(expectedWarnings, warnings.size());
        //for (ErrorStatus err: errors) {
        //    System.out.println("Error: " + err.getComponentId() + ": " + err.getStatusMessage());
        //}
        //for (WarningStatus warn: warnings) {
        //    System.out.println("Warning: " + warn.getComponentId() + ": " + warn.getStatusMessage());
        //}
    }

    @Test
    public void testNothing() throws Exception {
        final DOMElementItem item = makeItem("in.xml");
        
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        
        final X509ValidationStage stage = makeStage();
        // not setting any validators to run
        stage.initialize();
        
        stage.execute(items);

        errorsAndWarnings(item, 0, 0);
    }
    
    @Test
    public void testError() throws Exception {
        final DOMElementItem item = makeItem("in.xml");
        
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        
        final X509RSAKeyLengthValidator val =
                new X509RSAKeyLengthValidator();
        val.setErrorBoundary(2049);
        
        final List<Validator<X509Certificate>> vals = new ArrayList<>();
        vals.add(val);
        
        final X509ValidationStage stage = makeStage();
        stage.setValidators(vals);
        stage.initialize();
        
        stage.execute(items);

        errorsAndWarnings(item, 1, 0);
    }
    
    @Test
    public void testWarning() throws Exception {
        final DOMElementItem item = makeItem("in.xml");
        
        final List<Item<Element>> items = new ArrayList<>();
        items.add(item);
        
        final X509RSAKeyLengthValidator val =
                new X509RSAKeyLengthValidator();
        val.setWarningBoundary(2049);
        val.setErrorBoundary(2048);
        
        final List<Validator<X509Certificate>> vals = new ArrayList<>();
        vals.add(val);
        
        final X509ValidationStage stage = makeStage();
        stage.setValidators(vals);
        stage.initialize();
        
        stage.execute(items);
        
        errorsAndWarnings(item, 0, 1);
    }
    
}

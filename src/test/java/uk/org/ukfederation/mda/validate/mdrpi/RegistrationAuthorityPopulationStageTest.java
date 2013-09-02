
package uk.org.ukfederation.mda.validate.mdrpi;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.util.ClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDomTest;

public class RegistrationAuthorityPopulationStageTest extends BaseDomTest {

    private RegistrationAuthorityPopulationStage makeStage() throws ComponentInitializationException {
        final RegistrationAuthorityPopulationStage stage = new RegistrationAuthorityPopulationStage();
        stage.setId("test");
        stage.initialize();
        return stage; 
    }
    
    private DomElementItem makeItem(final String which) throws XMLParserException {
        final String fileName = "regpop/" + which + ".xml";
        final Element doc = readXmlData(fileName);
        return new DomElementItem(doc);
    }
    
    @Test
    public void populatePresent() throws Exception {
        final DomElementItem item = makeItem("present");
        
        final List<DomElementItem> items = new ArrayList<DomElementItem>();
        items.add(item);
        
        final RegistrationAuthorityPopulationStage stage = makeStage();
        
        stage.execute(items);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
        
        final List<RegistrationAuthority> regAuths = metadata.get(RegistrationAuthority.class);
        Assert.assertEquals(regAuths.size(), 1);
        final RegistrationAuthority regAuth = regAuths.get(0);
        Assert.assertEquals(regAuth.getRegistrationAuthority(), "http://ukfederation.org.uk");
    }
    
    @Test
    public void populateAbsent() throws Exception  {
        final DomElementItem item = makeItem("absent");
        
        final List<DomElementItem> items = new ArrayList<DomElementItem>();
        items.add(item);
        
        final RegistrationAuthorityPopulationStage stage = makeStage();
        
        stage.execute(items);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
        
        final List<RegistrationAuthority> regAuths = metadata.get(RegistrationAuthority.class);
        Assert.assertEquals(regAuths.size(), 0);
    }
    
    @Test
    public void populateNoExtensions() throws Exception  {
        final DomElementItem item = makeItem("noext");
        
        final List<DomElementItem> items = new ArrayList<DomElementItem>();
        items.add(item);
        
        final RegistrationAuthorityPopulationStage stage = makeStage();
        
        stage.execute(items);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
        
        final List<RegistrationAuthority> regAuths = metadata.get(RegistrationAuthority.class);
        Assert.assertEquals(regAuths.size(), 0);
    }
    
}

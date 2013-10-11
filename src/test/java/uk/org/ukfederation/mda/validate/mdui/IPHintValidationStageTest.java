
package uk.org.ukfederation.mda.validate.mdui;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.utilities.java.support.collection.ClassToInstanceMultiMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDomTest;

public class IPHintValidationStageTest extends BaseDomTest {

    @BeforeClass
    private void init() {
        setTestingClass(IPHintValidationStage.class);
    }
    
    @Test
    public void missingComponent() throws Exception {
        final Element doc = readXmlData("1.xml");
        final DomElementItem item = new DomElementItem(doc);
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item);
        
        final IPHintValidationStage stage = new IPHintValidationStage();
        stage.setId("test");
        stage.initialize();
        
        stage.execute(items);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 1);
        
        final ErrorStatus error = errors.get(0);
        Assert.assertTrue(error.getStatusMessage().contains("193.72.192/26"));
    }

    @Test
    public void hostAddress() throws Exception {
        final Element doc = readXmlData("2.xml");
        final DomElementItem item = new DomElementItem(doc);
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item);
        
        final IPHintValidationStage stage = new IPHintValidationStage();
        stage.setId("test");
        stage.setCheckingNetworks(true);
        stage.initialize();
        
        stage.execute(items);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 1);
        
        final ErrorStatus error = errors.get(0);
        Assert.assertTrue(error.getStatusMessage().contains("82.68.124.32/3"));
    }

    @Test
    public void ignoreHostAddress() throws Exception {
        final Element doc = readXmlData("2.xml");
        final DomElementItem item = new DomElementItem(doc);
        final List<DomElementItem> items = new ArrayList<>();
        items.add(item);
        
        final IPHintValidationStage stage = new IPHintValidationStage();
        stage.setId("test");
        stage.setCheckingNetworks(false);
        stage.initialize();
        
        stage.execute(items);
        
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        final List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 0);
    }
}

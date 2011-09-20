
package uk.org.ukfederation.mda.validate.mdui;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.util.ClassToInstanceMultiMap;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDomTest;

public class IPHintValidationStageTest extends BaseDomTest {

    @Test
    public void missingComponent() throws Exception {
        Element doc = readXmlData("ipHintValidation1.xml");
        DomElementItem item = new DomElementItem(doc);
        List<DomElementItem> items = new ArrayList<DomElementItem>();
        items.add(item);
        
        IPHintValidationStage stage = new IPHintValidationStage();
        stage.setId("test");
        stage.initialize();
        
        stage.execute(items);
        
        ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
        Assert.assertEquals(errors.size(), 1);
        
        ErrorStatus error = errors.get(0);
        Assert.assertTrue(error.getStatusMessage().contains("193.72.192/26"));
    }

}

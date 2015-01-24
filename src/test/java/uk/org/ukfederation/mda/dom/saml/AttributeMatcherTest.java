
package uk.org.ukfederation.mda.dom.saml;

import net.shibboleth.utilities.java.support.xml.ElementSupport;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDOMTest;

import com.google.common.base.Predicate;

public class AttributeMatcherTest extends BaseDOMTest {
    
    private final Document doc;
    private Element attr;
    
    protected AttributeMatcherTest() throws Exception {
        super(AttributeMatcher.class);
        setUp();
        doc = getParserPool().newDocument();
    }
    
    @BeforeTest
    private void beforeTest() throws Exception {
        attr = ElementSupport.constructElement(doc, SAMLSupport.ATTRIBUTE_NAME);
    }
    
    @Test
    public void matchNormal() throws Exception {
        final Predicate<Element> matcher1 = new AttributeMatcher("name", "name-format");
        attr.setAttribute("Name", "name");
        attr.setAttribute("NameFormat", "name-format");
        Assert.assertTrue(matcher1.apply(attr));
        
        final Predicate<Element> matcher2 = new AttributeMatcher("name2", "name-format");
        Assert.assertFalse(matcher2.apply(attr));
        
        final Predicate<Element> matcher3 = new AttributeMatcher("name", "name-format2");
        Assert.assertFalse(matcher3.apply(attr));
    }
    
    @Test
    public void matchDefaultFormat() throws Exception {
        attr.setAttribute("Name", "name");

        final Predicate<Element> matcher1 = new AttributeMatcher("name", SAMLSupport.ATTRNAME_FORMAT_UNSPECIFIED);
        Assert.assertTrue(matcher1.apply(attr));
        
        final Predicate<Element> matcher2 = new AttributeMatcher("name2", SAMLSupport.ATTRNAME_FORMAT_UNSPECIFIED);
        Assert.assertFalse(matcher2.apply(attr));
    }

}

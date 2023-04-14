
package uk.org.ukfederation.mda.validate.x509;

import java.security.cert.X509Certificate;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.testing.MockItem;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.metadata.validate.x509.BaseX509ValidatorTest;

public class X509ConsistentNameValidatorTest extends BaseX509ValidatorTest {
    
    /**
     * Constructor sets class under test.
     * 
     * @throws Exception if something goes wrong
     */
    public X509ConsistentNameValidatorTest() throws Exception {
        super(X509ConsistentNameValidator.class);
    }

    @Test
    public void testOK() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509ConsistentNameValidator val = new X509ConsistentNameValidator();
        Assert.assertTrue(val.isError());
        final X509Certificate cert = getCertificate("ligo-new.pem");
        Assert.assertEquals(val.validate(cert, item, "stage"), Validator.Action.CONTINUE);
        errorsAndWarnings(item, 0, 0);
    }

    @Test
    public void testFail1() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509ConsistentNameValidator val = new X509ConsistentNameValidator();
        final X509Certificate cert = getCertificate("ligo-old.pem");
        Assert.assertEquals(val.validate(cert, item, "stage"), Validator.Action.CONTINUE);
        errorsAndWarnings(item, 1, 0);
    }

    @Test
    public void testFail2() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509ConsistentNameValidator val = new X509ConsistentNameValidator();
        final X509Certificate cert = getCertificate("uk002204.pem");
        Assert.assertEquals(val.validate(cert, item, "stage"), Validator.Action.CONTINUE);
        errorsAndWarnings(item, 1, 0);
    }
    
    @Test
    public void testWarning() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509ConsistentNameValidator val = new X509ConsistentNameValidator();
        val.setError(false);
        Assert.assertFalse(val.isError());
        final X509Certificate cert = getCertificate("uk002204.pem");
        Assert.assertEquals(val.validate(cert, item, "stage"), Validator.Action.CONTINUE);
        errorsAndWarnings(item, 0, 1);
    }

}

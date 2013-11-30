
package uk.org.ukfederation.mda.validate;

import java.security.cert.X509Certificate;

import junit.framework.Assert;
import net.shibboleth.metadata.Item;

import org.testng.annotations.Test;

import uk.org.ukfederation.mda.MockItem;

public class X509CertificateConsistentNameValidatorTest extends BaseCertificateValidatorTest {
    
    /** Constructor sets class under test. */
    public X509CertificateConsistentNameValidatorTest() throws Exception {
        super(X509CertificateConsistentNameValidator.class);
    }

    @Test
    public void testOK() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509CertificateConsistentNameValidator val = new X509CertificateConsistentNameValidator();
        Assert.assertTrue(val.isError());
        final X509Certificate cert = getCertificate("ligo-new.pem");
        val.validate(cert, item, "stage");
        errorsAndWarnings(item, 0, 0);
    }

    @Test
    public void testFail1() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509CertificateConsistentNameValidator val = new X509CertificateConsistentNameValidator();
        final X509Certificate cert = getCertificate("ligo-old.pem");
        val.validate(cert, item, "stage");
        errorsAndWarnings(item, 1, 0);
    }

    @Test
    public void testFail2() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509CertificateConsistentNameValidator val = new X509CertificateConsistentNameValidator();
        final X509Certificate cert = getCertificate("uk002204.pem");
        val.validate(cert, item, "stage");
        errorsAndWarnings(item, 1, 0);
    }
    
    @Test
    public void testWarning() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509CertificateConsistentNameValidator val = new X509CertificateConsistentNameValidator();
        val.setError(false);
        Assert.assertFalse(val.isError());
        final X509Certificate cert = getCertificate("uk002204.pem");
        val.validate(cert, item, "stage");
        errorsAndWarnings(item, 0, 1);
    }

}

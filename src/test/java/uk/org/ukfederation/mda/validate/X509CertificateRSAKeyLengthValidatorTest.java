
package uk.org.ukfederation.mda.validate;

import java.security.cert.X509Certificate;

import net.shibboleth.metadata.Item;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uk.org.ukfederation.mda.MockItem;

public class X509CertificateRSAKeyLengthValidatorTest extends BaseCertificateValidatorTest {
    
    @BeforeClass
    public void beforeClass() throws Exception {
        setTestingClass(X509CertificateRSAKeyLengthValidator.class);
    }

    @Test
    public void testDefaults2048() throws Exception {
        final Item<String> item = new MockItem("foo");
        final Validator<X509Certificate> val = new X509CertificateRSAKeyLengthValidator();
        final X509Certificate cert = getCertificate("2048.pem");
        val.validate(cert, item, "stage");
        errorsAndWarnings(item, 0, 0);
    }

    @Test
    public void testDefaults1024() throws Exception {
        final Item<String> item = new MockItem("foo");
        final Validator<X509Certificate> val = new X509CertificateRSAKeyLengthValidator();
        final X509Certificate cert = getCertificate("1024.pem");
        val.validate(cert, item, "stage");
        errorsAndWarnings(item, 1, 0);
    }

    @Test
    public void testWarningOn1024() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509CertificateRSAKeyLengthValidator val = new X509CertificateRSAKeyLengthValidator();
        val.setErrorBoundary(1024);
        val.setWarningBoundary(2048);
        final X509Certificate cert = getCertificate("1024.pem");
        val.validate(cert, item, "stage");
        errorsAndWarnings(item, 0, 1);
    }

}

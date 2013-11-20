
package uk.org.ukfederation.mda.validate;

import java.security.cert.X509Certificate;

import net.shibboleth.metadata.Item;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uk.org.ukfederation.mda.MockItem;

public class X509CertificateRSAExponentValidatorTest extends BaseCertificateValidatorTest {
    
    @BeforeClass
    public void beforeClass() throws Exception {
        setTestingClass(X509CertificateRSAExponentValidator.class);
    }

    private void testCert(final String certName,
            final Validator<X509Certificate> val,
            final int expectedErrors, final int expectedWarnings) throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509Certificate cert = getCertificate(certName);
        val.validate(cert, item, "stage");
        errorsAndWarnings(item, expectedErrors, expectedWarnings);
    }

    private void testThreeCerts(final Validator<X509Certificate> val,
            final int expectedErrors3, final int expectedWarnings3,
            final int expectedErrors35, final int expectedWarnings35,
            final int expectedErrors65537, final int expectedWarnings65537) throws Exception {
        testCert("3.pem", val, expectedErrors3, expectedWarnings3); // exponent == 3
        testCert("35.pem", val, expectedErrors35, expectedWarnings35); // exponent == 35
        testCert("65537.pem", val, expectedErrors65537, expectedWarnings65537); // exponent == 65537
    }
    
    @Test
    public void testDefaults() throws Exception {
        final X509CertificateRSAExponentValidator val = new X509CertificateRSAExponentValidator();
        testThreeCerts(val, 1, 0, 0, 0, 0, 0);
    }

    @Test
    public void testNISTWarning() throws Exception {
        final X509CertificateRSAExponentValidator val = new X509CertificateRSAExponentValidator();
        val.setWarningBoundary(65537);
        testThreeCerts(val, 1, 0, 0, 1, 0, 0);
    }

    @Test
    public void testNISTError() throws Exception {
        final X509CertificateRSAExponentValidator val = new X509CertificateRSAExponentValidator();
        val.setErrorBoundary(65537);
        testThreeCerts(val, 1, 0, 1, 0, 0, 0);
    }

    @Test
    public void testWarningOnly() throws Exception {
        final X509CertificateRSAExponentValidator val = new X509CertificateRSAExponentValidator();
        val.setErrorBoundary(0);
        val.setWarningBoundary(65537);
        testThreeCerts(val, 0, 1, 0, 1, 0, 0);
    }

}

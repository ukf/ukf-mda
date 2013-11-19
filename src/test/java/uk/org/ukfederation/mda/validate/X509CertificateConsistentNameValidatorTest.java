
package uk.org.ukfederation.mda.validate;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

import junit.framework.Assert;
import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.WarningStatus;
import net.shibboleth.utilities.java.support.resource.Resource;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uk.org.ukfederation.mda.BaseTest;
import uk.org.ukfederation.mda.MockItem;

public class X509CertificateConsistentNameValidatorTest extends BaseTest {
    
    private CertificateFactory factory;

    @BeforeClass
    public void beforeClass() throws Exception {
        setTestingClass(X509CertificateConsistentNameValidator.class);
        factory = CertificateFactory.getInstance("X.509");
    }

    private X509Certificate getCertificate(final String id) throws Exception {
        final Resource certResource = getClasspathResource(id);
        certResource.initialize();
        final X509Certificate cert =
                (X509Certificate) factory.generateCertificate(certResource.getInputStream());
        return cert;
    }

    private void errorsAndWarnings(final Item<?> item,
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

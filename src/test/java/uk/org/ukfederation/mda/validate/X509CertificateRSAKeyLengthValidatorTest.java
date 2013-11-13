
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

public class X509CertificateRSAKeyLengthValidatorTest extends BaseTest {
    
    private CertificateFactory factory;

    @BeforeClass
    public void beforeClass() throws Exception {
        setTestingClass(X509CertificateRSAKeyLengthValidator.class);
        factory = CertificateFactory.getInstance("X.509");
    }

    private X509Certificate getCertificate(final String id) throws Exception {
        final Resource certResource = getClasspathResource(id);
        certResource.initialize();
        final X509Certificate cert =
                (X509Certificate) factory.generateCertificate(certResource.getInputStream());
        return cert;
    }

    @Test
    public void testDefaults2048() throws Exception {
        final Item<String> item = new MockItem("foo");
        final Validator<X509Certificate> val = new X509CertificateRSAKeyLengthValidator();
        final X509Certificate cert = getCertificate("2048.pem");
        val.validate(cert, item, "stage");
        final Collection<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(0, errors.size());
        final Collection<WarningStatus> warnings = item.getItemMetadata().get(WarningStatus.class);
        Assert.assertEquals(0, warnings.size());
    }

    @Test
    public void testDefaults1024() throws Exception {
        final Item<String> item = new MockItem("foo");
        final Validator<X509Certificate> val = new X509CertificateRSAKeyLengthValidator();
        final X509Certificate cert = getCertificate("1024.pem");
        val.validate(cert, item, "stage");
        final Collection<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(1, errors.size());
        final Collection<WarningStatus> warnings = item.getItemMetadata().get(WarningStatus.class);
        Assert.assertEquals(0, warnings.size());
    }

    @Test
    public void testWarningOn1024() throws Exception {
        final Item<String> item = new MockItem("foo");
        final X509CertificateRSAKeyLengthValidator val = new X509CertificateRSAKeyLengthValidator();
        val.setErrorBoundary(1024);
        val.setWarningBoundary(2048);
        final X509Certificate cert = getCertificate("1024.pem");
        val.validate(cert, item, "stage");
        final Collection<ErrorStatus> errors = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(0, errors.size());
        final Collection<WarningStatus> warnings = item.getItemMetadata().get(WarningStatus.class);
        Assert.assertEquals(1, warnings.size());
    }

}

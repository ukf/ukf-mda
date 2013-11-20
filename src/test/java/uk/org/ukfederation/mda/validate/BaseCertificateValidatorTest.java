
package uk.org.ukfederation.mda.validate;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.testng.annotations.BeforeClass;

import junit.framework.Assert;
import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.WarningStatus;
import net.shibboleth.utilities.java.support.resource.Resource;
import uk.org.ukfederation.mda.BaseTest;

public class BaseCertificateValidatorTest extends BaseTest {
    
    private CertificateFactory factory;

    @BeforeClass
    public void createCertificateFactory() throws Exception {
        factory = CertificateFactory.getInstance("X.509");
    }

    protected X509Certificate getCertificate(final String id) throws Exception {
        final Resource certResource = getClasspathResource(id);
        certResource.initialize();
        final X509Certificate cert =
                (X509Certificate) factory.generateCertificate(certResource.getInputStream());
        return cert;
    }

    protected void errorsAndWarnings(final Item<?> item,
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

}

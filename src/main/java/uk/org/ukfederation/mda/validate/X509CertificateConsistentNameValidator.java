/*
 * Copyright (C) 2013 University of Edinburgh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.ukfederation.mda.validate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.metadata.Item;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERString;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InetAddresses;

import edu.vt.middleware.crypt.util.HexConverter;

/**
 * Validator class to check that X.509 certificate CNs are consistent with any
 * DNS subjectAltNames.
 */
@ThreadSafe
public class X509CertificateConsistentNameValidator extends AbstractX509CertificateValidator {

    /**
     * Support class for X.509 certificate handling.
     * 
     * This code is pulled from a snapshot of the Shibboleth OpenSAML V3 security API, and
     * should be refactored to use that once it is released.
     */
    // Checkstyle: CyclomaticComplexity OFF -- fix in upstream code
    private static class X509Support {

        /** Common Name (CN) OID. */
        public static final String CN_OID = "2.5.4.3";
        
        /** RFC 2459 Other Subject Alt Name type. */
        public static final Integer OTHER_ALT_NAME = new Integer(0);

        /** RFC 2459 RFC 822 (email address) Subject Alt Name type. */
        public static final Integer RFC822_ALT_NAME = new Integer(1);

        /** RFC 2459 DNS Subject Alt Name type. */
        public static final Integer DNS_ALT_NAME = new Integer(2);

        /** RFC 2459 X.400 Address Subject Alt Name type. */
        public static final Integer X400ADDRESS_ALT_NAME = new Integer(3);

        /** RFC 2459 Directory Name Subject Alt Name type. */
        public static final Integer DIRECTORY_ALT_NAME = new Integer(4);

        /** RFC 2459 EDI Party Name Subject Alt Name type. */
        public static final Integer EDI_PARTY_ALT_NAME = new Integer(5);

        /** RFC 2459 URI Subject Alt Name type. */
        public static final Integer URI_ALT_NAME = new Integer(6);

        /** RFC 2459 IP Address Subject Alt Name type. */
        public static final Integer IP_ADDRESS_ALT_NAME = new Integer(7);

        /** RFC 2459 Registered ID Subject Alt Name type. */
        public static final Integer REGISTERED_ID_ALT_NAME = new Integer(8);

        /**
         * Gets the commons names that appear within the given distinguished name. The returned list
         * provides the names in the order they appeared in the DN.
         * 
         * @param dn the DN to extract the common names from
         * 
         * @return the common names that appear in the DN in the order they appear or null if the given DN is null
         */
        @Nullable public static List<String> getCommonNames(@Nullable final X500Principal dn) {
            if (dn == null) {
                return null;
            }

            Logger log = getLogger();
            log.debug("Extracting CNs from the following DN: {}", dn.toString());
            List<String> commonNames = new LinkedList<String>();
            try {
                ASN1InputStream asn1Stream = new ASN1InputStream(dn.getEncoded());
                DERObject parent = asn1Stream.readObject();

                String cn = null;
                DERObject dnComponent;
                DERSequence grandChild;
                DERObjectIdentifier componentId;
                for (int i = 0; i < ((DERSequence) parent).size(); i++) {
                    dnComponent = ((DERSequence) parent).getObjectAt(i).getDERObject();
                    if (!(dnComponent instanceof DERSet)) {
                        log.debug("No DN components.");
                        continue;
                    }

                    // Each DN component is a set
                    for (int j = 0; j < ((DERSet) dnComponent).size(); j++) {
                        grandChild = (DERSequence) ((DERSet) dnComponent).getObjectAt(j).getDERObject();

                        if (grandChild.getObjectAt(0) != null
                                && grandChild.getObjectAt(0).getDERObject() instanceof DERObjectIdentifier) {
                            componentId = (DERObjectIdentifier) grandChild.getObjectAt(0).getDERObject();

                            if (CN_OID.equals(componentId.getId())) {
                                // OK, this dn component is actually a cn attribute
                                if (grandChild.getObjectAt(1) != null
                                        && grandChild.getObjectAt(1).getDERObject() instanceof DERString) {
                                    cn = ((DERString) grandChild.getObjectAt(1).getDERObject()).getString();
                                    commonNames.add(cn);
                                }
                            }
                        }
                    }
                }

                asn1Stream.close();

                return commonNames;

            } catch (IOException e) {
                log.error("Unable to extract common names from DN: ASN.1 parsing failed: " + e);
                return null;
            }
        }

        /**
         * Gets the list of alternative names of a given name type.
         * 
         * @param certificate the certificate to extract the alternative names from
         * @param nameTypes the name types
         * 
         * @return the alt names, of the given type, within the cert
         */
        @Nullable public static List<?> getAltNames(@Nullable final X509Certificate certificate,
                @Nullable final Integer[] nameTypes) {
            if (certificate == null || nameTypes == null || nameTypes.length == 0) {
                return null;
            }

            List<Object> names = new LinkedList<Object>();
            Collection<List<?>> altNames = null;
            try {
                altNames = X509ExtensionUtil.getSubjectAlternativeNames(certificate);
            } catch (CertificateParsingException e) {
                getLogger().error("Encountered an problem trying to extract Subject Alternate "
                        + "Name from supplied certificate: " + e);
                return names;
            }

            if (altNames != null) {
                // 0th position represents the alt name type
                // 1st position contains the alt name data
                for (List<?> altName : altNames) {
                    for (Integer nameType : nameTypes) {
                        if (altName.get(0).equals(nameType)) {
                            names.add(convertAltNameType(nameType, altName.get(1)));
                            break;
                        }
                    }
                }
            }

            return names;
        }

        /**
         * Convert types returned by Bouncy Castle X509ExtensionUtil.getSubjectAlternativeNames(X509Certificate) to be
         * consistent with what is documented for: java.security.cert.X509Certificate#getSubjectAlternativeNames.
         * 
         * @param nameType the alt name type
         * @param nameValue the alt name value
         * @return converted representation of name value, based on type
         */
        @Nullable private static Object convertAltNameType(@Nonnull final Integer nameType,
                @Nonnull final Object nameValue) {
            Logger log = getLogger();
            
            if (DIRECTORY_ALT_NAME.equals(nameType) || DNS_ALT_NAME.equals(nameType) || RFC822_ALT_NAME.equals(nameType)
                    || URI_ALT_NAME.equals(nameType) || REGISTERED_ID_ALT_NAME.equals(nameType)) {

                // these are just strings in the appropriate format already, return as-is
                return nameValue;
            } else if (IP_ADDRESS_ALT_NAME.equals(nameType)) {
                // this is a byte[], IP addr in network byte order
                byte [] nameValueBytes = (byte[]) nameValue;
                try {
                    return InetAddresses.toAddrString(InetAddress.getByAddress(nameValueBytes));
                } catch (UnknownHostException e) {
                    HexConverter hexConverter = new HexConverter(true);
                    log.warn("Was unable to convert IP address alt name byte[] to string: " +
                            hexConverter.fromBytes(nameValueBytes), e);
                    return null;
                }
            } else if (EDI_PARTY_ALT_NAME.equals(nameType) || X400ADDRESS_ALT_NAME.equals(nameType)
                    || OTHER_ALT_NAME.equals(nameType)) {

                // these have no defined representation, just return a DER-encoded byte[]
                return ((DERObject) nameValue).getDEREncoded();
            } else {
                log.warn("Encountered unknown alt name type '{}', adding as-is", nameType);
                return nameValue;
            }
        }
        
        /**
         * Get an SLF4J Logger.
         * 
         * @return a Logger instance
         */
        @Nonnull private static Logger getLogger() {
            return LoggerFactory.getLogger(X509Support.class);
        }
        
    }
    // Checkstyle: CyclomaticComplexity ON
    
    /**
     * Constructor.
     */
    public X509CertificateConsistentNameValidator() {
        super("ConsistentName");
    }

    /** {@inheritDoc} */
    public void validate(@Nonnull final X509Certificate cert, @Nonnull final Item<?> item,
            @Nonnull final String stageId) {
        
        // Extract the DNS subjectAltNames. If we don't have any, there can't be a problem.
        final List<?> altNames = X509Support.getAltNames(cert, new Integer[]{X509Support.DNS_ALT_NAME});
        if (altNames.isEmpty()) {
            return;
        }
        
        // Extract the CNs. Again, none of those means no problem here.
        final List<String> commonNames = X509Support.getCommonNames(cert.getSubjectX500Principal());
        if (commonNames.isEmpty()) {
            return;
        }
        
        // There is a problem if any of the CNs do not also appear in the DNS subjectAltNames.
        for (String cn: commonNames) {
            if (!altNames.contains(cn)) {
                final StringBuilder b = new StringBuilder();
                boolean first = true;
                b.append("CN=");
                b.append(cn);
                b.append(" not present in DNS subjectAltNames {");
                for (Object dnsName: altNames) {
                    if (first) {
                        first = false;
                    } else {
                        b.append(", ");
                    }
                    b.append('"');
                    b.append(dnsName);
                    b.append('"');
                }
                b.append('}');
                addError(b.toString(), item, stageId);
            }
        }
    }

}

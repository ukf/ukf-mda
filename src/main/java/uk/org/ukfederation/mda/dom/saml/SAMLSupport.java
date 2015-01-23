/*
 * Copyright (C) 2015 University of Edinburgh.
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

package uk.org.ukfederation.mda.dom.saml;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

/** Helper class for dealing with SAML documents. */
@ThreadSafe
public final class SAMLSupport {

    /** Namespace URI for SAML elements. */
    public static final String SAML_NS = "urn:oasis:names:tc:SAML:2.0:assertion";
    
    /** Conventional prefix for SAML elements. */
    public static final String SAML_PREFIX = "saml";
    
    /** saml:Attribute element. */
    public static final QName ATTRIBUTE_NAME = new QName(SAML_NS, "Attribute", SAML_PREFIX);
    
    /** saml:AttributeValue element. */
    public static final QName ATTRIBUTE_VALUE_NAME = new QName(SAML_NS, "AttributeValue", SAML_PREFIX);
    
    /** Constructor. */
    private SAMLSupport() {
    }

}

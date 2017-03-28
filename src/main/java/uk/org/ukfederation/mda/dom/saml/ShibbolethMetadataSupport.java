/*
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

import org.w3c.dom.Element;

import com.google.common.base.Function;

import uk.org.ukfederation.mda.dom.Container;
import uk.org.ukfederation.mda.dom.ElementMaker;

/** Helper class for dealing with Shibboleth metadata. */
@ThreadSafe
public final class ShibbolethMetadataSupport {

    /** Shibboleth metadata namespace URI. */
    public static final String SHIBMD_NS = "urn:mace:shibboleth:metadata:1.0";

    /** Default Shibboleth metadata namespace prefix. */
    public static final String SHIBMD_PREFIX = "shibmd";

    /** Scope element name. */
    public static final QName SCOPE_NAME = new QName(SHIBMD_NS, "Scope", SHIBMD_PREFIX);

    /** regexp attribute name. */
    public static final QName REGEXP_ATTRIB_NAME = new QName("regexp");

    /** Maker class for Scope elements. */
    public static final Function<Container, Element> SCOPE_MAKER =
        new ElementMaker(ShibbolethMetadataSupport.SCOPE_NAME);

    /** Constructor. */
    private ShibbolethMetadataSupport() {

    }
}
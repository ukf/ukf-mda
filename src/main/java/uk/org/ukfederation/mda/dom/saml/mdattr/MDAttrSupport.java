/*
 * Copyright (C) 2014 University of Edinburgh.
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

package uk.org.ukfederation.mda.dom.saml.mdattr;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

/** Helper class for dealing with MDAttr metadata. */
@ThreadSafe
public final class MDAttrSupport {

    /** MDAttr namespace. */
    public static final String MDATTR_NS = "urn:oasis:names:tc:SAML:metadata:attribute";

    /** mdattr:AntityAttributes element. */
    public static final QName MDATTR_ENTITY_ATTRIBUTES = new QName(MDATTR_NS, "EntityAttributes");
    
    /** Constructor. */
    private MDAttrSupport() {
    }

}

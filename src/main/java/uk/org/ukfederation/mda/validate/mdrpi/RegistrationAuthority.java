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

package uk.org.ukfederation.mda.validate.mdrpi;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Carries the registration authority URI for a SAML entity.
 * 
 * Although the value is formally a URI, we represent it as a String for now
 * to prevent running into trouble if people use malformed values.
 */
@ThreadSafe
public class RegistrationAuthority implements ItemMetadata {

    /**
     * Serial version UID for the class.
     */
    private static final long serialVersionUID = -4485180950229424894L;

    /** Registration authority URI. */
    private final String registrationAuthority;

    /**
     * Constructor.
     * 
     * @param authority The registration authority for the entity, never null
     */
    public RegistrationAuthority(@Nonnull @NotEmpty final String authority) {
        registrationAuthority = Constraint.isNotNull(StringSupport.trimOrNull(authority),
                "registration authority may not be null or empty");
    }

    /**
     * Gets the registration authority value.
     * 
     * @return unique identifier for the data carried by the Item
     */
    @Nonnull public String getRegistrationAuthority() {
        return registrationAuthority;
    }

}
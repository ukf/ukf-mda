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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple immutable implementation of {@link EntityAttributeContext}.
 */
public class SimpleEntityAttributeContext implements EntityAttributeContext {

    /** The attribute's value. */
    @Nonnull
    private final String value;
    
    /** The attribute's <code>Name</code>. */
    @Nonnull
    private final String name;
    
    /** The attribute's <code>NameFormat</code>. */
    @Nonnull
    private final String nameFormat;
    
    /** The entity's registration authority, or <code>null</code>. */
    @Nullable
    private final String registrationAuthority;
    
    /**
     * Constructor.
     * 
     * @param attributeValue attribute value
     * @param attributeName attribute <code>Name</code>
     * @param attributeNameFormat attribute <code>NameFormat</code>
     * @param registrar entity's registration authority, or <code>null</code>
     */
    public SimpleEntityAttributeContext(@Nullable final String attributeValue,
            @Nonnull final String attributeName,
            @Nonnull final String attributeNameFormat,
            @Nonnull final String registrar) {
        value = attributeValue;
        name = attributeName;
        nameFormat = attributeNameFormat;
        registrationAuthority = registrar;
    }
    
    /**
     * Shorthand three-argument constructor.
     * 
     * @param attributeValue attribute value
     * @param attributeName attribute <code>Name</code>
     * @param attributeNameFormat attribute <code>NameFormat</code>
     */
    public SimpleEntityAttributeContext(@Nullable final String attributeValue,
            @Nonnull final String attributeName,
            @Nonnull final String attributeNameFormat) {
        this(attributeValue, attributeName, attributeNameFormat, null);
    }    

    @Override
    public String getRegistrationAuthority() {
        return registrationAuthority;
    }

    @Override
    public String getNameFormat() {
        return nameFormat;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("{v=").append(getValue());
        b.append(", n=").append(getName());
        b.append(", f=").append(getNameFormat());
        b.append(", r=");
        if (getRegistrationAuthority() == null) {
            b.append("(none)");
        } else {
            b.append(getRegistrationAuthority());
        }
        b.append('}');
        return b.toString();
    }
}

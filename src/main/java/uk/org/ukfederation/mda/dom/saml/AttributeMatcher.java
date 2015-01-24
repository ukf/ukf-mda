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

import javax.annotation.Nonnull;

import org.w3c.dom.Element;

import uk.org.ukfederation.mda.dom.ElementMatcher;

/**
 * Match {@link com.google.common.base.Predicate} for SAML <code>Attribute</code> elements with specific
 * <code>Name</code> and <code>NameFormat</code> attributes,
 * for use with the {@link uk.org.ukfederation.mda.dom.Container} system.
 */
public class AttributeMatcher extends ElementMatcher {

    /** <code>NameFormat</code> attribute value to match. */
    @Nonnull private final String matchFormat;
    
    /** <code>Name</code> attribute value to match. */
    @Nonnull private final String matchName;
    
    /**
     * Constructor.
     * 
     * @param name <code>Name</code> attribute value to match
     * @param format <code>NameFormat</code> attribute value to match
     */
    public AttributeMatcher(@Nonnull final String name, @Nonnull final String format) {
        super(SAMLSupport.ATTRIBUTE_NAME);
        assert name != null;
        assert format != null;
        matchName = name;
        matchFormat = format;
    }

    @Override
    public boolean apply(@Nonnull final Element element) {
        // check for element name
        if (!super.apply(element)) {
            return false;
        }
        
        // now check attributes
        return matchFormat.equals(SAMLSupport.extractAttributeNameFormat(element)) &&
                matchName.equals(element.getAttribute("Name"));
    }
}

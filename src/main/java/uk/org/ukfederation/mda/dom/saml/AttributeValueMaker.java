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
import javax.annotation.concurrent.ThreadSafe;

import org.w3c.dom.Element;

import uk.org.ukfederation.mda.dom.Container;
import uk.org.ukfederation.mda.dom.ElementMaker;

/**
 * A class for constructing SAML <code>AttributeValue</code> elements
 * for use with the {@link Container} system.
 */
@ThreadSafe
public class AttributeValueMaker extends ElementMaker {
    
    /** Value for the attribute. */
    @Nonnull
    private final String attributeValue;

    /**
     * Constructor.
     * 
     * @param value value for the attribute
     */
    public AttributeValueMaker(@Nonnull final String value) {
        super(SAMLSupport.ATTRIBUTE_VALUE_NAME);
        assert value != null;
        attributeValue = value;
    }

    @Override
    public Element apply(@Nonnull final Container container) {
        final Element newElement = super.apply(container);
        newElement.setTextContent(attributeValue);
        return newElement;
    }
}

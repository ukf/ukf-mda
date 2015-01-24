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
 * A class for constructing SAML <code>Attribute</code> elements
 * for use with the {@link Container} system.
 */
@ThreadSafe
public class AttributeMaker extends ElementMaker {
    
    /** Value for the <code>Name</code> XML attribute. */
    @Nonnull
    private final String attributeName;

    /** Value for the <code>NameFormat</code> XML attribute. */
    @Nonnull
    private final String attributeNameFormat;

    /**
     * Constructor.
     * 
     * @param name value for the <code>Name</code> XML attribute
     * @param nameFormat value for the <code>NameFormat</code> XML attribute
     */
    public AttributeMaker(@Nonnull final String name, @Nonnull final String nameFormat) {
        super(SAMLSupport.ATTRIBUTE_NAME);
        assert name != null;
        assert nameFormat != null;
        attributeName = name;
        attributeNameFormat = nameFormat;
    }

    @Override
    public Element apply(@Nonnull final Container container) {
        final Element newElement = super.apply(container);
        newElement.setAttributeNS(null, "Name", attributeName);
        newElement.setAttributeNS(null, "NameFormat", attributeNameFormat);
        return newElement;
    }
}

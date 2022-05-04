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

package uk.org.ukfederation.mda.dom.saml.mdattr;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

import net.shibboleth.metadata.dom.saml.mdattr.EntityAttributeFilteringStage;
import net.shibboleth.metadata.dom.saml.mdattr.EntityAttributeFilteringStage.EntityAttributeContext;
import net.shibboleth.utilities.java.support.logic.Constraint;

public class AssuranceCertificationMatcherTest {

    private void test(final boolean expected, final Predicate<EntityAttributeContext> matcher,
            final EntityAttributeContext context) {
        Assert.assertEquals(matcher.apply(context), expected, context.toString());
    }

    @Test
    public void testNoRA() {
        final Predicate<EntityAttributeContext> matcher = new AssuranceCertificationMatcher("category");

        // all four components match
        test(true, matcher, new MockContextImpl("category",
                "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "whatever"));

        // context has no RA
        test(true, matcher, new MockContextImpl("category",
                "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", null));

        // these matches should fail because one component differs
        test(false, matcher, new MockContextImpl("category2", "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", null));
        test(false, matcher, new MockContextImpl("category", "http://macedir.org/entity-category-support",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", null));
        test(false, matcher, new MockContextImpl("category", "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified", null));
    }

    @Test
    public void testWithRA() {
        final Predicate<EntityAttributeContext> matcher = new AssuranceCertificationMatcher("category", "registrar");

        // all four components match
        test(true, matcher, new MockContextImpl("category",
                "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "registrar"));

        // context has no RA
        test(false, matcher, new MockContextImpl("category",
                "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", null));

        // these matches should fail because one component differs
        test(false, matcher, new MockContextImpl("category2", "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "registrar"));
        test(false, matcher, new MockContextImpl("category", "http://macedir.org/entity-category-support",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "registrar"));
        test(false, matcher, new MockContextImpl("category", "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified", "registrar"));
        test(false, matcher, new MockContextImpl("category", "urn:oasis:names:tc:SAML:attribute:assurance-certification",
                "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "registrar2"));
    }
    
    /**
     * A simple immutable implementation of {@link EntityAttributeContext}.
     * <p>
     * This is identical to the package-private implementation defined in 
     * {@link EntityAttributeFilteringStage}.
     * </p> 
     */
    private static class MockContextImpl implements EntityAttributeContext {

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
        public MockContextImpl(@Nonnull final String attributeValue,
                @Nonnull final String attributeName,
                @Nonnull final String attributeNameFormat,
                @Nullable final String registrar) {
            value = Constraint.isNotNull(attributeValue, "value may not be null");
            name = Constraint.isNotNull(attributeName, "name may not be null");
            nameFormat = Constraint.isNotNull(attributeNameFormat, "name format may not be null");
            registrationAuthority = registrar;
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

}

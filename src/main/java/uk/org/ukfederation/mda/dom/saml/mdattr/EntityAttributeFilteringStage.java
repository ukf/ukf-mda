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

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.dom.saml.mdrpi.RegistrationAuthority;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.xml.ElementSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.base.Predicate;

/**
 * A stage which filters entity attributes from entity definitions according to a supplied
 * set of rules.
 * 
 * For each attribute value under consideration, a {@link EntityAttributeContext} is built
 * from the components of the attribute and the entity's <code>registrationAuthority</code>,
 * if any.
 * 
 * Note that the <code>registrationAuthority</code> to be used is assumed to have been
 * extracted out into a {@link RegistrationAuthority} object in the entity's item metadata.
 */
public class EntityAttributeFilteringStage extends BaseStage<Element> {

    /** Namespace URI for SAML elements. */
    private static final String SAML_NS = "urn:oasis:names:tc:SAML:2.0:assertion";

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(EntityAttributeFilteringStage.class);

    /**
     * List of matching rules to apply to each attribute value. The list is applied in
     * order, with the first rule returning <code>true</code> terminating the evaluation.
     * This amounts to an implicit ORing of the individual rules, with early
     * termination.
     */
    private List<Predicate<EntityAttributeContext>> rules;
    
    /**
     * Apply the rules to a context.
     * 
     * @param ctx the context to apply the rules to
     * @return <code>true</code> if one of the rules returns <code>true</code>;
     *  otherwise <code>false</code>
     */
    private boolean applyRules(final EntityAttributeContext ctx) {
        for (final Predicate<EntityAttributeContext> rule : rules) {
            if (rule.apply(ctx)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Extract the registration authority for an entity from its entity metadata.
     * 
     * @param item the {@link Item} representing the entity
     * @return the registration authority URI, or <code>null</code> if not present
     */
    private String extractRegistrationAuthority(@Nonnull final Item<Element> item) {
        final List<RegistrationAuthority> regAuthList = item.getItemMetadata().get(RegistrationAuthority.class);
        if (regAuthList.isEmpty()) {
            return null;
        } else {
            return regAuthList.get(0).getRegistrationAuthority();
        }
    }
    
    /**
     * Extract an <code>Attribute</code> element's <code>NameFormat</code>, applying the
     * SAML standard's specified default if the XML attribute is not present.
     *  
     * @param attribute <code>Attribute</code> {@link Element}
     * @return <code>NameFormat</code> value, or the "unspecified" default
     */
    @Nonnull
    private String extractAttributeNameFormat(@Nonnull final Element attribute) {
        final Attr attr = attribute.getAttributeNode("NameFormat");
        if (attr == null) {
            return "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified";
        } else {
            return attr.getValue();
        }
    }
    
    /**
     * Filter an <code>Attribute</code> element.
     * 
     * @param attribute an <code>Attribute</code> element to filter
     * @param registrationAuthority the registration authority associated with the entity
     */
    private void filterAttribute(@Nonnull final Element attribute, @Nullable final String registrationAuthority) {
        // Determine the attribute's name; this will default to the empty string if not present
        final String attributeName = attribute.getAttribute("Name");
        
        // Determine the attribute's NameFormat
        final String attributeNameFormat = extractAttributeNameFormat(attribute);
        
        // Locate the AttributeValue elements to filter
        final List<Element> attributeValues =
                ElementSupport.getChildElementsByTagNameNS(attribute, SAML_NS, "AttributeValue");
        
        // Filter each AttributeValue in turn
        for (final Element value : attributeValues) {
            final String attributeValue = value.getTextContent();

            // Construct an entity attribute context to be matched against
            final EntityAttributeContext ctx =
                    new SimpleEntityAttributeContext(attributeValue, attributeName,
                            attributeNameFormat, registrationAuthority);            
            final boolean retain = applyRules(ctx);
            if (!retain) {
                log.debug("removing {}", ctx);
                attribute.removeChild(value);
            }
        }
    }
    
    /**
     * Filter an <code>EntityAttributes</code> extension element.
     * 
     * @param entityAttributes the <code>EntityAttributes</code> extension element
     * @param registrationAuthority the registration authority associated with the entity
     */
    private void filterEntityAttributes(@Nonnull final Element entityAttributes,
            @Nullable final String registrationAuthority) {
        // Locate the Attribute elements to filter
        final List<Element> attributes =
                ElementSupport.getChildElementsByTagNameNS(entityAttributes, SAML_NS, "Attribute");
        
        // Filter each Attribute in turn
        for (final Element attribute : attributes) {
            filterAttribute(attribute, registrationAuthority);
            
            // remove the Attribute container if it is now empty
            if (ElementSupport.getFirstChildElement(attribute) == null) {
                log.debug("removing empty Attribute");
                entityAttributes.removeChild(attribute);
            }
        }
    }
    
    @Override
    protected void doExecute(final Collection<Item<Element>> itemCollection) throws StageProcessingException {
        for (final Item<Element> item : itemCollection) {
            final Element entity = item.unwrap();
            
            // Establish the item's registrationAuthority, if any
            final String registrationAuthority = extractRegistrationAuthority(item);

            // Locate mdattr:EntityAttributes element
            final Element entityAttributes = SAMLMetadataSupport.getDescriptorExtensions(entity,
                    MDAttrSupport.MDATTR_ENTITY_ATTRIBUTES);
            if (entityAttributes != null) {
                filterEntityAttributes(entityAttributes, registrationAuthority);
                
                // remove the EntityAttributes container if it is now empty
                if (ElementSupport.getFirstChildElement(entityAttributes) == null) {
                    log.debug("removing empty EntityAttributes");
                    final Node extensions = entityAttributes.getParentNode();
                    extensions.removeChild(entityAttributes);
                }
            }
        }
    }


}

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

package uk.org.ukfederation.mda.dom.saml.mdattr;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import uk.org.ukfederation.mda.dom.Container;
import uk.org.ukfederation.mda.dom.saml.AttributeMaker;
import uk.org.ukfederation.mda.dom.saml.AttributeMatcher;
import uk.org.ukfederation.mda.dom.saml.AttributeValueMaker;
import uk.org.ukfederation.mda.dom.saml.AttributeValueMatcher;
import uk.org.ukfederation.mda.dom.saml.SAMLSupport;

/**
 * A stage which adds entity attribute values to entity definitions.
 */
public class EntityAttributeAddingStage extends BaseStage<Element> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(EntityAttributeAddingStage.class);
    
    /**
     * The <code>Name</code> of the attribute to be added.
     * 
     * The default value is for an entity attribute specifying an entity category.
     */
    @Nonnull
    private String attributeName = EntityCategorySupport.EC_CATEGORY_ATTR_NAME;
    
    /**
     * The <code>NameFormat</code> of the attribute to be added.
     * 
     * The default value is suitable for most entity attributes.
     */
    @Nonnull
    private String attributeNameFormat = EntityCategorySupport.EC_ATTR_NAME_FORMAT;
    
    /** The value of the attribute to be added. */
    @Nonnull
    private String attributeValue;
    
    /**
     * Whether we add <code>mdattr:EntityAttributes</code> as the first child
     * of <code>md:Extensions</code> if not already present.
     * 
     * Default: <code>false</code> (add as last child).
     */
    private boolean addingFirstChild;

    /** {@link Predicate} used to match existing Attribute elements. */
    @NonnullAfterInit
    private Predicate<Element> attributeMatcher;
    
    /** {@link Function} used to create new Attribute elements. */
    @NonnullAfterInit
    private Function<Container, Element> attributeMaker;
    
    /** {@link Predicate} used to match existing AttributeValue elements. */
    @NonnullAfterInit
    private Predicate<Element> attributeValueMatcher;

    /** {@link Function} used to create new AttributeValue elements. */
    @NonnullAfterInit
    private Function<Container, Element> attributeValueMaker;

    /**
     * Returns the attribute name.
     * 
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets the attribute name.
     * 
     * @param name the attributeName to set
     */
    public void setAttributeName(@Nonnull final String name) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        attributeName = Constraint.isNotNull(name, "attributeName can not be null");
    }

    /**
     * Gets the attribute name format.
     * 
     * @return the attributeNameFormat
     */
    @Nonnull
    public String getAttributeNameFormat() {
        return attributeNameFormat;
    }

    /**
     * Sets the attribute name format.
     * 
     * @param nameFormat the attributeNameFormat to set
     */
    public void setAttributeNameFormat(@Nonnull final String nameFormat) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        attributeNameFormat = Constraint.isNotNull(nameFormat, "attributeNameFormat can not be null");
    }

    /**
     * Gets the attribute value.
     * 
     * @return the attributeValue
     */
    @Nonnull
    public String getAttributeValue() {
        return attributeValue;
    }

    /**
     * Sets the attribute value.
     * 
     * @param value the attributeValue to set
     */
    public void setAttributeValue(@Nonnull final String value) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        attributeValue = Constraint.isNotNull(value, "attributeValue can not be null");
    }
    
    /**
     * Get whether we are adding <code>mdattr:EntityAttributes</code> as the first child
     * of <code>md:Extensions</code> if not already present.
     * 
     * @return <code>true</code> if adding as the first child, <code>false</code> if the last
     */
    public boolean isAddingFirstChild() {
        return addingFirstChild;
    }
    
    /**
     * Sets whether to add <code>mdattr:EntityAttributes</code> as the first child
     * of <code>md:Extensions</code> if not already present.
     * 
     * @param addFirst <code>true</code> to add as the first child, <code>false</code> as the last
     */
    public void setAddingFirstChild(final boolean addFirst) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        addingFirstChild = addFirst;
    }

    /**
     * Looks for the attribute value we want to add within the contents of a list of
     * Attribute container elements.
     * 
     * @param attributes {@link List} of Attribute {@link Container}s
     * @return true iff the value appears somewhere in the list of containers
     */
    private boolean attributeValuePresent(@Nonnull final List<Container> attributes) {
        for (final Container attribute : attributes) {
            if (attribute.findChild(attributeValueMatcher) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doExecute(final Collection<Item<Element>> itemCollection) throws StageProcessingException {
        for (final Item<Element> item : itemCollection) {
            final Element entity = item.unwrap();
            if (SAMLMetadataSupport.isEntityDescriptor(entity)) {
                // Start from the entity
                final Container entityContainer = new Container(entity);
                
                // Dig down to <Extensions>
                final Container extensionsContainer =
                        entityContainer.locateChild(SAMLSupport.EXTENSIONS_MATCHER,
                                SAMLSupport.EXTENSIONS_MAKER, Container.FIRST_CHILD);
                
                // Dig down to <EntityAttributes>
                final Container attributesContainer =
                        extensionsContainer.locateChild(MDAttrSupport.ENTITY_ATTRIBUTES_MATCHER,
                                MDAttrSupport.ENTITY_ATTRIBUTES_MAKER,
                                addingFirstChild ? Container.FIRST_CHILD : Container.LAST_CHILD);
                
                // Collect all matching <Attribute> containers
                final List<Container> attributes =
                        attributesContainer.findChildren(attributeMatcher);
                
                // If any of the existing attribute values match our value, we're done
                if (attributeValuePresent(attributes)) {
                    log.debug("attribute value '{}' already present", attributeValue);
                    continue;
                }
                
                // If not already present, re-locate an <Attribute> and add it in there.
                final Container attribute =
                        attributesContainer.locateChild(attributeMatcher, attributeMaker, Container.LAST_CHILD);
                attribute.addChild(attributeValueMaker, Container.LAST_CHILD);
            }
        }
    }
    
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (attributeValue == null) {
            throw new ComponentInitializationException("attributeValue property must be supplied");
        }

        attributeMatcher = new AttributeMatcher(attributeName, attributeNameFormat);
        attributeMaker = new AttributeMaker(attributeName, attributeNameFormat);
        attributeValueMatcher = new AttributeValueMatcher(attributeValue);
        attributeValueMaker = new AttributeValueMaker(attributeValue);
    }

    @Override
    protected void doDestroy() {
        attributeMatcher = null;
        attributeMaker = null;
        attributeValueMatcher = null;
        attributeValueMaker = null;
        super.doDestroy();
    }

}

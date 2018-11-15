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

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.pipeline.AbstractIteratingStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.collection.ClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.AttributeSupport;
import uk.org.ukfederation.mda.dom.Container;
import uk.org.ukfederation.mda.dom.ElementMaker;
import uk.org.ukfederation.mda.dom.ElementMatcher;
import uk.org.ukfederation.members.Members;

/**
 * Stage to inject scope lists into IdP entities from the members.xml file.
 */
public class ScopeInjectionStage extends AbstractIteratingStage<Element> {

    /** Element matcher for the <code>Extensions</code> element. */
    private static final Predicate<Element> EXTENSIONS_MATCHER =
        new ElementMatcher(SAMLMetadataSupport.EXTENSIONS_NAME);
    
    /**
     * Element maker for the <code>Extensions</code> element.
     *
     * Following existing UK federation conventions, we construct a prefixless
     * <code>Extensions</code> element.
     */
    private static final Function<Container, Element> EXTENSIONS_MAKER =
        new ElementMaker(new QName(SAMLMetadataSupport.MD_NS, SAMLMetadataSupport.EXTENSIONS_NAME.getLocalPart()));
    
    /** QName of the IDPSSODescriptor element. */
    private static final QName IDP_SSO_DESCRIPTOR_NAME = new QName(SAMLMetadataSupport.MD_NS, "IDPSSODescriptor");

    /** Element matcher for the IDPSSODescriptor element. */
    private static final Predicate<Element> IDP_SSO_DESCRIPTOR_MATCHER =
        new ElementMatcher(IDP_SSO_DESCRIPTOR_NAME);
    
    /** QName of the AttributeAuthorityDescriptor element. */
    private static final QName ATTRIBUTE_AUTHORITY_DESCRIPTOR_NAME = new QName(SAMLMetadataSupport.MD_NS,
            "AttributeAuthorityDescriptor");

    /** Element matcher for the AttributeAuthorityDescriptor element. */
    private static final Predicate<Element> ATTRIBUTE_AUTHORITY_DESCRIPTOR_MATCHER =
        new ElementMatcher(ATTRIBUTE_AUTHORITY_DESCRIPTOR_NAME);

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ScopeInjectionStage.class);
    
    /** Information about members of the UK federation. */
    @NonnullAfterInit private Members members;

    /**
     * Get the members API object.
     *
     * @return the members API object
     */
    @NonnullAfterInit
    public final Members getMembers() {
        return members;
    }
    
    /**
     * Set the members API object to use.
     * 
     * @param m the members API object to use
     */
    public void setMembers(@Nonnull final Members m) {
        members = m;
    }
    
    @Override
    protected void doExecute(@Nonnull final Item<Element> item) throws StageProcessingException {
        doExecute(item.unwrap(), item.getItemMetadata());
    }
    
    /**
     * Process a single entity.
     * 
     * @param entity DOM {@link Element} containing the <code>EntityDescriptor</code>
     * @param metadata item metadata collection for the entity
     * @throws StageProcessingException if the item is not an entity
     */
    protected void doExecute(@Nonnull final Element entity,
            @Nonnull final ClassToInstanceMultiMap<ItemMetadata> metadata) throws StageProcessingException {

        // Verify that we're dealing with an entity descriptor.
        if (!SAMLMetadataSupport.isEntityDescriptor(entity)) {
            throw new StageProcessingException("item is not an EntityDescriptor");
        }
        
        // Get the entityID
        final String entityID = AttributeSupport.getAttributeValue(entity, null, "entityID");
        if (entityID == null) {
            throw new StageProcessingException("entity does not have an entityID");
        }
        
        // Get the pushed scope list. If there are none, we're done.
        final List<String> pushedScopes = members.scopesForEntity(entityID);
        if (pushedScopes == null) {
            return;
        }

        log.debug("entity {} has {} pushed scopes", entityID, pushedScopes.size());

        // Make a container for the EntityDescriptor
        final Container entityContainer = new Container(entity);
        
        // Handle the Extensions on the entity itself. This must always be present.
        addPushedScopes(entityContainer, pushedScopes);
        
        // Add to each of the appropriate role descriptors, if present.
        addPushedScopesToRole(entityContainer, pushedScopes, IDP_SSO_DESCRIPTOR_MATCHER);
        addPushedScopesToRole(entityContainer, pushedScopes, ATTRIBUTE_AUTHORITY_DESCRIPTOR_MATCHER);
    }

    /**
     * Add pushed scopes to a given role descriptor within an entity.
     * 
     * @param entity {@link Container} for the entity
     * @param scopes list of scopes to be added
     * @param roleMatcher matcher for the role descriptor element
     */
    private void addPushedScopesToRole(@Nonnull final Container entity, @Nonnull final List<String> scopes,
            @Nonnull final Predicate<Element> roleMatcher) {
        final Container role = entity.findChild(roleMatcher);
        if (role == null) {
            return;
        }
        addPushedScopes(role, scopes);
    }

    /**
     * Add each of the given scopes to the <code>Extensions</code> element within the given descriptor.
     * 
     * An <code>Extensions</code> element is created if it is not present.
     * 
     * @param descriptor {@link Container} for the descriptor
     * @param scopes list of scopes to be added
     */
    private void addPushedScopes(@Nonnull final Container descriptor, @Nonnull final List<String> scopes) {
        final Container extensions =
                descriptor.locateChild(EXTENSIONS_MATCHER, EXTENSIONS_MAKER, Container.FIRST_CHILD);
        for (final String scope : scopes) {
            final Element newScope = ShibbolethMetadataSupport.SCOPE_MAKER.apply(extensions);
            newScope.setTextContent(scope);
            AttributeSupport.appendAttribute(newScope, ShibbolethMetadataSupport.REGEXP_ATTRIB_NAME, "false");
            extensions.addChild(newScope, Container.LAST_CHILD);
        }
    }

    @Override
    protected void doDestroy() {
        members = null;

        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (members == null) {
            throw new ComponentInitializationException(
                    "members API object must be provided");
        }
    }
}

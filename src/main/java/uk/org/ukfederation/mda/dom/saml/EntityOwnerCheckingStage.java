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

import org.w3c.dom.Element;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.pipeline.BaseIteratingStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.collection.ClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.AttributeSupport;
import net.shibboleth.utilities.java.support.xml.ElementSupport;
import uk.org.ukfederation.mda.UKFedLabelSupport;
import uk.org.ukfederation.members.Members;
import uk.org.ukfederation.members.jaxb.MemberElement;

/**
 * Stage to check that each entity in a collection is owned by a UK federation member.
 */
public class EntityOwnerCheckingStage extends BaseIteratingStage<Element> {

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
    protected boolean doExecute(@Nonnull final Item<Element> item) throws StageProcessingException {
        doExecute(item.unwrap(), item.getItemMetadata());
        return true;
    }
    
    /**
     * Process a single entity.
     * 
     * @param entity DOM {@link Element} containing the <code>EntityDescriptor</code>
     * @param metadata item metadata collection for the entity
     */
    protected void doExecute(@Nonnull final Element entity,
            @Nonnull final ClassToInstanceMultiMap<ItemMetadata> metadata) {
        // Verify that we're dealing with an entity descriptor.
        if (!SAMLMetadataSupport.isEntityDescriptor(entity)) {
            metadata.put(new ErrorStatus(getId(), "item is not an EntityDescriptor"));
            return;
        }
        
        // Acquire its UK federation organization ID
        final Element ukfMemberLabel =
                SAMLMetadataSupport.getDescriptorExtensions(entity, UKFedLabelSupport.UK_FEDERATION_MEMBER_NAME);
        if (ukfMemberLabel == null) {
            addError(metadata, "entity has no " +
                    UKFedLabelSupport.UK_FEDERATION_MEMBER_NAME.getLocalPart() + " element");
            return;
        }
        final String orgID = AttributeSupport.getAttributeValue(ukfMemberLabel,
                UKFedLabelSupport.UK_FEDERATION_MEMBER_ORGID);
        if (orgID == null) {
            addError(metadata, "entity's " +
                    UKFedLabelSupport.UK_FEDERATION_MEMBER_NAME.getLocalPart() +
                    " element has no " +
                    UKFedLabelSupport.UK_FEDERATION_MEMBER_ORGID + " attribute");
            return;
        }

        // Acquire the Organization element.
        final Element orgElement = ElementSupport.getFirstChildElement(entity,
                new QName(SAMLMetadataSupport.MD_NS, "Organization"));
        if (orgElement == null) {
            addError(metadata, "entity has no Organization element");
            return;
        }
        
        // Extract the organization's name.
        final String orgName = extractOrganizationName(orgElement);
        if (orgName == null) {
            addError(metadata, "entity has no OrganizationName with xml:lang='en'");
            return;
        }
        
        // Check that this is a valid organization name
        final MemberElement member = members.getMemberByName(orgName);
        if (member == null) {
            addError(metadata, "unknown owner name: " + orgName);
            return;
        }

        // Cross-check the entity's orgID against the value from members.xml.
        if (!orgID.equals(member.getID())) {
            addError(metadata, "mismatched orgID: " + orgID + " should be " + member.getID());
            return;
        }
    }
    
    /**
     * Given an &lt;Organization&gt; element, extract the canonical name of the
     * organization owning the entity.
     * 
     * This will be the text content of the &lt;OrganizationName&lt; child element
     * whose <code>xml:lang</code> attribute contains "en".
     * 
     * @param orgElement the entity's &lt;Organization&gt; element
     * 
     * @return the organization's name, or <code>null</code> if not present
     */
    private String extractOrganizationName(@Nonnull final Element orgElement) {
        // Acquire all of the OrganizationName elements
        final List<Element> orgNames = ElementSupport.getChildElements(orgElement,
                new QName(SAMLMetadataSupport.MD_NS, "OrganizationName"));
        
        // Extract the first one with xml:lang = "en"
        for (final Element orgName : orgNames) {
            final String lang = AttributeSupport.getXMLLang(orgName);
            if ("en".equals(lang)) {
                return orgName.getTextContent();
            }
        }
        
        // Perhaps there is a country-specific variant instead.
        for (final Element orgName : orgNames) {
            final String lang = AttributeSupport.getXMLLang(orgName);
            if (lang != null && lang.startsWith("en-")) {
                return orgName.getTextContent();
            }
        }
        
        // No xml:lang="en" found.
        return null;
    }

    /**
     * Add the given error status to the entity.
     * 
     * @param metadata item metadata collection to put the status into
     * @param message message describing the error
     */
    private void addError(@Nonnull final ClassToInstanceMultiMap<ItemMetadata> metadata,
            @Nonnull final String message) {
        metadata.put(new ErrorStatus(getId(), message));
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

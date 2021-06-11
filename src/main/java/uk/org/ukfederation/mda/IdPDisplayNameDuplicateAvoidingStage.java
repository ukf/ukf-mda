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

package uk.org.ukfederation.mda;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.ImmutableMap;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.InfoStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.dom.saml.mdrpi.RegistrationAuthority;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.collection.ClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.xml.ElementSupport;
import uk.org.ukfederation.mda.upstream.MDUISupport;

/**
 * A stage which examines the discovery names used by each of a collection of entities, and deconflicts
 * those discovery names prioritising "our" entities and adjusting the discovery names of others.
 *
 * <p>Properties:</p>
 * 
 * <dl>
 *   <dt>registrationAuthority</dt>
 *   <dd>
 *      The registration authority whose discovery names
 *      should be preserved. By default, <code>"http://ukfederation.org.uk"</code>.
 *   </dd>
 *
 *   <dt>registrationAuthorityDisplayNames</dt>
 *   <dd>
 *      A {@link Map} from registration authority URIs to the codes to be used to represent
 *      them when creating new discovery names. By default, empty.
 *   </dd>
 *
 *   <dt>defaultRegistrationAuthorityDisplayName</dt>
 *   <dd>
 *      The value to use if a registration authority's URI does not appear in the
 *      <code>registrationAuthorityDisplayNames</code> map. By default, <code>"??"</code>.
 *   </dd>
 *   
 *   <dt>nameFormat</dt>
 *   <dd>
 *      The {@link MessageFormat} format string used to compose new display names.
 *      By default, <code>"[{1}] {0}"</code>, resulting in names of the form "<code>[CODE] oldname</code>".
 *   </dd>
 * </dl>
 *   
 *
 * @see <a href="https://repo.infr.ukfederation.org.uk/ukf/ukf-meta/-/issues/63">ukf/ukf-meta#63</a>
 */
@ThreadSafe
public class IdPDisplayNameDuplicateAvoidingStage extends BaseStage<Element> {

    /** {@link QName} representing an SAML metadata <code>IDPSSODescriptor</code>. */
    private static final QName MD_IDP_SSO_DESCRIPTOR = new QName(SAMLMetadataSupport.MD_NS, "IDPSSODescriptor");
    
    /** {@link QName} representing a SAML metadata <code>OrganizationDisplayName</code>. */
    private static final QName MD_ORG_DISPLAY_NAME = new QName(SAMLMetadataSupport.MD_NS, "OrganizationDisplayName");

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(IdPDisplayNameDuplicateAvoidingStage.class);

    /**
     * "Our" registration authority name; the one whose discovery names must be preserved.
     *
     * <p>
     * The default value is <code>http://ukfederation.org.uk</code>, signifying the
     * UK federation.
     * </p>
     */
    @Nonnull @GuardedBy("this")
    private String registrationAuthority = "http://ukfederation.org.uk";

    /**
     * Replacement display names for registration authorities.
     */
    @Nonnull @NonnullElements @GuardedBy("this")
    private Map<String, String> registrationAuthorityDisplayNames = Collections.emptyMap();

    /**
     * Default registration authority display name, used when the registration authority does
     * not appear in the collection of registration authority display names.
     */
    @Nonnull @GuardedBy("this")
    private String defaultRegistrationAuthorityDisplayName = "??";

    /**
     * The {@link MessageFormat} format string used to compose new display names.
     *
     * <ul>
     *   <li><code>{0}</code> represents the entity's original discovery name.</li>
     *   <li><code>{1}</code> represents the display name for the entity's registration authority.</li>
     * </ul>
     */
    @Nonnull @GuardedBy("this")
    private String nameFormat = "[{1}] {0}";

    /**
     * Get the registration authority.
     * 
     * @return the registration authority
     */
    public final synchronized String getRegistrationAuthority() {
        return registrationAuthority;
    }
    
    /**
     * Set the registration authority for "our" identity providers.
     *
     * @param newAuthority registration authority URI indicating "our" identity providers
     */
    public final synchronized void setRegistrationAuthority(@Nonnull final String newAuthority) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(newAuthority, "registration authority can not be null");
        registrationAuthority = newAuthority;
    }

    /**
     * Returns the map of display names for registration authorities.
     * 
     * @return {@link Map} of display names for authorities.
     */
    @Nonnull @NonnullElements
    public final synchronized Map<String, String> getRegistrationAuthorityDisplayNames() {
        return registrationAuthorityDisplayNames;
    }

    /**
     * Sets the map of display names for registration authorities.
     * 
     * @param names {@link Map} of display names for registration authorities.
     */
    public final synchronized void setRegistrationAuthorityDisplayNames(@Nullable final Map<String, String> names) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        registrationAuthorityDisplayNames = ImmutableMap.copyOf(names);
    }

    /**
     * Get the default registration authority display name.
     *
     * @return the default registration authority display name
     */
    public final synchronized String getDefaultRegistrationAuthorityDisplayName() {
        return defaultRegistrationAuthorityDisplayName;
    }

    /**
     * Set the default registration authority display name.
     *
     * @param newDefault the new default registration authority display name
     */
    public final synchronized void
            setDefaultRegistrationAuthorityDisplayName(@Nonnull final String newDefault) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(newDefault, "default registration authority display name can not be null");
        defaultRegistrationAuthorityDisplayName = newDefault;
    }

    /**
     * Get the {@link MessageFormat} format string used to create new discovery names.
     *
     * @return the {@link MessageFormat} format string
     */
    public final synchronized String getNameFormat() {
        return nameFormat;
    }

    /**
     * Set the {@link MessageFormat} format string used to create new discovery names.
     *
     * @param newNameFormat the new {@link MessageFormat} format string
     */
    public final synchronized void setNameFormat(@Nonnull final String newNameFormat) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(newNameFormat, "name format can not be null");
        nameFormat = newNameFormat;
    }

    /**
     * Return all elements with the given name within the given entity descriptor.
     * 
     * @param entity the {@link Element} representing the entity's <code>md:EntityDescriptor</code>
     * @param qname the {@link QName} being searched for
     * @return a {@link List} of {@link Element}s with the requested name
     */
    @Nonnull @NonnullElements
    private List<Element> extractElements(@Nonnull final Element entity,
            @Nonnull final QName qname) {
        final List<Element> names = new ArrayList<>();
        final NodeList elements = entity.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        for (int n = 0; n < elements.getLength(); n++) {
            final Element element = (Element)elements.item(n);
            names.add(element);
        }
        return names;
    }

    /**
     * Return a list of <@link Element>s representing the entity's discovery names.
     * 
     * <p>This either collects the entity's <code>mdui:DisplayName</code> elements or,
     * if there are none, any <code>md:OrganizationDisplayName</code> elements it has.
     *
     * @param entity the {@link Element} representing the entity
     * @return a {@link List} of {@link Element}s, possibly empty
     */
    @Nonnull @NonnullElements
    private List<Element> extractDiscoveryNameElements(@Nonnull final Element entity) {
        // Look at mdui:DisplayName first
        final List<Element> mduiNames = extractElements(entity, MDUISupport.DISPLAYNAME_NAME);
        if (!mduiNames.isEmpty()) {
            return mduiNames;
        }
        
        // Otherwise, fall back to legacy md:OrganizationDisplayName elements
        return extractElements(entity, MD_ORG_DISPLAY_NAME);
    }

    @Nonnull @NonnullElements private List<String> extractDiscoveryNames(
            @Nonnull @NonnullElements final List<Element> elements) {
        final List<String> names = new ArrayList<>();
        for (final Element element : elements) {
            names.add(element.getTextContent().trim());
        }
        return names;
    }

    /**
     * Extract the registration authority name for the given item, provided by its {@link RegistrationAuthority}
     * item metadata.
     * 
     * <p>If no {@link RegistrationAuthority} metadata is available, return <code>null</code>.</p>
     * 
     * <p>If more than one {@link RegistrationAuthority} is present (this should not normally be the case)
     * then just return the first.
     *
     * @param item {@link Item} representing the registered entity
     * @return the entity's registration authority, or <code>null</code>
     */
    @Nullable
    private String extractRegistrationAuthority(@Nonnull final Item<Element> item) {
        final List<RegistrationAuthority> regAuths = item.getItemMetadata().get(RegistrationAuthority.class);
        if (regAuths.isEmpty()) {
            return null;
        } else {
            return regAuths.get(0).getRegistrationAuthority();
        }
    }

    /**
     * Separate a list of entities into two lists of identity providers, on the basis of
     * the registration authority associated with each.
     * 
     * <p>Non-entities, and non-IdPs, are ignored.</p>
     * 
     * @param items collection of items representing entities to process
     * @param ourIdPs collection of identity providers registered by the given registration authority
     * @param otherIdPs collection of identity providers registered by any other registration authority
     * @param ourRegAuth registration authority to make the distinction between the lists with
     */
    private void separateEntities(@Nonnull @NonnullElements final Collection<Item<Element>> items,
            @Nonnull @NonnullElements final List<Item<Element>> ourIdPs,
            @Nonnull @NonnullElements final List<Item<Element>> otherIdPs,
            @Nonnull final String ourRegAuth) {
        for (final Item<Element> item : items) {
            final Element entity = item.unwrap();
            final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();

            // All items must be entities
            if (!SAMLMetadataSupport.isEntityDescriptor(entity)) {
                metadata.put(new ErrorStatus(getId(), "item was not an EntityDescriptor"));
                continue;
            }

            // All items must have a registrationAuthority
            final String regAuth = extractRegistrationAuthority(item);
            if (regAuth == null) {
                metadata.put(new ErrorStatus(getId(), "item is missing a registration authority"));
                continue;
            }

            // Process only IdPs
            if (!ElementSupport.getChildElements(entity, MD_IDP_SSO_DESCRIPTOR).isEmpty()) {
                if (regAuth.equals(ourRegAuth)) {
                    ourIdPs.add(item);
                } else {
                    otherIdPs.add(item);
                }
                
            }
        }        
    }

    @Override
    protected void doExecute(@Nonnull @NonnullElements final Collection<Item<Element>> items)
            throws StageProcessingException {

        // Collect commonly used guarded fields.
        final String ourRegAuth = getRegistrationAuthority();

        // Collect two lists of IdPs: "ours" and "others"
        final List<Item<Element>> ourIdPs = new ArrayList<>();
        final List<Item<Element>> otherIdPs = new ArrayList<>();
        separateEntities(items, ourIdPs, otherIdPs, ourRegAuth);

        // Collect discovery names used by "our" entities
        final Set<String> ourNames = new HashSet<>();
        for (final Item<Element> item : ourIdPs) {
            final List<Element> discoveryNameElements = extractDiscoveryNameElements(item.unwrap());
            final List<String> discoveryNames = extractDiscoveryNames(discoveryNameElements);
            // None of these names should be in the collection already, although they MAY include duplicates
            for (final String name : discoveryNames) {
                // A clash between this entity and our other entities is fatal
                if (ourNames.contains(name)) {
                    throw new StageProcessingException("discovery name " + name +
                            " appears in more than one entity from " + ourRegAuth);
                }
            }
            // Add all of the new names to the collection
            ourNames.addAll(discoveryNames);
        }
        
        // Now check the "other" IdPs to see if they need deconfliction
        final MessageFormat newNameFormatter = new MessageFormat(getNameFormat());
        for (final Item<Element> item : otherIdPs) {
            final List<Element> discoveryNameElements = extractDiscoveryNameElements(item.unwrap());
            for (final Element nameElement : discoveryNameElements) {
                final String name = nameElement.getTextContent().trim();
                if (ourNames.contains(name)) {
                    // Deconflict this name
                    String registrationHandle =
                            getRegistrationAuthorityDisplayNames().get(extractRegistrationAuthority(item));
                    if (registrationHandle == null) {
                        registrationHandle = getDefaultRegistrationAuthorityDisplayName();
                    }
                    final StringBuffer newName = newNameFormatter.format(
                            new Object[] { name, registrationHandle },
                            new StringBuffer(), null);
                    log.debug("discovery name changed from '{}' to '{}'", name, newName);
                    item.getItemMetadata().put(new InfoStatus(getId(),
                            "discovery name changed to '" + newName + "'"));
                    nameElement.setTextContent(newName.toString());
                }
            }
        }
    }

}

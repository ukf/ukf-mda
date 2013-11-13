/*
 * Copyright (C) 2013 University of Edinburgh.
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

package uk.org.ukfederation.mda.validate.mdui;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.collection.ClassToInstanceMultiMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/** Base class for validation stages. */
@ThreadSafe
public abstract class BaseValidationStage extends BaseStage<Element> {

    /**
     * Returns the {@link Element} representing the EntityDescriptor which is the
     * closest-containing ancestor of the given element.
     * 
     * @param element {@link Element} to locate the ancestor Entity of.
     * @return ancestor EntityDescriptor {@link Element}, or null.
     */
    protected Element ancestorEntity(@Nonnull final Element element) {
        assert element != null;
        for (Element e = element; e != null; e = (Element) e.getParentNode()) {
            if (SAMLMetadataSupport.isEntityDescriptor(e)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Add an {@link ErrorStatus} to the given item, in respect of the given {@link Element}.
     * If the item is an EntitiesDescriptor, interpose an identifier for the individual
     * EntityDescriptor.
     * 
     * @param item      {@link Item} to add the error to
     * @param element   {@link Element} the error reflects
     * @param error     error text
     */
    protected void addError(@Nonnull final Item<Element> item, @Nonnull final Element element,
            @Nonnull final String error) {
        assert item != null;
        assert element != null;
        assert error != null;
        final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        String prefix = "";
        if (SAMLMetadataSupport.isEntitiesDescriptor(element)) {
            final Element entity = ancestorEntity(element);
            final Attr id = entity.getAttributeNode("ID");
            if (id != null) {
                prefix = id.getTextContent() + ": ";
            } else {
                Attr entityID = entity.getAttributeNode("entityID");
                if (entityID != null) {
                    prefix = entityID.getTextContent() + ": ";
                }
            }
        }
        metadata.put(new ErrorStatus(getId(), prefix + error));
    }
    
    /**
     * Validate an individual {@link Item}.
     * @param item the {@link Item} to validate.
     * @param docElement the unwrapped document {@link Element}.
     */
    protected abstract void validateItem(Item<Element> item, Element docElement);
    
    /** {@inheritDoc} */
    protected void doExecute(@Nonnull @NonnullElements final Collection<Item<Element>> items)
            throws StageProcessingException {
        for (Item<Element> item : items) {
            validateItem(item, item.unwrap());
        }
    }

}

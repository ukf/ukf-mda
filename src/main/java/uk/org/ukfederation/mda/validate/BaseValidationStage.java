/*
 * Copyright (C) 2011 University of Edinburgh.
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

package uk.org.ukfederation.mda.validate;

import java.util.Collection;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import net.jcip.annotations.ThreadSafe;
import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.saml.SamlMetadataSupport;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.util.ClassToInstanceMultiMap;

/** Base class for validation stages. */
@ThreadSafe
public abstract class BaseValidationStage extends BaseStage<DomElementItem> {

    /**
     * Returns the {@link Element} representing the EntityDescriptor which is the
     * closest-containing ancestor of the given element.
     * 
     * @param element {@link Element} to locate the ancestor Entity of.
     * @return ancestor EntityDescriptor {@link Element}, or null.
     */
    protected Element ancestorEntity(final Element element) {
        for (Element e = element; e != null; e = (Element) e.getParentNode()) {
            if (SamlMetadataSupport.isEntityDescriptor(e)) {
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
     * @param item      {@link DomElementItem} to add the error to
     * @param element   {@link Element} the error reflects
     * @param error     error text
     */
    protected void addError(final DomElementItem item, final Element element, final String error) {
        ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
        String prefix = "";
        if (SamlMetadataSupport.isEntitiesDescriptor(element)) {
            Element entity = ancestorEntity(element);
            Attr id = entity.getAttributeNode("ID");
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
     * Validate an individual {@link DomElementItem}.
     * @param item the {@link DomElementItem} to validate.
     * @param docElement the unwrapped document {@link Element}.
     */
    protected abstract void validateItem(DomElementItem item, Element docElement);
    
    /** {@inheritDoc} */
    protected void doExecute(final Collection<DomElementItem> items) throws StageProcessingException {
        for (DomElementItem item : items) {
            validateItem(item, item.unwrap());
        }
    }

}

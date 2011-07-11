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

package uk.org.ukfederation.mda;

import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.saml.SamlMetadataSupport;
import net.shibboleth.metadata.pipeline.BaseIteratingStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;

import org.opensaml.util.xml.AttributeSupport;
import org.w3c.dom.Element;

/**
 * A stage which, for each EntityDescriptor collection element, adds a {@link UKId}, with the entity's ID, to
 * the metadata item.
 */
public class EntityDescriptorUKIdPopulationStage extends BaseIteratingStage<DomElementItem> {

    /** {@inheritDoc} */
    protected boolean doExecute(DomElementItem item) throws StageProcessingException {
        Element metadataElement = item.unwrap();

        if (SamlMetadataSupport.isEntityDescriptor(metadataElement)) {
            String id = AttributeSupport.getAttributeValue(metadataElement, null, "ID");
            if (id != null) {
                item.getItemMetadata().put(new UKId(id));
            }
        }

        return true;
    }
    
}
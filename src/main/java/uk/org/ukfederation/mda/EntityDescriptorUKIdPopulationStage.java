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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.jcip.annotations.ThreadSafe;
import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.saml.SamlMetadataSupport;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.ComponentInitializationException;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.util.ClassToInstanceMultiMap;

import org.opensaml.util.StringSupport;
import org.opensaml.util.xml.AttributeSupport;
import org.w3c.dom.Element;

/**
 * A stage which, for each EntityDescriptor collection element, adds a {@link UKId}, with the entity's ID, to
 * the metadata item.
 */
@ThreadSafe
public class EntityDescriptorUKIdPopulationStage extends BaseStage<DomElementItem> {

    /**
     * Compiled regular expression.
     */
    private Pattern pattern;

    /** {@inheritDoc} */
    protected void doExecute(Collection<DomElementItem> items) throws StageProcessingException {

        // ID values that we have already seen (they must be unique)
        Set<String> ids = new HashSet<String>(items.size());
        
        for (DomElementItem item : items) {
           Element element = item.unwrap();
           ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
           
           if (!SamlMetadataSupport.isEntityDescriptor(element)) {
               // all items must be EntityDescriptor elements
               metadata.put(new ErrorStatus(getId(), "item was not an EntityDescriptor"));
           } else {
               String id = AttributeSupport.getAttributeValue(element, null, "ID");
               String eid = StringSupport.nullToEmpty(AttributeSupport.getAttributeValue(element, null, "entityID"));
               if (id == null) {
                   metadata.put(new ErrorStatus(getId(), "EntityDescriptor " + eid + " did not have an ID attribute"));
               } else if (!pattern.matcher(id).matches()) {
                   metadata.put(new ErrorStatus(getId(), "EntityDescriptor " + eid + " has an ID value '" + id +
                           "' that does not look like a UK federation identifier"));
               } else if (ids.contains(id)) {
                   metadata.put(new ErrorStatus(getId(), "EntityDescriptor " + eid + " has duplicate ID value " + id));
               } else {
                   metadata.put(new UKId(id));
                   ids.add(id);
               }
           }
           
        }
    }
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        pattern = Pattern.compile("^uk[0-9]{6}$");
    }

}
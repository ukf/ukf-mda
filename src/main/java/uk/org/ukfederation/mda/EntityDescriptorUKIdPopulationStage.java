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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.w3c.dom.Element;

import com.google.common.base.Strings;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.pipeline.AbstractStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.collection.ClassToInstanceMultiMap;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A stage which, for each EntityDescriptor collection element, adds a {@link UKId}, with the entity's ID, to
 * the metadata item.
 */
@ThreadSafe
public class EntityDescriptorUKIdPopulationStage extends AbstractStage<Element> {

    /**
     * Compiled regular expression.
     */
    @GuardedBy("this") private Pattern pattern;

    @Override
    protected void doExecute(@Nonnull @NonnullElements final List<Item<Element>> items)
            throws StageProcessingException {

        // ID values that we have already seen (they must be unique)
        final Set<String> ids = new HashSet<>(items.size());
        
        for (final Item<Element> item : items) {
           final Element element = item.unwrap();
           final ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
           
           if (!SAMLMetadataSupport.isEntityDescriptor(element)) {
               // all items must be EntityDescriptor elements
               metadata.put(new ErrorStatus(getId(), "item was not an EntityDescriptor"));
           } else {
               final String id = AttributeSupport.getAttributeValue(element, null, "ID");
               final String eid = Strings.nullToEmpty(AttributeSupport.getAttributeValue(element, null, "entityID"));
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
    
    @Override
    protected void doDestroy() {
        pattern = null;

        super.doDestroy();
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        pattern = Pattern.compile("^uk[0-9]{6}$");
    }

}

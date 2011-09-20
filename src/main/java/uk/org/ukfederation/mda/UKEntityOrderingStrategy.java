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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import net.shibboleth.metadata.ItemId;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.saml.EntitiesDescriptorAssemblerStage.ItemOrderingStrategy;
import net.shibboleth.metadata.dom.saml.SamlMetadataSupport;

/**
 * Implements an ordering strategy for UK federation aggregates.
 * 
 * UK registered entities (with {@link UKId} metadata) come first, in the
 * natural order of their {@link UKId}.  So, for example, <code>ID="uk000001"</code>
 * comes before <code>ID="uk000002"</code> which comes before any entity not
 * sporting a {@link UKId} at all.
 * 
 * Items with {@link ItemId} metadata come next, again in the natural ordering
 * for {@link ItemId}.  Items with neither {@link UKId} or {@link ItemId} come
 * last in the ordering.
 */
public class UKEntityOrderingStrategy implements ItemOrderingStrategy {
    
    /**
     * Helper class which wraps a {@link DomElementItem} but extracts any
     * associated {@link UKId} and {@link ItemId} for simpler comparisons.
     */
    private static class OrderableItem implements Comparable<OrderableItem> {
        
        /** Number of fields we are capable of comparing. */
        private static final int NFIELDS = 4;
        
        /** The wrapped {@link DomElementItem}. */
        private final DomElementItem item;
        
        /** Array of field values. */
        private final String[] fields = new String[NFIELDS];
        
        /**
         * Constructor.
         * 
         * @param domItem the {@link DomElementItem} to wrap.
         */
        public OrderableItem(DomElementItem domItem) {
            item = domItem;

            Element docElement = domItem.unwrap();
            if (SamlMetadataSupport.isEntitiesDescriptor(docElement)) {
                // EntitiesDescriptors come before everything else
                fields[0] = "yes";
                
                // Named EntitiesDescriptors come before unnamed, in order of name
                Attr nameAttr = docElement.getAttributeNode("Name");
                if (nameAttr != null) {
                    fields[1] = nameAttr.getTextContent();
                }
            }
            
            List<UKId> ukids = item.getItemMetadata().get(UKId.class);
            if (ukids.size() != 0) {
                fields[2] = ukids.get(0).getId();
            }

            List<ItemId> itemids = item.getItemMetadata().get(ItemId.class);
            if (itemids.size() != 0) {
                fields[3] = itemids.get(0).getId();
            }
        }

        /**
         * Compare a single field.
         * 
         * @param sThis value of the field in this object
         * @param sThat value of the field in the other object
         * @return comparison value
         */
        private int compareField(final String sThis, final String sThat) {
            if (sThis != null) {
                if (sThat != null) {
                    // both have this field; direct comparison
                    return sThis.compareTo(sThat);
                } else {
                    // we have this field, other does not: we should order first
                    return -1;
                }
            } else if (sThat != null) {
                // we do not have this field, other does: we should order last
                return 1;
            } else {
                // neither has this field
                return 0;
            }
        }
        
        /** {@inheritDoc} */
        public int compareTo(OrderableItem o) {
            for (int fno = 0; fno < NFIELDS; fno++) {
                int compared = compareField(fields[fno], o.fields[fno]);
                if (compared != 0) {
                    return compared;
                }
            }

            // nothing to choose between them
            return 0;
        }
        
        /**
         * Unwrap the wrapped {@link DomElementItem}.
         * 
         * @return the wrapped {@link DomElementItem}.
         */
        public DomElementItem unwrap() {
            return item;
        }
    }

    /** {@inheritDoc} */
    public List<DomElementItem> order(Collection<DomElementItem> items) {
        
        // Construct an orderable list wrapping the original items.
        List<OrderableItem> orderableList = new ArrayList<OrderableItem>(items.size());
        for (DomElementItem item : items) {
            orderableList.add(new OrderableItem(item));
        }
        
        // sort the orderable list
        Collections.sort(orderableList);
        
        // extract the result into a new collection
        List<DomElementItem> results = new ArrayList<DomElementItem>(items.size());
        for (OrderableItem result : orderableList) {
            results.add(result.unwrap());
        }

        return results;
    }

}

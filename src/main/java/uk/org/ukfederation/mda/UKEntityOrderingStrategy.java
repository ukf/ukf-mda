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

import net.shibboleth.metadata.ItemId;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.saml.EntitiesDescriptorAssemblerStage.ItemOrderingStrategy;

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
        
        /** The wrapped {@link DomElementItem}. */
        private final DomElementItem item;
        
        /** The {@link UKId} for the wrapped item, if it has one. */
        private final UKId ukid;
        
        /** The {@link ItemId} for the wrapped item, if it has one. */
        private final ItemId itemid;
        
        /**
         * Constructor.
         * 
         * @param domItem the {@link DomElementItem} to wrap.
         */
        public OrderableItem(DomElementItem domItem) {
            item = domItem;

            List<UKId> ukids = item.getItemMetadata().get(UKId.class);
            if (ukids.size() != 0) {
                ukid = ukids.get(0);
            } else {
                ukid = null;
            }

            List<ItemId> itemids = item.getItemMetadata().get(ItemId.class);
            if (itemids.size() != 0) {
                itemid = itemids.get(0);
            } else {
                itemid = null;
            }
        }

        /** {@inheritDoc} */
        public int compareTo(OrderableItem o) {

            // Compare on the basis of UKId, if present
            if (ukid != null) {
                if (o.ukid != null) {
                    return ukid.compareTo(o.ukid);
                } else {
                    // we have UKId, other does not: we should order first
                    return -1;
                }
            } else if (o.ukid != null) {
                // we do not have UKId, other does: we should order last
                return 1;
            }
            
            // Neither has a UKId, second level of comparison will be on ItemId
            if (itemid != null) {
                if (o.itemid != null) {
                    return itemid.compareTo(o.itemid);
                } else {
                    // we have ItemId, other does not: we should order first
                    return -1;
                }
            } else if (o.itemid != null) {
                // we do not have ItemId, other does: we should order last
                return 1;
            }
            
            // If neither has either UKId or ItemId, there's nothing to choose
            // between them
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

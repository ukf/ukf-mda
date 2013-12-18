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

package uk.org.ukfederation.mda;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Predicate;

import net.shibboleth.metadata.dom.DOMElementItem;

/** An implementation of {@link Predicate} that selects entities with {@link UKId}s. */
@ThreadSafe
public class UKEntitySelectionStrategy implements Predicate<DOMElementItem> {

    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nonnull final DOMElementItem item) {
        return item.getItemMetadata().containsKey(UKId.class);
    }

}

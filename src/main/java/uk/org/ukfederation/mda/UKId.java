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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Objects;

import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

/** Carries the fragment ID for an item of UK federation registered metadata. */
@Immutable
public class UKId implements ItemMetadata, Comparable<UKId> {

    /** UK federation fragment ID for the Item. */
    private String id;

    /**
     * Constructor.
     * 
     * @param ukid The UK federation fragment ID for the entity, never null
     */
    public UKId(@Nonnull @NotEmpty final String ukid) {
        id = Constraint.isNotNull(StringSupport.trimOrNull(ukid), "UK ID may not be null or empty");
    }

    /**
     * Gets a unique identifier for the data carried by the Item.
     * 
     * @return unique identifier for the data carried by the Item
     */
    @Nonnull public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UKId)) {
            return false;
        }

        final UKId other = (UKId) obj;
        return Objects.equal(id, other.id);
    }

    @Override
    public int compareTo(final UKId other) {
        return getId().compareTo(other.getId());
    }
    
}

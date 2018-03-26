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
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import com.google.common.base.Objects;

/** Carries the fragment ID for an item of UK federation registered metadata. */
@ThreadSafe
public class UKId implements ItemMetadata, Comparable<UKId> {

    /** Serial version UID. */
    private static final long serialVersionUID = 1755199108111044022L;
    
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

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public int compareTo(final UKId other) {
        return getId().compareTo(other.getId());
    }
    
}

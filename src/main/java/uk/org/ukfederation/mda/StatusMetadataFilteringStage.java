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

import net.shibboleth.metadata.*;
import net.shibboleth.metadata.pipeline.AbstractItemMetadataSelectionStage;
import net.shibboleth.metadata.pipeline.Stage;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.collection.ClassToInstanceMultiMap;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;


/**
 * A {@link Stage} that removes matching {@link StatusMetadata} entries from
 * an {@link Item}'s metadata collection.
 *
 * <p>During execution, the stage examines all {@link StatusMetadata} instances
 * attached to a matching item and compares their status messages against a
 * configured message value. Message matching can be performed using either
 * exact equality or substring matching, depending on the value of
 * {@code exactMatch}.</p>
 *
 * <p>When a matching {@link StatusMetadata} instance is found, it is removed
 * from the item's metadata.</p>
 *
 * <p>This stage can be used, for example, to remove specific
 * {@link net.shibboleth.metadata.InfoStatus},
 * {@link net.shibboleth.metadata.WarningStatus}, or
 * {@link net.shibboleth.metadata.ErrorStatus} entries based on their
 * associated status message.</p>
 *
 * @param <T> the type of item content processed by this stage
 */
@ThreadSafe
public class StatusMetadataFilteringStage<T> extends AbstractItemMetadataSelectionStage<T, StatusMetadata> {

    /** Class logger. */
    private static final @Nonnull Logger LOG = LoggerFactory.getLogger(StatusMetadataFilteringStage.class);

    /** The message to match against status metadata entries. */
    @NonnullAfterInit @GuardedBy("this")
    private String message;
    /**
     * Whether the message comparison should be exact or partial.
     *
     * <p>If {@code true}, an exact string match is required;
     * otherwise, a substring match is performed.</p>
     */
    private boolean exactMatch;


    /**
     * Sets the message to match against status entries.
     *
     * @param statusMessage the message to match against status metadata entries.
     */
    public synchronized void setMessage(@Nonnull String statusMessage) {
        checkSetterPreconditions();
        message = statusMessage;
    }

    /**
     * Sets whether the message comparison should be exact or partial.
     *
     * @param exactMatching {@code true} for exact message match; {@code false} for substring match
     */
    public void setExactMatch(boolean exactMatching) {
        checkSetterPreconditions();
        exactMatch = exactMatching;
    }

    /**
     * Gets the message to match against status entries.
     * @return message
     */
    @Nonnull
    public final synchronized String getMessage() {
        return message;
    }

    /**
     * Gets whether the message comparison should be exact or partial.
     * @return isExactMatch
     */
    public final boolean isExactMatch() {
        return exactMatch;
    }

    @Override
    protected synchronized void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (message == null) {
            throw new ComponentInitializationException("Unable to initialize " + getId()
                    + ", message may not be null");
        }
    }

    /**
     * Removes all matching {@link StatusMetadata} entries from the supplied item.
     *
     * <p>A status entry matches when its message satisfies the configured
     * matching criteria. Matching entries are removed from the item's metadata
     * collection.</p>
     *
     * @param items the full set of items being processed
     * @param matchingItem the item whose metadata is being evaluated
     * @param matchingMetadata the metadata associated with the matching item
     */
    @Override
    protected void doExecute(@Nonnull @NonnullElements final List<Item<T>> items,
                             @Nonnull final Item<T> matchingItem,
                             @Nonnull @NonnullElements final ClassToInstanceMultiMap<StatusMetadata> matchingMetadata) {

        for (final StatusMetadata statusMetadata : matchingMetadata.values()) {
            final String statusMessage = statusMetadata.getStatusMessage();
            if ((isExactMatch() && getMessage().equals(statusMessage) ||
                    (!isExactMatch() && statusMessage.contains(getMessage())))) {
                matchingItem.getItemMetadata().remove(statusMetadata);
            }
        }
    }
}

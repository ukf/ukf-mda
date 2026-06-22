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

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.StatusMetadata;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.function.Predicate;



/**
 * A {@link Predicate} implementation that evaluates whether a {@link Item<Element>}
 * contains {@link StatusMetadata} of a specified {@code statusType} with a matching message.
 *
 * <p>The match can be configured as either an exact match or a substring match using
 * the {@code exactMatch} flag. </p>
 */
@Immutable
public class StatusMetadataCheckingStrategy implements Predicate<Item<Element>> {

    /** Logger for this class. */
    private static final @Nonnull Logger LOG = LoggerFactory.getLogger(StatusMetadataCheckingStrategy.class);

    /** The type of {@link StatusMetadata} to evaluate. */
    @Nonnull private final Class<? extends StatusMetadata> statusType;
    /** The message to match against status metadata entries. */
    @Nonnull private final String message;
    /**
     * Whether the message comparison should be exact or partial.
     *
     * <p>If {@code true}, an exact string match is required;
     * otherwise, a substring match is performed.</p>
     */
    private final boolean exactMatch;


    /**
     * Constructs a new strategy for evaluating {@link StatusMetadata}.
     *
     * @param status the type of status metadata to match
     * @param statusMessage the message to match against status entries
     * @param exactMatching {@code true} for exact message match; {@code false} for substring match
     */
    public StatusMetadataCheckingStrategy(@Nonnull final Class<? extends StatusMetadata> status, @Nonnull final String statusMessage, final boolean exactMatching) {
        message = statusMessage;
        exactMatch = exactMatching;
        statusType = status;
    }



    /**
     * Evaluates whether the supplied {@link Item<Element>} contains a
     * {@link StatusMetadata} entry of the configured type with a matching message.
     *
     * @param item the metadata item to evaluate (must not be {@code null})
     * @return {@code true} if a matching status entry is found; {@code false} otherwise
     */
    @Override
    public boolean test(@Nonnull final Item<Element> item) {
        final List<? extends StatusMetadata> statusList = item.getItemMetadata().get(statusType);
        for (StatusMetadata status: statusList) {
            final String statusMessage = status.getStatusMessage();
            if ((exactMatch && message.equals(statusMessage) ||
                    (!exactMatch && statusMessage.contains(message)))) {
                return true;
            }
        }
        return false;
    }
}

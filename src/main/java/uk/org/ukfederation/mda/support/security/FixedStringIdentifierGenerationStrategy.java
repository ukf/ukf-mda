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

package uk.org.ukfederation.mda.support.security;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;

/**
 * Identifier generation strategy using a fixed identifier string.
 *
 * This can be used in circumstances where there is no requirement that identifiers be
 * different from each other.
 */
@Immutable
public class FixedStringIdentifierGenerationStrategy implements IdentifierGenerationStrategy {

    /** Fixed identifier to use for all invocations. */
    @Nonnull private final String identifier;

    /**
     * Constructor.
     *
     * @param id fixed identifier to use for all invocations.
     */
    public FixedStringIdentifierGenerationStrategy(@Nonnull final String id) {
        identifier = Constraint.isNotNull(id, "identifier may not be null");
    }

    @Override
    @Nonnull
    public String generateIdentifier() {
        return identifier;
    }

    @Override
    @Nonnull
    public String generateIdentifier(final boolean xmlSafe) {
        return identifier;
    }

}

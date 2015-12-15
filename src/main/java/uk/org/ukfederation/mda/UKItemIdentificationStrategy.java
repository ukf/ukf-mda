/*
 * Copyright (C) 2015 University of Edinburgh.
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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.saml.mdrpi.RegistrationAuthorityItemIdentificationStrategy;

/**
 * Item identification strategy for UK federation deployment.
 * 
 * The basic identifier strategy is to use a {@link UKId} if one is present, or
 * fall back to the super class implementation (which in turn
 * falls back to a configurable static value such as "unknown").
 */
@ThreadSafe
public class UKItemIdentificationStrategy extends RegistrationAuthorityItemIdentificationStrategy {

    @Override
    @Nonnull protected String getBasicIdentifier(@Nonnull final Item<?> item) {
        final List<UKId> itemIds = item.getItemMetadata().get(UKId.class);
        if (itemIds != null && !itemIds.isEmpty()) {
            return itemIds.get(0).getId();
        } else {
            return super.getBasicIdentifier(item);
        }
    }
    
}

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

package uk.org.ukfederation.mda.validate;

import javax.annotation.Nonnull;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.WarningStatus;

/**
 * Base class for validators.
 * 
 * Encapsulates the notion of an identifier for each validator class, and helper
 * methods for constructing status metadata.
 */
public abstract class BaseValidator {

    /** Identifier for this validator. Normally set per validation class. */
    private final String validatorId;
    
    /**
     * Constructor.
     * 
     * @param id an identifier for this validator
     */
    public BaseValidator(@Nonnull final String id) {
        validatorId = id;
    }

    /**
     * Return this validator's identifier.
     * 
     * @return this validator's identifier
     */
    @Nonnull
    public final String getValidatorId() {
        return validatorId;
    }
    
    /**
     * Construct a modified component identifier from the stage identifier and the
     * validator identifier.
     * 
     * @param stageId identifier for the calling stage
     * 
     * @return composite component identifier
     */
    private String makeComponentId(@Nonnull final String stageId) {
        return stageId + "/" + getValidatorId();
    }

    /**
     * Add an {@link ErrorStatus} to the given {@link Item}.
     * 
     * @param message message to include in the status metadata
     * @param item {@link Item} to add the status metadata to
     * @param stageId component identifier for the calling stage
     */
    protected void addError(@Nonnull final String message, @Nonnull final Item<?> item,
            @Nonnull final String stageId) {
        item.getItemMetadata().put(new ErrorStatus(makeComponentId(stageId), message));
    }
    
    /**
     * Add a {@link WarningStatus} to the given {@link Item}.
     * 
     * @param message message to include in the status metadata
     * @param item {@link Item} to add the status metadata to
     * @param stageId component identifier for the calling stage
     */
    protected void addWarning(@Nonnull final String message, @Nonnull final Item<?> item,
            @Nonnull final String stageId) {
        item.getItemMetadata().put(new WarningStatus(makeComponentId(stageId), message));
    }
    
}

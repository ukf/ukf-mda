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

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.component.DestructableComponent;
import net.shibboleth.utilities.java.support.component.IdentifiableComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;

/**
 * Interface for a validator to be applied to an object in the context of a given {@link Item}.
 * 
 * @param <T> type of the object to be validated
 */
public interface Validator<T> extends DestructableComponent, IdentifiableComponent,
    InitializableComponent {
    
    /**
     * Apply the validator to the object in the given {@link Item} context.
     * 
     * The validator influences future processing by adding item metadata to the {@link Item}.
     * 
     * @param e the object to be validated
     * @param item the {@link Item} context for the validation
     * @param stageId the identifier for the stage that is requesting the validation, for
     *      inclusion in status metadata
     * @throws StageProcessingException if an error occurs during validation
     */
    public void validate(@Nonnull T e, @Nonnull Item<?> item, @Nonnull String stageId)
        throws StageProcessingException;
    
}

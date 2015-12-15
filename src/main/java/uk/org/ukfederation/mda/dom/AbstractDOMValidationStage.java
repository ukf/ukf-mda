/*
 * Copyright (C) 2013-2015 University of Edinburgh.
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

package uk.org.ukfederation.mda.dom;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import uk.org.ukfederation.mda.validate.Validator;

/**
 * Base stage to apply a collection of validators to each object from each item.
 * 
 * @param <T> type of the object to be validated
 */ 
public abstract class AbstractDOMValidationStage<T> extends AbstractDOMTraversalStage {

    /** The collection of validators to apply. */
    @Nonnull
    private List<Validator<T>> validators = Collections.emptyList();
    
    /**
     * Set the list of validators to apply to each item.
     * 
     * @param newValidators the list of validators to set
     */
    public void setValidators(@Nonnull final List<Validator<T>> newValidators) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        validators = ImmutableList.copyOf(Iterables.filter(newValidators, Predicates.notNull()));
    }
    
    /**
     * Gets the list of validators being applied to each item.
     * 
     * @return list of validators
     */
    @Nonnull
    public List<Validator<T>> getValidators() {
        return Collections.unmodifiableList(validators);
    }

    /**
     * Apply each of the configured validators in turn to the provided object.
     * 
     * @param obj object to be validated
     * @param context context for the validation
     * @throws StageProcessingException if errors occur during processing
     */
    protected void applyValidators(@Nonnull final T obj, @Nonnull final TraversalContext context)
            throws StageProcessingException {
        for (final Validator<T> validator: validators) {
            final Validator.Action action = validator.validate(obj, context.getItem(), getId());
            if (action == Validator.Action.DONE) {
                return;
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doDestroy() {
        validators = null;
        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        for (Validator<T> validator : validators) {
            if (!validator.isInitialized()) {
                validator.initialize();
            }
        }
    }

}
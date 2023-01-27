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

package uk.org.ukfederation.mda.dom.saml.ukfedlabel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemIdentificationStrategy;
import net.shibboleth.metadata.pipeline.AbstractFilteringStage;
import net.shibboleth.metadata.pipeline.Stage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.component.ComponentInitializationException;
import uk.org.ukfederation.mda.UKItemIdentificationStrategy;

/**
 * A {@link Stage} to apply flow constraints to entities.
 *
 * @param <T> type of item being processed
 */
@ThreadSafe
public class FlowConstraintApplyingStage<T> extends AbstractFilteringStage<T> {
    
    /**
     * Item identification strategy to use if we need to throw errors.
     *
     * <p>
     * In the current implementation, always a {@link UKItemIdentificationStrategy}, but
     * in principle we might extend this if we upstream this component.
     * </p>
     */
    @GuardedBy("this") private final ItemIdentificationStrategy<T> idStrategy = new UKItemIdentificationStrategy<>();
    
    /**
     * The name of the flow this stage is controlling.
     */
    @GuardedBy("this") @NonnullAfterInit
    private String flowName;

    /**
     * Returns the designated item identification strategy.
     *
     * @return the item identification strategy
     */
    @Nonnull private final synchronized ItemIdentificationStrategy<T> getIdStrategy() {
        return idStrategy;
    }

    /**
     * Get the name of the flow this stage is controlling.
     *
     * @return name of the flow
     */
    @NonnullAfterInit
    public final synchronized String getFlowName() {
        return flowName;
    }

    /**
     * Set the name of the flow this stage is controlling.
     *
     * @param newFlowName name of the flow to control
     */
    public final synchronized void setFlowName(@Nonnull final String newFlowName) {
        flowName = newFlowName;
    }

    @Override
    protected boolean doExecute(@Nonnull @NonnullElements final Item<T> item) throws StageProcessingException {
        final List<EnableFlow> enables = item.getItemMetadata().get(EnableFlow.class);
        final List<DisableFlow> disables = item.getItemMetadata().get(DisableFlow.class);
        
        // Can't have both enables and disables
        if (!enables.isEmpty() && !disables.isEmpty()) {
            throw new StageProcessingException("item " + getIdStrategy().getItemIdentifier(item)
                + " has both enables and disables");
        }
        
        // Process enables, if present
        if (!enables.isEmpty()) {
            for (final EnableFlow enable : enables) {
                if (getFlowName().equals(enable.getFlowName())) {
                    return true;
                }
            }
            // Did not match an enable: discard
            return false;
        }
        
        // Process disables, if present
        if (!disables.isEmpty()) {
            for (final DisableFlow disable : disables) {
                if (getFlowName().equals(disable.getFlowName())) {
                    return false;
                }
            }
            // Did not match a disable: preserve
            return true;
        }
        
        // Preserve by default
        return true;
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (flowName == null) {
            throw new ComponentInitializationException("flowName must not be null");
        }
    }

}

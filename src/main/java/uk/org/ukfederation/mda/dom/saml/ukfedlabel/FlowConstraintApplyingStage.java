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

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemIdentificationStrategy;
import net.shibboleth.metadata.pipeline.AbstractFilteringStage;
import net.shibboleth.metadata.pipeline.Stage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import uk.org.ukfederation.mda.UKItemIdentificationStrategy;

/**
 * A {@link Stage} to apply flow constraints to entities.
 *
 * @param <T> type of item being processed
 */
public class FlowConstraintApplyingStage<T> extends AbstractFilteringStage<T> {
    
    /** Item identification strategy to use if we need to throw errors. */
    private final ItemIdentificationStrategy idStrategy = new UKItemIdentificationStrategy();
    
    /**
     * The name of the flow this stage is controlling.
     */
    @NonnullAfterInit
    private String flowName;

    /**
     * Get the name of the flow this stage is controlling.
     *
     * @return name of the flow
     */
    @NonnullAfterInit
    public String getFlowName() {
        return flowName;
    }

    /**
     * Set the name of the flow this stage is controlling.
     *
     * @param newFlowName name of the flow to control
     */
    public void setFlowName(@Nonnull final String newFlowName) {
        flowName = newFlowName;
    }

    @Override
    protected boolean doExecute(@Nonnull @NonnullElements final Item<T> item) throws StageProcessingException {
        final List<EnableFlow> enables = item.getItemMetadata().get(EnableFlow.class);
        final List<DisableFlow> disables = item.getItemMetadata().get(DisableFlow.class);
        
        // Can't have both enables and disables
        if (!enables.isEmpty() && !disables.isEmpty()) {
            throw new StageProcessingException("item " + idStrategy.getItemIdentifier(item)
                + " has both enables and disables");
        }
        
        // Process enables, if present
        if (!enables.isEmpty()) {
            for (final EnableFlow enable : enables) {
                if (flowName.equals(enable.getFlowName())) {
                    return true;
                }
            }
            // Did not match an enable: discard
            return false;
        }
        
        // Process disables, if present
        if (!disables.isEmpty()) {
            for (final DisableFlow disable : disables) {
                if (flowName.equals(disable.getFlowName())) {
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

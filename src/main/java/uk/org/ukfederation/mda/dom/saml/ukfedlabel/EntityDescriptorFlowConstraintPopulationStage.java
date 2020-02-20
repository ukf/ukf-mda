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
import javax.annotation.concurrent.ThreadSafe;

import org.w3c.dom.Element;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.pipeline.AbstractIteratingStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;

/**
 * A stage which extracts flow constraint extensions from each entity in the collection,
 * then adds {@link EnableFlow} and {@link DisableFlow} instances to the item metadata.
 */
@ThreadSafe
public class EntityDescriptorFlowConstraintPopulationStage extends AbstractIteratingStage<Element> {

    @Override
    protected void doExecute(@Nonnull final Item<Element> item) throws StageProcessingException {
        final Element entity = item.unwrap();

        // Process EnableFlow extensions
        final List<Element> enables = SAMLMetadataSupport.getDescriptorExtensionList(entity,
                UKFedLabelSupport.UK_FEDERATION_ENABLE_FLOW_NAME);
        for (final Element enable : enables) {
            final String flowName = enable.getAttribute("flow");
            item.getItemMetadata().put(new EnableFlow(flowName));
        }

        // Process DisableFlow extensions
        final List<Element> disables = SAMLMetadataSupport.getDescriptorExtensionList(entity,
                UKFedLabelSupport.UK_FEDERATION_DISABLE_FLOW_NAME);
        for (final Element disable : disables) {
            final String flowName = disable.getAttribute("flow");
            item.getItemMetadata().put(new DisableFlow(flowName));
        }
    }

}

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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.shibboleth.metadata.ItemMetadata;

/**
 * Abstract parent class for all flow constraints.
 */
@Immutable
public abstract class FlowConstraint implements ItemMetadata {
    
    /** Name of the flow this constraint applies to. */
    @Nonnull private final String flowName;
    
    /**
     * Constructor.
     *
     * @param flow name of the flow this constraint applies to
     */
    FlowConstraint(@Nonnull final String flow) {
        flowName = flow;
    }

    /**
     * Get the name of the flow this constraint applies to.
     *
     * @return the flow name
     */
    @Nonnull public String getFlowName() {
        return flowName;
    }

}

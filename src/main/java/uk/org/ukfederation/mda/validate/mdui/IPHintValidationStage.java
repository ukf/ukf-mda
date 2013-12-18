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

package uk.org.ukfederation.mda.validate.mdui;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.net.IPRange;

import org.w3c.dom.Element;

import uk.org.ukfederation.mda.dom.AbstractDOMTraversalStage;

/**
 * A stage which validates mdui:IPHint elements.
 */
@ThreadSafe
public class IPHintValidationStage extends AbstractDOMTraversalStage {

    /** Whether to check that the CIDR notation describes a network. Defaults to true. */
    private boolean checkingNetworks = true;
    
    /**
     * Gets whether the stage is checking for network addresses only.
     * 
     * @return whether the stage is checking for network addresses only
     */
    public boolean isCheckingNetworks() {
        return checkingNetworks;
    }

    /**
     * Sets whether the stage is checking for network addresses only.
     * 
     * @param check whether to check for network addresses only
     */
    public void setCheckingNetworks(final boolean check) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        this.checkingNetworks = check;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean applicable(Element element) {
        return MDUISupport.MDUI_NS.equals(element.getNamespaceURI()) &&
                "IPHint".equals(element.getLocalName());
    }

    /** {@inheritDoc} */
    @Override
    protected void visit(@Nonnull final Element ipHint, @Nonnull final TraversalContext context) {
        assert ipHint != null;
        assert context != null;
        final String hint = ipHint.getTextContent();
        try {
            final IPRange range = IPRange.parseCIDRBlock(hint);
            if (checkingNetworks) {
                if (range.getHostAddress() != null) {
                    addError(context.getItem(), ipHint, "invalid IPHint '" + hint +
                            "': CIDR notation represents a host, not a network");
                }
            }
        } catch (IllegalArgumentException e) {
            addError(context.getItem(), ipHint, "invalid IPHint '" + hint + "': " + e.getMessage());
        }
    }

}

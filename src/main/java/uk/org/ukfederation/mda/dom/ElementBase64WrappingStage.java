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

package uk.org.ukfederation.mda.dom;

import javax.annotation.Nonnull;

import org.w3c.dom.Element;

import net.shibboleth.metadata.dom.AbstractElementVisitingStage;
import net.shibboleth.metadata.dom.DOMTraversalContext;
import net.shibboleth.metadata.dom.ElementVisitor;
import uk.org.ukfederation.mda.dom.impl.Base64WrappingVisitor;

/**
 * Stage to wrap the assumed Base64 text text content of named elements
 * within a {@link net.shibboleth.metadata.dom.DOMElementItem}.
 */
public class ElementBase64WrappingStage extends AbstractElementVisitingStage {

    /** Visitor to apply to each visited element. */
    @Nonnull private final ElementVisitor visitor = new Base64WrappingVisitor();

    @Override
    protected void visit(@Nonnull final Element e, @Nonnull final DOMTraversalContext context) {
        visitor.visitElement(e, context.getItem());
    }

}

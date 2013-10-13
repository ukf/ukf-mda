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

package uk.org.ukfederation.mda.dom;

import javax.annotation.Nonnull;

import net.shibboleth.metadata.dom.DOMElementItem;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * {@link Node} visitor which trims leading and trailing whitespace from the
 * visited node's text content.
 */
class WhitespaceTrimmingVisitor implements NodeVisitor, ElementVisitor, AttrVisitor {

    /** {@inheritDoc} */
    public void visitNode(@Nonnull final Node visited, @Nonnull final DOMElementItem item) {
        assert visited != null;
        assert item != null;
        final String originalText = visited.getTextContent();
        final String newText = originalText.trim();
        visited.setTextContent(newText);
    }

    /** {@inheritDoc} */
    public void visitElement(@Nonnull final Element visited, @Nonnull final DOMElementItem item) {
        visitNode(visited, item);
    }
    
    /** {@inheritDoc} */
    public void visitAttr(@Nonnull final Attr visited, @Nonnull final DOMElementItem item) {
        visitNode(visited, item);
    }
    
}

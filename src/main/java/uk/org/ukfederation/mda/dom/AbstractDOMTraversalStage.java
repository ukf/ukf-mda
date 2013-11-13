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

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.xml.ElementSupport;

import org.w3c.dom.Element;

/**
 * A DOM traversal class using the template method pattern.
 */
@ThreadSafe
public abstract class AbstractDOMTraversalStage extends BaseStage<Element> {

    /**
     * Indicates whether the visitor should be applied to a particular {@link Element}.
     * 
     * @param e {@link Element} to which we may wish to apply the visitor
     * 
     * @return <code>true</code> if the visitor should be applied to this {@link Element}.
     */
    protected abstract boolean applicable(@Nonnull final Element e);

    /**
     * Visit a particular {@link Element}.
     * 
     * @param e the {@link Element} to visit.
     * @param item the context {@link Item}.
     */
    protected abstract void visit(@Nonnull final Element e, @Nonnull final Item<Element> item);
    
    /**
     * Depth-first traversal of the DOM tree rooted in an element, applying the
     * visitor when appropriate.  The traversal snapshots the child elements at
     * each level, so that the visitor could in principle reorder or delete them
     * during processing.
     * 
     * @param e {@link Element} to start from
     * @param item {@link Item} context for the traversal
     */
    private void traverse(@Nonnull final Element e, @Nonnull final Item<Element> item) {
        final List<Element> children = ElementSupport.getChildElements(e);
        for (Element child : children) {
            traverse(child, item);
        }
        if (applicable(e)) {
            visit(e, item);
        }
    }
    
    /** {@inheritDoc} */
    protected void doExecute(Collection<Item<Element>> itemCollection) throws StageProcessingException {
        for (Item<Element> item : itemCollection) {
            final Element docElement = item.unwrap();
            traverse(docElement, item);
        }
    }

}

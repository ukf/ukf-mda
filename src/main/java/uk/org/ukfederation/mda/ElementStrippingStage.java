/*
 * Copyright (C) 2011 University of Edinburgh.
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

package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.jcip.annotations.ThreadSafe;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A stage which removes all instances of the specified element from DOM metadata.
 */
@ThreadSafe
public class ElementStrippingStage extends BaseStage<DomElementItem> {

    /** Namespace of the element to strip. */
    private String elementNamespace;

    /** Name of the element to strip. */
    private String elementName;

    /**
     * Get the namespace of the element to strip.
     * 
     * @return namespace of the element to strip
     */
    public String getElementNamespace() {
        return elementNamespace;
    }

    /**
     * Set the namespace of the element to strip.
     * 
     * @param namespace namespace of the element to strip
     */
    public void setElementNamespace(final String namespace) {
        elementNamespace = namespace;
    }

    /**
     * Get the name of the element to strip.
     * 
     * @return the name of the element to strip
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * Set the name of the element to strip.
     * 
     * @param name the name of the element to strip
     */
    public void setElementName(final String name) {
        elementName = name;
    }

    /** {@inheritDoc} */
    protected void doExecute(final Collection<DomElementItem> items) throws StageProcessingException {
        for (DomElementItem item : items) {
            final Element docElement = item.unwrap();

            // List all the matching descendant elements in this document in document order
            // Note that this list will never include the document element itself
            NodeList nodeList = docElement.getElementsByTagNameNS(elementNamespace, elementName);

            // Copy these into a list, because a NodeList can change length at any time
            final int nNodes = nodeList.getLength();
            List<Element> elements = new ArrayList<Element>(nNodes);
            for (int eIndex = 0; eIndex < nNodes; eIndex++) {
                elements.add((Element) nodeList.item(eIndex));
            }
            
            // Remove the elements from the document
            for (Element element : elements) {
                element.getParentNode().removeChild(element);
            }
        }
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (elementNamespace == null) {
            throw new ComponentInitializationException("target namespace can not be null or empty");
        }
        if (elementName == null) {
            throw new ComponentInitializationException("target element name can not be null or empty");
        }
    }

}
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

import java.util.Collection;

import net.jcip.annotations.ThreadSafe;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.saml.SamlMetadataSupport;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;

import org.opensaml.util.xml.ElementSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A stage which removes all empty Extensions elements from SAML metadata.
 */
@ThreadSafe
public class RemoveEmptyExtensionsStage extends BaseStage<DomElementItem> {

    /**
     * Determines whether a given DOM element has any element children.
     * 
     * @param element Element to check for child elements.
     * @return true if and only if the Element has child elements.
     */
    private boolean hasChildElements(final Element element) {
        Node firstChild = ElementSupport.getFirstChildElement(element);
        return firstChild != null;
    }
    
    /** {@inheritDoc} */
    protected void doExecute(final Collection<DomElementItem> items) throws StageProcessingException {
        for (DomElementItem item : items) {
            Element element = item.unwrap();
            
            // List all the Extensions elements in this document in document order
            NodeList extensionList = element.getElementsByTagNameNS(SamlMetadataSupport.MD_NS, "Extensions");
            
            // Process in reverse order so that Extensions inside Extensions are
            // handled correctly.
            for (int eIndex = extensionList.getLength()-1; eIndex >= 0; eIndex--) {
                Element extensions = (Element) extensionList.item(eIndex);
                if (!hasChildElements(extensions)) {
                    extensions.getParentNode().removeChild(extensions);
                }
            }
        }
    }

}
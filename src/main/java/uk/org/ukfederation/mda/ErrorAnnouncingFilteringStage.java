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
import java.util.Iterator;
import java.util.List;

import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.ItemId;
import net.shibboleth.metadata.ItemMetadata;
import net.shibboleth.metadata.WarningStatus;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.util.ClassToInstanceMultiMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A stage implementation which announces errors and any attached warnings in elements
 * to the logging system, then removes the associated items.
 */
public class ErrorAnnouncingFilteringStage extends BaseStage<DomElementItem> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ErrorAnnouncingFilteringStage.class);

    /**
     * Whether the stage should throw an exception if errors are encountered.
     * Default value: {@value}.
     */
    private boolean terminating;
    
    /**
     * Gets whether the stage should throw an exception if errors are encountered.
     * 
     * @return whether the stage should throw an exception if errors are encountered.
     */
    public boolean isTerminating() {
        return terminating;
    }

    /**
     * Sets whether the stage should throw an exception if errors are encountered.
     *  
     * @param terminate whether the stage should throw an exception if errors are encountered.
     */
    public void setTerminating(boolean terminate) {
        this.terminating = terminate;
    }

    /**
     * Performs the stage processing on the given {@link DomElementItem} collection.
     * 
     * @param collection collection of {@link DomElementItem}s to process.
     *  
     * @throws TerminationException if errors are encountered and the stage has been set to
     *                              terminate on errors.
     */
    public void doExecute(Collection<DomElementItem> collection) throws TerminationException {
        int errorsEncountered = 0;
        Iterator<DomElementItem> iterator = collection.iterator();
        while (iterator.hasNext()) {
            DomElementItem item = iterator.next();
            ClassToInstanceMultiMap<ItemMetadata> metadata = item.getItemMetadata();
            List<ErrorStatus> errors = metadata.get(ErrorStatus.class);
            if (errors.size() > 0) {
                errorsEncountered++;
                
                // Establish a name for this element
                List<UKId> ukId = metadata.get(UKId.class);
                List<ItemId> entityId = metadata.get(ItemId.class);
                String name;
                if (ukId.size() > 0) {
                    name = ukId.get(0).getId();
                } else if (entityId.size() > 0) {
                    name = entityId.get(0).getId();
                } else {
                    name = "element";
                }
                log.error("removing " + name + "; reasons follow");

                // Mention any status items that were previously warnings on this element
                if (log.isWarnEnabled()) {
                    for (WarningStatus warning : metadata.get(WarningStatus.class)) {
                        log.warn("   {}: {}", new Object[] {warning.getComponentId(), warning.getStatusMessage()});
                    }
                }

                // Mention the errors on this element, which are the reason we are removing it
                for (ErrorStatus error : errors) {
                    log.error("   {}: {}", new Object[] {error.getComponentId(), error.getStatusMessage()});
                }

                // remove the element
                iterator.remove();
            }
        }
        
        /*
         * If there have been errors and the stage has been set to terminate on
         * errors, throw an exception now.
         */
        if (errorsEncountered>0 && isTerminating()) {
            throw new TerminationException("errors encountered; terminating");
        }

    }

}

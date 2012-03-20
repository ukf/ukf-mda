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

package uk.org.ukfederation.mda.statistics;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;
import net.shibboleth.metadata.dom.DomElementItem;
import net.shibboleth.metadata.dom.saml.SamlMetadataSupport;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * A stage implementation which generates statistics for a collection
 * of entities.
 */
@ThreadSafe
public class StatisticsVelocityStage extends BaseStage<DomElementItem> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(StatisticsVelocityStage.class);
    
    /** Pool of parsers used to parse XML to DOM output. */
    private ParserPool parserPool;

    /** Name of the template to invoke for this stage. */
    private String templateName;

    /**
     * Gets the pool of DOM parsers used to parse XML to DOM.
     * 
     * @return pool of DOM parsers used to parse XML to DOM
     */
    public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Sets the pool of DOM parsers used to parse XML to DOM.
     * 
     * @param pool pool of DOM parsers used to parse XML to DOM
     */
    public synchronized void setParserPool(final ParserPool pool) {
        if (isInitialized()) {
            return;
        }
        parserPool = pool;
    }

    /**
     * Gets the name of the template to invoke.
     * 
     * @return the name of the template to invoke.
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Sets the name of the template to invoke.
     * 
     * @param name the name of the template to invoke.
     */
    public synchronized void setTemplateName(String name) {
        templateName = name;
    }


    /** {@inheritDoc} */
    public void doExecute(Collection<DomElementItem> collection) throws StageProcessingException {
        
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.description",
                "Velocity Classpath Resource Loader");
        ve.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();

        VelocityContext context = new VelocityContext();
        context.put("name", new String("Velocity"));

        final Set<DomElementItem> entities = new HashSet<DomElementItem>(collection.size());
        
        for (DomElementItem item : collection) {
            if (SamlMetadataSupport.isEntityDescriptor(item.unwrap())) {
                entities.add(item);
            }
        }
        context.put("entities", entities);
        
        StringWriter w = new StringWriter();
        log.debug("merging template " + templateName);
        ve.mergeTemplate(templateName, Velocity.ENCODING_DEFAULT, context, w);
        log.debug("resulting string is " + w.toString());
        
        log.debug("parsing to DOM document");
        try {
            Document doc = parserPool.parse(new StringReader(w.toString()));
            collection.clear();
            collection.add(new DomElementItem(doc));
        } catch (XMLParserException e) {
            throw new StageProcessingException("could not parse template output", e);
        }
    }


    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (parserPool == null) {
            throw new ComponentInitializationException("Unable to initialize " + getId() +
                    ", parserPool may not be null");
        }

        if (templateName == null) {
            throw new ComponentInitializationException("Unable to initialize " + getId() +
                    ", templateName must be specified");
        }
        
    }

}

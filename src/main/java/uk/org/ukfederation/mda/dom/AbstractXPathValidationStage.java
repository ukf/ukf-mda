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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.metadata.pipeline.BaseStage;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.SimpleNamespaceContext;

/**
 * Abstract base class allowing the results of an XPath expression in a DOM document
 * to be validated as a given type.
 *
 * @param <V> type to convert each selected node to for validation
 */
public abstract class AbstractXPathValidationStage<V> extends BaseStage<Element> {

    /** The list of validators to apply. */
    @Nonnull
    private List<Validator<V>> validators = Collections.emptyList();
    
    /** The XPath expression to execute on each {@link DOMElementItem}. */
    private String xpathExpression;

    /** The {@link NamespaceContext} to use in interpreting the XPath expression. */
    private NamespaceContext namespaceContext = new SimpleNamespaceContext();

    /**
     * Set the list of validators to apply to each item.
     * 
     * @param newValidators the list of validators to set
     */
    public void setValidators(@Nonnull final List<Validator<V>> newValidators) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        validators = ImmutableList.copyOf(Iterables.filter(newValidators, Predicates.notNull()));
    }
    
    /**
     * Gets the list of validators being applied to each item.
     * 
     * @return list of validators
     */
    @Nonnull
    public List<Validator<V>> getValidators() {
        return Collections.unmodifiableList(validators);
    }

    /**
     * Gets the XPath expression to execute on each {@link DOMElementItem}.
     * 
     * @return XPath expression to execute on each {@link DOMElementItem}
     */
    @Nullable public String getXPathExpression() {
        return xpathExpression;
    }

    /**
     * Sets the XPath expression to execute on each {@link DOMElementItem}.
     * 
     * @param expression XPath expression to execute on each {@link DOMElementItem}
     */
    public synchronized void setXPathExpression(@Nonnull @NotEmpty final String expression) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        xpathExpression =
                Constraint.isNotNull(StringSupport.trimOrNull(expression), "XPath expression can not be null or empty");
    }

    /**
     * Gets the {@link NamespaceContext} to use in interpreting the XPath expression.
     * 
     * @return {@link NamespaceContext} to use in interpreting the XPath expression
     */
    @Nonnull public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    /**
     * Sets the {@link NamespaceContext} to use in interpreting the XPath expression.
     * 
     * @param context {@link NamespaceContext} to use in interpreting the XPath expression
     */
    public synchronized void setNamespaceContext(@Nullable final NamespaceContext context) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        if (context == null) {
            namespaceContext = new SimpleNamespaceContext();
        } else {
            namespaceContext = context;
        }
    }

    /**
     * Apply each of the configured validators in turn to the provided object.
     * 
     * @param obj object to be validated
     * @param item current item containing the object being validated 
     * @throws StageProcessingException if errors occur during processing
     */
    protected void applyValidators(@Nonnull final V obj, @Nonnull final Item<Element> item)
            throws StageProcessingException {
        for (final Validator<V> validator: validators) {
            final Validator.Action action = validator.validate(obj, item, getId());
            if (action == Validator.Action.DONE) {
                return;
            }
        }
    }
    
    /**
     * Convert the visited {@link Node} to the type to be validated.
     *
     * @param node {@link Node} being validated
     * @return converted value
     */
    protected abstract V convert(@Nonnull final Node node);

    @Override
    public void doExecute(@Nonnull @NonnullElements final Collection<Item<Element>> metadataCollection)
            throws StageProcessingException {
        final XPathFactory factory = XPathFactory.newInstance();
        final XPath xpath = factory.newXPath();
        if (namespaceContext != null) {
            xpath.setNamespaceContext(namespaceContext);
        }

        final XPathExpression compiledExpression;
        try {
            compiledExpression = xpath.compile(xpathExpression);
        } catch (final XPathExpressionException e) {
            throw new StageProcessingException("error compiling XPath expression", e);
        }

        for (final Item<Element> item : metadataCollection) {
            try {
                final NodeList nodes = (NodeList)compiledExpression.evaluate(item.unwrap(), XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                   final Node node = nodes.item(i);
                   final V value = convert(node);
                   applyValidators(value, item);
                }
            } catch (final XPathExpressionException e) {
                throw new StageProcessingException("XPath expression error", e);
            }
        }
    }

    @Override
    protected void doDestroy() {
        validators = null;
        xpathExpression = null;
        namespaceContext = null;
        super.doDestroy();
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (xpathExpression == null) {
            throw new ComponentInitializationException("XPath expression can not be null or empty");
        }

        for (final Validator<V> validator : validators) {
            if (!validator.isInitialized()) {
                validator.initialize();
            }
        }
    }

}

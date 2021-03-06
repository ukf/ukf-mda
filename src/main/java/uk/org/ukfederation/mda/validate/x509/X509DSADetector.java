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

package uk.org.ukfederation.mda.validate.x509;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.validate.BaseValidator;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.utilities.java.support.component.ComponentSupport;

/**
 * Validator class to check that X.509 certificates do not contain DSA public keys.
 *
 * <p>The original Digital Signature Algorithm (DSA) is very weak by modern standards,
 * involving a 1024-bit key and the SHA-1 digest algorithm.</p>
 *
 * <p>By default, this validator adds an {@link net.shibboleth.metadata.ErrorStatus} to an
 * item containing a certificate wrapping a DSA public key, and returns
 * {@link net.shibboleth.metadata.validate.Validator.Action#DONE}
 * on the basis that further processing of the certificate is unlikely to be desired.</p>
 *
 * <p>The {@link #error} property may be set to <code>false</code> to downgrade the
 * {@link net.shibboleth.metadata.ErrorStatus} to a {@link net.shibboleth.metadata.WarningStatus}.</p>
 *
 * <p>The {@link #action} property may be set to
 * {@link net.shibboleth.metadata.validate.Validator.Action#CONTINUE} if there is a need to
 * perform additional validation on a DSA certificate.</p>
 */
@ThreadSafe
public class X509DSADetector extends BaseValidator implements Validator<X509Certificate> {

    /**
     * {@link net.shibboleth.metadata.validate.Validator.Action} to return when a DSA key is detected. Default:
     * {@link net.shibboleth.metadata.validate.Validator.Action#DONE}.
     */
    @GuardedBy("this") @Nonnull private Action action = Action.DONE;

    /**
     * Whether an {@link net.shibboleth.metadata.ErrorStatus} should be added on failure.
     * 
     * Default: <code>true</code>.
     */
    @GuardedBy("this") private boolean error = true;

    /**
     * Returns the {@link net.shibboleth.metadata.validate.Validator.Action} to be returned if a DSA key is detected.
     *
     * @return the {@link net.shibboleth.metadata.validate.Validator.Action} to be returned
     */
    public final synchronized Action getAction() {
        return action;
    }

    /**
     * Sets the {@link net.shibboleth.metadata.validate.Validator.Action} to be returned if a DSA key is detected.
     *
     * @param newAction the {@link net.shibboleth.metadata.validate.Validator.Action} to be returned
     */
    public final synchronized void setAction(@Nonnull final Action newAction) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        action = newAction;
    }

    /**
     * Set whether an {@link net.shibboleth.metadata.ErrorStatus} should be added on failure.
     * 
     * @param newValue whether an {@link net.shibboleth.metadata.ErrorStatus} should be added on failure
     */
    public final synchronized void setError(final boolean newValue) {
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        error = newValue;
    }
    
    /**
     * Returns whether an {@link net.shibboleth.metadata.ErrorStatus} is being added on failure.
     * 
     * @return <code>true</code> if an {@link net.shibboleth.metadata.ErrorStatus} is being added on failure.
     */
    public final synchronized boolean isError() {
        return error;
    }

    @Override
    public Action validate(@Nonnull final X509Certificate cert, @Nonnull final Item<?> item,
            @Nonnull final String stageId) {
        final PublicKey key = cert.getPublicKey();
        if ("DSA".equals(key.getAlgorithm())) {
            addStatus(isError(), "certificate contains a DSA key", item, stageId);
            return getAction();
        } else {
            return Action.CONTINUE;
        }
    }

}

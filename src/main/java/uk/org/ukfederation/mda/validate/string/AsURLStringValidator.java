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

package uk.org.ukfederation.mda.validate.string;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nonnull;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import uk.org.iay.incommon.mda.validate.BaseAsValidator;

/**
 * A <code>Validator</code> that checks {@link String} values as URLs by converting the
 * value to an {@link URL} and applying a sequence of validators to that value.
 *
 * This validator fails (and returns {@link net.shibboleth.metadata.validate.Validator.Action#DONE}) if the
 * value can not be converted to a {@link URL}.
 *
 * Otherwise, the validator applies the sequence of validators to the {@link URL} and returns
 * the value of that sequence.
 */
public class AsURLStringValidator extends BaseAsValidator<String, URL> {


    /**
     * Constructor.
     *
     * Sets the default message to reflect the error that comes out of the constructor for {@link URL}.
     */
    public AsURLStringValidator() {
        super.setMessage("%s");
    }

    @Override
    protected URL convert(@Nonnull final String from) throws IllegalArgumentException {
        try {
            return new URL(from);
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Action validate(@Nonnull final String t, @Nonnull final Item<?> item, @Nonnull final String stageId)
            throws StageProcessingException {
        try {
            final URL v = convert(t);
            return applyValidators(v, item);
        } catch (final IllegalArgumentException e) {
            if (isConversionRequired()) {
                /*
                 * TODO:
                 *
                 * We override validate here so that we can pull the message out of the MalformedURLException
                 * and present it as part of the ErrorStatus we generate. When we come to import this into the
                 * Shibboleth MDA frameworks, it would make sense to add this functionality somewhere higher up
                 * in the hierarchy so that this wasn't necessary.
                 */
                if (e.getCause() instanceof MalformedURLException) {
                    /*
                     * We have simply wrapped a MalformedURLException, use its message
                     * instead of the value. Such messages already include the malformed
                     * value.
                     */
                    final MalformedURLException m = (MalformedURLException)(e.getCause());
                    addErrorMessage(m.getMessage(), item, stageId);
                } else {
                    addErrorMessage(t, item, stageId);
                }
                return Action.DONE;
            } else {
                return Action.CONTINUE;
            }
        }
    }

}

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.validate.BaseValidator;
import net.shibboleth.metadata.validate.Validator;

/**
 * A {@link Validator} that checks a {@link String} representing an e-mail address.
 *
 * This is a direct replacement for the <code>dodgyAddress</code> Xalan extension method
 * from the sdss-xalan-md project, and uses the same underlying implementation.
 */
public class EmailAddressStringValidator extends BaseValidator implements Validator<String> {

    /**
     * Pattern for valid e-mail addresses.
     * 
     * This is a simplified version of the address forms permitted by RFC2822.
     * 
     * addr-spec      = local-part "@" domain
     * local-part     = dot-atom
     * dot-atom       = dot-atom-text
     * dot-atom-text  = 1*atext *("." 1*atext)
     * atext          = ALPHA / DIGIT /
     *                  "&amp;" / "'" /
     *                  "+" /
     *                  "-" /
     *                  "_"
     *                  
     * Quite a few legal options are currently missing here. The full RFC 2822
     * grammar for atext is:
     * 
     * atext           = ALPHA / DIGIT / ; Any character except controls,
     *                   "!" / "#" /     ;  SP, and specials.
     *                   "$" / "%" /     ;  Used for atoms
     *                   "&amp;" / "'" /
     *                   "*" / "+" /
     *                   "-" / "/" /
     *                   "=" / "?" /
     *                   "^" / "_" /
     *                   "`" / "{" /
     *                   "|" / "}" /
     *                   "~"
     * 
     * Note that the UK federation metadata convention includes an
     * explicit "mailto:" scheme.
     */
    private static Pattern eMailPattern = Pattern.compile(
        "^mailto:[a-z0-9&'+\\-_]+(\\.[a-z0-9&+'\\-_]+)*\\@([0-9a-z\\-_]+\\.)+[a-z]+$",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Indicates whether an e-mail address looks dodgy, i.e., has the wrong pattern.
     * 
     * @param eMail e-mail address to check
     * @return <code>true</code> if the e-mail address does not match the pattern
     */
    public static boolean dodgyAddress(final String eMail) {
        final Matcher m = eMailPattern.matcher(eMail);
        return !m.matches();
    }

    @Override
    public Action validate(final String e, final Item<?> item, final String stageId) throws StageProcessingException {
        if (dodgyAddress(e)) {
            addError("badly formatted e-mail address: '" + e + "'", item, stageId);
            return Action.DONE;
        } else {
            return Action.CONTINUE;
        }
    }
    
}

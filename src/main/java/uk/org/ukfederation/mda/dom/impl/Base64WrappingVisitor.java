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

package uk.org.ukfederation.mda.dom.impl;

import javax.annotation.Nonnull;

import org.w3c.dom.Element;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.ElementVisitor;

/**
 * {@link Element} visitor which wraps the visited element's text content.
 */
public class Base64WrappingVisitor implements ElementVisitor {

    @Override
    public void visitElement(@Nonnull final Element visited, @Nonnull final Item<Element> item) {
        final String originalText = visited.getTextContent();
        final String newText = "\n" + wrapBase64(originalText) + "\n";
        visited.setTextContent(newText);
    }
    
    /**
     * The argument string is the base-64 encoding of something. Normalise this
     * so that it doesn't have white space in peculiar places, then break it into
     * lines of 64 characters each.
     * 
     * @param s base-64 encoded string
     * @return normalised string with line breaks
     */
    @Nonnull
    public static String wrapBase64(@Nonnull final String s) {

        /* remove all white space */
        final String clean = s.replaceAll("\\s*", "");

        final StringBuilder result = new StringBuilder();
        final StringBuilder line = new StringBuilder();
        for (final char c : clean.toCharArray()) {
            if (line.length() == 64) {
                if (result.length() != 0) {
                    result.append('\n');
                }
                result.append(line);
                line.setLength(0);
            }
            line.append(c);
        }
        if (line.length() != 0) {
            if (result.length() != 0) {
                result.append('\n');
            }
            result.append(line);
        }
        return result.toString();
    }
}

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

package uk.org.ukfederation.mda.validate;

import javax.annotation.concurrent.ThreadSafe;

/** Support class for dealing with the XML DSIG specification. */
@ThreadSafe
public final class XMLDSIGSupport {

    /** Namespace URI for the XML DSIG specification. */
    public static final String XML_DSIG_NS = "http://www.w3.org/2000/09/xmldsig#";

    /** Constructor. */
    private XMLDSIGSupport() {
    }

}

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

package uk.org.ukfederation.mda.dom.saml.ukfedlabel;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

/** Helper class for dealing with the uk-fed-label namespace. */
@ThreadSafe
public final class UKFedLabelSupport {

    /** UKFedLabel namespace. */
    public static final String UKFEDLABEL_NS = "http://ukfederation.org.uk/2006/11/label";

    /** UKFedLabel conventional prefix. */
    public static final String UKFEDLABEL_PREFIX = "ukfedlabel";

    /** ukfedlabel:UKFederationMember element. */
    public static final QName UK_FEDERATION_MEMBER_NAME =
            new QName(UKFEDLABEL_NS, "UKFederationMember", UKFEDLABEL_PREFIX);

    /** orgID attribute on ukfedlabel:UKFederationMember element. */
    public static final QName UK_FEDERATION_MEMBER_ORGID =
            new QName("orgID");

    /** Constructor. */
    private UKFedLabelSupport() {
    }

}

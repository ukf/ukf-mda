/*
 * Copyright (C) 2014 University of Edinburgh.
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

package uk.org.ukfederation.mda.dom.saml.mdattr;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Helper class for dealing with entity categories.
 * 
 * @see https://datatracker.ietf.org/doc/draft-young-entity-category/
 */
@ThreadSafe
public final class EntityCategorySupport {

    /** The attribute <code>NameFormat</code> for all entity category attributes. */
    public static final String EC_ATTR_NAME_FORMAT = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
    
    /** The attribute <code>Name</code> for the Entity Category Attribute. */
    public static final String EC_CATEGORY_ATTR_NAME = "http://macedir.org/entity-category";
    
    /** The attribute <code>Name</code> for the Entity Category Support Attribute. */
    public static final String EC_SUPPORT_ATTR_NAME = "http://macedir.org/entity-category-support";

    /** Constructor. */
    private EntityCategorySupport() {
    }

}

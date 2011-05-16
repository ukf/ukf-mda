/*
 * Copyright (C) 2011 University of Edinburgh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.shibboleth.metadata.dom.DomMetadata;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/** {@link XPathFilteringStage} unit test. */
public class XPathFilteringStageTest extends BaseDomTest {

	@Test
	public void testExecute() throws Exception {
		// Construct a map containing required namespace prefix definitions
		Map<String, String> prefixMappings = new HashMap<String, String>();
		prefixMappings.put("ukfedlabel", "http://ukfederation.org.uk/2006/11/label");
		
		// Construct the strategy object
		XPathFilteringStage strategy =
			new XPathFilteringStage("//ukfedlabel:DeletedEntity", prefixMappings);
		
		// Construct the input metadata
		ArrayList<DomMetadata> metadataCollection = new ArrayList<DomMetadata>();
        metadataCollection.add(new DomMetadata(readXmlData("xpathInput1.xml")));
        metadataCollection.add(new DomMetadata(readXmlData("xpathInput2.xml")));
        metadataCollection.add(new DomMetadata(readXmlData("xpathInput3.xml")));
		Assert.assertEquals(metadataCollection.size(), 3);
		
		// Filter the metadata collection
		strategy.doExecute(metadataCollection);
		Assert.assertEquals(metadataCollection.size(), 1);
		Element element = metadataCollection.get(0).getMetadata();
		String id = element.getAttribute("id");
		Assert.assertEquals(id, "entity2");
	}
	
}

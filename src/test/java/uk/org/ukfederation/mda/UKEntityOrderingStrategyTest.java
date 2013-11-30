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

package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.List;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemId;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/** Unit tests for the {@link UKEntityOrderingStrategy} class. */
public class UKEntityOrderingStrategyTest extends BaseDOMTest {
    
    /** Constructor sets class under test. */
    public UKEntityOrderingStrategyTest() {
        super(UKEntityOrderingStrategy.class);
    }

    /**
     * Test the ordering strategy.
     * 
     * @throws XMLParserException 
     */
    @Test
    public void testOrder() throws XMLParserException {
        // Create a trivial DOM Document as a placeholder
        final Element trivialDoc = readXmlData("trivial.xml");
        
        // Create some items in the order they will end up
        
        final DOMElementItem i00 = new DOMElementItem(readXmlData("named.xml"));
        
        final DOMElementItem i01 = new DOMElementItem(readXmlData("unnamed.xml"));
        
        final DOMElementItem i0 = new DOMElementItem(trivialDoc);
        i0.getItemMetadata().put(new UKId("uk000000"));
        
        final DOMElementItem i1 = new DOMElementItem(trivialDoc);
        i1.getItemMetadata().put(new UKId("uk000005"));
        i1.getItemMetadata().put(new ItemId("this has no effect"));
        
        final DOMElementItem i2 = new DOMElementItem(trivialDoc);
        i2.getItemMetadata().put(new UKId("uk123456"));
        
        final DOMElementItem i3 = new DOMElementItem(trivialDoc);
        i3.getItemMetadata().put(new ItemId("https://example.com/path0"));
        
        final DOMElementItem i4 = new DOMElementItem(trivialDoc);
        i3.getItemMetadata().put(new ItemId("https://example.com/path1"));
        
        final DOMElementItem i5 = new DOMElementItem(trivialDoc);
        // nothing at all on i5
        
        // Make a collection containing those items in an arbitrary order
        final List<Item<Element>> items = new ArrayList<>();
        items.add(i4);
        items.add(i3);
        items.add(i1);
        items.add(i0);
        items.add(i00);
        items.add(i5);
        items.add(i01);
        items.add(i2);
        
        // Order the collection
        final UKEntityOrderingStrategy strat = new UKEntityOrderingStrategy();
        final List<Item<Element>>items2 = strat.order(items);
        
        // Check that everything is in the right place afterwards
        Assert.assertEquals(items2.get(0), i00);
        Assert.assertEquals(items2.get(1), i01);
        Assert.assertEquals(items2.get(2), i0);
        Assert.assertEquals(items2.get(3), i1);
        Assert.assertEquals(items2.get(4), i2);
        Assert.assertEquals(items2.get(5), i3);
        Assert.assertEquals(items2.get(6), i4);
        Assert.assertEquals(items2.get(7), i5);
    }

}

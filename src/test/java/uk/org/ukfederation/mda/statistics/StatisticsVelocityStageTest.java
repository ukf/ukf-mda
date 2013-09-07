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

package uk.org.ukfederation.mda.statistics;

import java.util.ArrayList;
import java.util.Collection;

import net.shibboleth.metadata.dom.DomElementItem;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import uk.org.ukfederation.mda.BaseDomTest;

/** Unit tests for the StatisticsVelocityStage class. */
public class StatisticsVelocityStageTest extends BaseDomTest {

    /**
     * Simple "hello, world" test.
     * 
     * @throws Exception if anything goes wrong.
     */
    @Test
    public void testHello() throws Exception {
        final StatisticsVelocityStage stage = new StatisticsVelocityStage();
        stage.setId("test");
        stage.setTemplateName("/templates/hello.vm");
        stage.setParserPool(parserPool);
        stage.initialize();

        final Collection<DomElementItem> items = new ArrayList<>();
        stage.execute(items);
        Assert.assertEquals(items.size(), 1);
        final Element e = items.iterator().next().unwrap();
        Assert.assertEquals("hello", e.getLocalName());
    }
}

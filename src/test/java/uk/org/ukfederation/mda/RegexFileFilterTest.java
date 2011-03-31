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

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link RegexFileFilter} unit tests. */
public class RegexFileFilterTest {

	/**
	 * Test acceptance of files corresponding to the UK federation use case.
	 */
	@Test
	public void testAccept() {
		RegexFileFilter a = new RegexFileFilter("uk\\d{6}\\.xml");
		Assert.assertTrue(a.accept(new File("uk123456.xml")));
		Assert.assertTrue(a.accept(new File("foo/uk123456.xml")));
		Assert.assertFalse(a.accept(new File("imported.xml")));
		Assert.assertFalse(a.accept(new File("uk123456.new")));
		Assert.assertFalse(a.accept(new File("uk123456.xml~")));
		Assert.assertFalse(a.accept(new File("x-123456.xml")));
		Assert.assertFalse(a.accept(new File("foo/baruk123456.xml")));
		Assert.assertFalse(a.accept(new File("uk123456.xml/bar")));
	}
  
}

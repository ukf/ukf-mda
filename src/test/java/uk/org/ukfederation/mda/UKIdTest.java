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

import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit tests for the {@link UKId} class. */
public class UKIdTest {
    
    /** Basic tests. */
    @Test
    public void test() {
        UKId info = new UKId(" test ");
        assert "test".equals(info.getId());

        try {
            info = new UKId("");
            throw new AssertionError();
        } catch (IllegalArgumentException e) {
            // expected this
        }

        try {
            info = new UKId(null);
            throw new AssertionError();
        } catch (IllegalArgumentException e) {
            // expected this
        }
    }
    
    /**
     * Test the implementation of the <code>Comparable</code> interface.
     */
    @Test
    public void testCompareTo() {
        UKId one = new UKId("one");
        UKId two = new UKId("two");
        UKId twoAgain = new UKId("two");
        
        Assert.assertTrue(two.compareTo(two) == 0);
        Assert.assertTrue(two.compareTo(twoAgain) == 0);
        Assert.assertTrue(one.compareTo(two) < 0);
        Assert.assertTrue(two.compareTo(one) > 0);
    }
    
    /**
     * Test that the hash codes for different {@link UKId}s are different.
     * Impossible to test for sure, because of course the strings chosen
     * have a very very low chance have the same hashCode.
     */
    @Test
    public void testHashCode() {
        UKId one = new UKId("one");
        UKId two = new UKId("two");
        Assert.assertFalse(one.hashCode() == two.hashCode());
    }

}

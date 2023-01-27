package uk.org.ukfederation.mda;

import net.shibboleth.shared.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit tests for the {@link UKId} class. */
public class UKIdTest {
    
    /** Basic tests. */
    @Test
    public void test() {
        final UKId info = new UKId(" test ");
        assert "test".equals(info.getId());

        try {
            new UKId("");
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }

        try {
            new UKId(null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }
    }
    
    /**
     * Test the implementation of the <code>Comparable</code> interface.
     */
    @Test
    public void testCompareTo() {
        final UKId one = new UKId("one");
        final UKId two = new UKId("two");
        final UKId twoAgain = new UKId("two");
        
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
        final UKId one = new UKId("one");
        final UKId two = new UKId("two");
        Assert.assertFalse(one.hashCode() == two.hashCode());
    }

}

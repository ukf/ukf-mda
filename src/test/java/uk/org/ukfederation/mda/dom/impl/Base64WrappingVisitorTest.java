package uk.org.ukfederation.mda.dom.impl;

import javax.annotation.Nonnull;

import org.testng.Assert;
import org.testng.annotations.Test;

import uk.ac.sdss.xalan.md.TextUtils;

public class Base64WrappingVisitorTest {

    final String ten = "1234567890";
    final String sixty = ten + ten + ten + ten + ten + ten;
    final String sixtyFour = sixty + "1234";

    /*
     * Make sure that the innermost wrapping function matches the original from the
     * sdss-xalan-md project.
     *
     * This allows us to reimplement without worrying about deviating from the original.
     */

    private void testCase(@Nonnull final String test, @Nonnull final String expected) {
        Assert.assertEquals(TextUtils.wrapBase64(test), expected, "old algorithm fails");
        Assert.assertEquals(Base64WrappingVisitor.wrapBase64(test), expected, "new algorithm fails");
    }

    @Test
    public void testEmpty() {
        testCase("", "");
        testCase("   \n\n   ", "");
    }

    @Test
    public void testShort() {
        testCase(" ab cd ", "abcd");
        testCase(ten + "    " + ten, ten + ten);
        testCase(sixty, sixty);
        testCase(sixtyFour, sixtyFour);
    }

    @Test
    public void testLong() {
        testCase(sixtyFour + sixtyFour,
                sixtyFour + "\n" + sixtyFour);
        testCase(sixtyFour + sixtyFour + "wibble",
                sixtyFour + "\n" + sixtyFour +"\n" + "wibble");
    }
}

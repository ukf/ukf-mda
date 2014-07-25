
package uk.org.ukfederation.mda.dom.saml.mdattr;

import junit.framework.Assert;

import org.testng.annotations.Test;

public class SimpleEntityAttributeContextTest {

    @Test
    public void testFour() {
        final EntityAttributeContext ctx = new SimpleEntityAttributeContext("a", "b", "c", "d");
        Assert.assertEquals("a", ctx.getValue());
        Assert.assertEquals("b", ctx.getName());
        Assert.assertEquals("c", ctx.getNameFormat());
        Assert.assertEquals("d", ctx.getRegistrationAuthority());
    }
    
    @Test
    public void testThree() {
        final EntityAttributeContext ctx = new SimpleEntityAttributeContext("a", "b", "c");
        Assert.assertEquals("a", ctx.getValue());
        Assert.assertEquals("b", ctx.getName());
        Assert.assertEquals("c", ctx.getNameFormat());
        Assert.assertNull(ctx.getRegistrationAuthority());
    }

    @Test
    public void stringFour() {
        final EntityAttributeContext ctx = new SimpleEntityAttributeContext("a", "b", "c", "d");
        Assert.assertEquals("{v=a, n=b, f=c, r=d}", ctx.toString());
    }

    @Test
    public void stringThree() {
        final EntityAttributeContext ctx = new SimpleEntityAttributeContext("a", "b", "c");
        Assert.assertEquals("{v=a, n=b, f=c, r=(none)}", ctx.toString());
    }
    
}

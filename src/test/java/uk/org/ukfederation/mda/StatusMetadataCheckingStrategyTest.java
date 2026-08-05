
package uk.org.ukfederation.mda;

import net.shibboleth.metadata.*;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.shared.xml.XMLParserException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;


public class StatusMetadataCheckingStrategyTest extends BaseDOMTest {

    /** Default invalid code */
    static private final String MESSAGE_CODE = "strip-invalid-sirtfi";

    /** Constructor sets class under test. */
    public StatusMetadataCheckingStrategyTest() {
        super(StatusMetadataCheckingStrategy.class);
    }


    /**
     * Test the true case with
     * 1) Warning
     * 2) Correct message code
     * 2) Exact Match = true
     * 3) isRemove = false (default)
     *
     */
    @Test
    public void testTrueWithWarningAndCorrectMessageCodeAndExactMatch() throws XMLParserException {
        Assert.assertTrue(internalTest(WarningStatus.class, WarningStatus.class, MESSAGE_CODE, true));
    }

    /**
     * Test the true case with
     * 1) Error
     * 2) Correct message code
     * 2) Exact Match = true
     * 3) isRemove = false (default)
     *
     */
    @Test
    public void testTrueWithErrorAndCorrectMessageCodeAndExactMatch() throws XMLParserException {
        Assert.assertTrue(internalTest(ErrorStatus.class, ErrorStatus.class, MESSAGE_CODE, true));
    }

    /**
     * Test the true case with
     * 1) Info
     * 2) Correct message code
     * 2) Exact Match = true
     * 3) isRemove = false (default)
     *
     */
    @Test
    public void testTrueWithInfoAndCorrectMessageCodeAndExactMatch() throws XMLParserException {
        Assert.assertTrue(internalTest(InfoStatus.class, InfoStatus.class, MESSAGE_CODE, true));
    }


    /**
     * Test the false case with
     * 1) Error
     * 2) Correct message code
     * 2) Exact Match = true
     * 3) isRemove = false (default)
     *
     */
    @Test
    public void testFalseWithWrongStatusTypeAndCorrectMessageCodeAndExactMatch() throws XMLParserException {
        Assert.assertFalse(internalTest(InfoStatus.class, ErrorStatus.class, MESSAGE_CODE, true));
    }

    /**
     * Test the false case with
     * 1) Warning
     * 2) Incorrect message code
     * 2) Exact Match = true
     * 3) isRemove = false (default)
     *
     */
    @Test
    public void testFalseWithWarningAndIncorrectMessageCodeAndExactMatch() throws XMLParserException {
        Assert.assertFalse(internalTest(WarningStatus.class, WarningStatus.class, "WRONG" + MESSAGE_CODE, true));
    }

    /**
     * Test the false case with
     * 1) Warning
     * 2) Correct message code
     * 2) Exact Match = false
     * 3) isRemove = false (default)
     *
     */
    @Test
    public void testTrueWithWarningAndCorrectMessageCodeAndNotExactMatch() throws XMLParserException {
        Assert.assertTrue(internalTest(WarningStatus.class, WarningStatus.class, MESSAGE_CODE, false));
    }

    /**
     * Test the false case with
     * 1) Warning
     * 2) Incorrect message code
     * 2) Exact Match = false
     * 3) isRemove = false (default)
     *
     */
    @Test
    public void testFalseWithWarningAndCorrectMessageCodeAndNotExactMatch() throws XMLParserException {
        Assert.assertFalse(internalTest(WarningStatus.class, WarningStatus.class, "WRONG" + MESSAGE_CODE, false));
    }

    /**
     * Internal generalized test
     *
     * @param actualStatusType
     *  Type of the actual message status [Info, Warning, Error]
     * @param expectedStatusType
     *  Type of the expected message status [Info, Warning, Error]
     * @param expectedMessage
     *  Expected message to be matched
     * @param exactMatch
     *  True for exact match, false for contain only
     * @return
     *  True if matched
     * @throws XMLParserException
     */
    private boolean internalTest(final Class<? extends StatusMetadata> actualStatusType, final Class<? extends StatusMetadata> expectedStatusType,
                                 final String expectedMessage, final boolean exactMatch) throws XMLParserException {
        // Create a DOM Document with status meta data
        final Item<Element> dom = new DOMElementItem(readXMLData("sirtfi.xml"));
        if (InfoStatus.class.equals(actualStatusType)) {
            dom.getItemMetadata().put(new InfoStatus("id", MESSAGE_CODE));
        } else if (WarningStatus.class.equals(actualStatusType)) {
            dom.getItemMetadata().put(new WarningStatus("id", MESSAGE_CODE));
        } else if (ErrorStatus.class.equals(actualStatusType)) {
            dom.getItemMetadata().put(new ErrorStatus("id", MESSAGE_CODE));
        }

        // Test
        final StatusMetadataCheckingStrategy strat = new StatusMetadataCheckingStrategy(expectedStatusType, expectedMessage, exactMatch);
        final boolean result = strat.test(dom);

        return result;
    }
    
}

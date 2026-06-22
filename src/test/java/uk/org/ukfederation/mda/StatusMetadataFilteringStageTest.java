
package uk.org.ukfederation.mda;

import net.shibboleth.metadata.*;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.XMLParserException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class StatusMetadataFilteringStageTest extends BaseDOMTest {

    /** Default invalid code */
    static private final String MESSAGE_CODE = "strip-invalid-sirtfi";

    /** Constructor sets class under test. */
    public StatusMetadataFilteringStageTest() {
        super(StatusMetadataFilteringStage.class);
    }


    /**
     * Test single remove
     */
    @Test
    public void testRemoveFunctionSingle() throws XMLParserException, StageProcessingException, ComponentInitializationException {
        final List<StatusMetadata> statusMetadataList = new ArrayList<>();
        statusMetadataList.add(new WarningStatus("id", MESSAGE_CODE));
        final Class<? extends StatusMetadata> targetStatusType = WarningStatus.class;
        internalTest(true, statusMetadataList, targetStatusType, 0);
    }


    /**
     * Test single remove with other warning message
     */
    @Test
    public void testRemoveFunctionSingleWithOtherWarningMessage() throws XMLParserException, StageProcessingException, ComponentInitializationException {
        final List<StatusMetadata> statusMetadataList = new ArrayList<>();
        statusMetadataList.add(new WarningStatus("id", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id2", "ANOTHER WARNING"));
        final Class<? extends StatusMetadata> targetStatusType = WarningStatus.class;
        internalTest(true, statusMetadataList, targetStatusType, 1);
    }

    /**
     * Test multiple remove
     */
    @Test
    public void testRemoveFunctionMultiple() throws XMLParserException, StageProcessingException, ComponentInitializationException {
        final List<StatusMetadata> statusMetadataList = new ArrayList<>();
        statusMetadataList.add(new WarningStatus("id", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id2", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id3", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id4", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id5", MESSAGE_CODE));
        final Class<? extends StatusMetadata> targetStatusType = WarningStatus.class;
        internalTest(true, statusMetadataList, targetStatusType, 0);
    }

    /**
     * Test multiple remove of Error Type
     */
    @Test
    public void testRemoveFunctionMultipleError() throws XMLParserException, StageProcessingException, ComponentInitializationException {
        final List<StatusMetadata> statusMetadataList = new ArrayList<>();
        statusMetadataList.add(new ErrorStatus("id", MESSAGE_CODE));
        statusMetadataList.add(new ErrorStatus("id2", MESSAGE_CODE));
        statusMetadataList.add(new ErrorStatus("id3", MESSAGE_CODE));
        statusMetadataList.add(new ErrorStatus("id4", MESSAGE_CODE));
        statusMetadataList.add(new ErrorStatus("id5", MESSAGE_CODE));
        final Class<? extends StatusMetadata> targetStatusType = ErrorStatus.class;
        internalTest(true, statusMetadataList, targetStatusType, 0);
    }

    /**
     * Test multiple remove of Info Type
     */
    @Test
    public void testRemoveFunctionMultipleInfo() throws XMLParserException, StageProcessingException, ComponentInitializationException {
        final List<StatusMetadata> statusMetadataList = new ArrayList<>();
        statusMetadataList.add(new InfoStatus("id", MESSAGE_CODE));
        statusMetadataList.add(new InfoStatus("id2", MESSAGE_CODE));
        statusMetadataList.add(new InfoStatus("id3", MESSAGE_CODE));
        statusMetadataList.add(new InfoStatus("id4", MESSAGE_CODE));
        statusMetadataList.add(new InfoStatus("id5", MESSAGE_CODE));
        final Class<? extends StatusMetadata> targetStatusType = InfoStatus.class;
        internalTest(true, statusMetadataList, targetStatusType, 0);
    }



    /**
     * Test multiple remove with other types of messages
     */
    @Test
    public void testRemoveFunctionMultipleWithOtherWarningMessage() throws XMLParserException, StageProcessingException, ComponentInitializationException {

        final List<StatusMetadata> statusMetadataList = new ArrayList<>();
        statusMetadataList.add(new WarningStatus("id", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id2", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id3", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id4", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id5", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id6", "ANOTHER WARNING"));
        statusMetadataList.add(new WarningStatus("id7", "ANOTHER WARNING1"));
        statusMetadataList.add(new InfoStatus("id8", "ANOTHER WARNING2"));
        statusMetadataList.add(new ErrorStatus("id9", "ANOTHER WARNING3"));
        final Class<? extends StatusMetadata> targetStatusType = WarningStatus.class;
        internalTest(true, statusMetadataList, targetStatusType, 2);
    }

    /**
     * Test multiple remove with other types of messages, not exact match
     */
    @Test
    public void testRemoveFunctionMultipleWithOtherWarningMessageNotExactMatch() throws XMLParserException, StageProcessingException, ComponentInitializationException {

        final List<StatusMetadata> statusMetadataList = new ArrayList<>();
        statusMetadataList.add(new WarningStatus("id", MESSAGE_CODE + "post"));
        statusMetadataList.add(new WarningStatus("id2", "pre" + MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id3", "pre" + MESSAGE_CODE + "post"));
        statusMetadataList.add(new WarningStatus("id4", "strip-invalid-TEST-sirtfi"));
        statusMetadataList.add(new WarningStatus("id5", MESSAGE_CODE));
        statusMetadataList.add(new WarningStatus("id6", "ANOTHER WARNING"));
        statusMetadataList.add(new WarningStatus("id7", "ANOTHER WARNING1"));
        statusMetadataList.add(new InfoStatus("id8", "ANOTHER WARNING2"));
        statusMetadataList.add(new ErrorStatus("id9", "ANOTHER WARNING3"));
        final Class<? extends StatusMetadata> targetStatusType = WarningStatus.class;
        internalTest(false, statusMetadataList, targetStatusType, 3);
    }



    /**
     * Executes a parameterized test of {@link StatusMetadataFilteringStage}.
     *
     * <p>A {@link DOMElementItem} is created and populated with the supplied
     * {@link StatusMetadata} instances. The configured filter stage is then
     * executed against the item, and the remaining number of matching
     * {@link StatusMetadata} entries is verified.</p>
     *
     * @param exactMatch whether message matching should be performed using exact
     *                   equality ({@code true}) or substring matching ({@code false})
     * @param statusMetadataList the status metadata entries to attach to the test item
     * @param targetStatusType the type of {@link StatusMetadata} to be processed
     *                         by the filter stage
     * @param result the expected number of {@link StatusMetadata} entries of the
     *               specified type remaining after execution
     *
     * @throws XMLParserException if the test metadata document cannot be parsed
     * @throws StageProcessingException if stage execution fails
     * @throws ComponentInitializationException if the stage cannot be initialized
     */
    private void internalTest(final boolean exactMatch, final List<StatusMetadata> statusMetadataList,
                             final Class<? extends StatusMetadata> targetStatusType, final int result) throws XMLParserException, StageProcessingException, ComponentInitializationException {
        // Create a DOM Document with status metadata
        final DOMElementItem dom = new DOMElementItem(readXMLData("sirtfi.xml"));
        for (final StatusMetadata statusMetadata: statusMetadataList) {
            dom.getItemMetadata().put(statusMetadata);
        }
        // Put the DOM to a List<Item<Element>> for Filter Stage to check
        final List<Item<Element>> items = new ArrayList<>();
        items.add(dom);

        // Test
        final StatusMetadataFilteringStage<Element> stage = new StatusMetadataFilteringStage<>();
        stage.setMessage(MESSAGE_CODE);
        stage.setExactMatch(exactMatch);
        stage.setSelectionRequirements(CollectionSupport.setOf(targetStatusType));
        stage.setId("test");
        stage.initialize();
        stage.execute(items);

        Assert.assertEquals(dom.getItemMetadata().get(targetStatusType).size(), result);
    }


    @Test
    public void testNoInputMessage() {
        Assert.assertThrows(ComponentInitializationException.class, () -> testInputInternal(null, true).initialize());
    }

    @Test
    public void testInputNoExactMatch() throws ComponentInitializationException, XMLParserException {
        testInputInternal(MESSAGE_CODE, null).initialize();
    }

    @Test
    public void testInputNoMessageAndExactMatch() {
        Assert.assertThrows(ComponentInitializationException.class, () -> testInputInternal(null, null).initialize());
    }

    private StatusMetadataFilteringStage<Element> testInputInternal(final String message, final Boolean exactMatch) throws XMLParserException {
        final List<StatusMetadata> statusMetadataList = new ArrayList<>();
        statusMetadataList.add(new WarningStatus("id", MESSAGE_CODE));
        final Class<? extends StatusMetadata> targetStatusType = WarningStatus.class;

        // Create a DOM Document with status metadata
        final DOMElementItem dom = new DOMElementItem(readXMLData("sirtfi.xml"));
        for (final StatusMetadata statusMetadata: statusMetadataList) {
            dom.getItemMetadata().put(statusMetadata);
        }
        // Put the DOM to a List<Item<Element>> for Filter Stage to check
        final List<Item<Element>> items = new ArrayList<>();
        items.add(dom);

        // Condition
        final StatusMetadataFilteringStage<Element> stage = new StatusMetadataFilteringStage<>();
        stage.setMessage(message);
        if (exactMatch != null) {
            stage.setExactMatch(exactMatch);
        }
        stage.setSelectionRequirements(CollectionSupport.setOf(targetStatusType));
        stage.setId("test");

        return stage;
    }


}

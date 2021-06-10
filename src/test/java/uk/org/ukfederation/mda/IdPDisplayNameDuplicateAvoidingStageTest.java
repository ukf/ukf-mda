
package uk.org.ukfederation.mda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.shibboleth.metadata.InfoStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.dom.saml.SAMLMetadataSupport;
import net.shibboleth.metadata.dom.saml.mdui.MDUISupport;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;

public class IdPDisplayNameDuplicateAvoidingStageTest extends BaseDOMTest {

    protected IdPDisplayNameDuplicateAvoidingStageTest() {
        super(IdPDisplayNameDuplicateAvoidingStage.class);
    }

    private Map<String, String> makeRegMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("https://www.wayf.dk", "DK");
        map.put("http://ukfederation.org.uk", "UK");
        map.put("https://incommon.org", "US");
        return map;
    }


    /**
     * Return all elements with the given name within the given entity descriptor.
     * 
     * @param entity the {@link Element} representing the entity's <code>md:EntityDescriptor</code>
     * @param qname the {@link QName} being searched for
     * @return a {@link List} of {@link Element}s with the requested name
     */
    @Nonnull @NonnullElements
    private List<Element> extractElements(@Nonnull final Element entity,
            @Nonnull final QName qname) {
        final List<Element> names = new ArrayList<>();
        final NodeList elements = entity.getElementsByTagNameNS(qname.getNamespaceURI(), qname.getLocalPart());
        for (int n = 0; n < elements.getLength(); n++) {
            final Element element = (Element)elements.item(n);
            names.add(element);
        }
        return names;
    }

    /**
     * Return a list of {@link Element}s representing the entity's discovery names.
     * 
     * <p>This either collects the entity's <code>mdui:DisplayName</code> elements or,
     * if there are none, any <code>md:OrganizationDisplayName</code> elements it has.
     *
     * @param entity the {@link Element} representing the entity
     * @return a {@link List} of {@link Element}s, possibly empty
     */
    @Nonnull @NonnullElements private List<Element> extractDiscoveryNameElements(@Nonnull final Element entity) {
        // Look at mdui:DisplayName first
        final List<Element> mduiNames = extractElements(entity, MDUISupport.DISPLAYNAME_NAME);
        if (!mduiNames.isEmpty()) {
            return mduiNames;
        }
        
        // Otherwise, fall back to legacy md:OrganizationDisplayName elements
        return extractElements(entity, new QName(SAMLMetadataSupport.MD_NS, "OrganizationDisplayName"));
    }

    @Nonnull @NonnullElements private List<String> extractDiscoveryNames(
            @Nonnull @NonnullElements final List<Element> elements) {
        final List<String> names = new ArrayList<>();
        for (final Element element : elements) {
            names.add(element.getTextContent().trim());
        }
        return names;
    }

    @Test
    public void royalAcademyClash() throws Exception {
        final List<Item<Element>> items = readDOMItems(new String[] { "ram-uk.xml", "ram-dk-clash.xml" });
        final Item<Element> originalUKItem = items.get(0).copy();
        final Item<Element> originalDKItem = items.get(1).copy();

        final IdPDisplayNameDuplicateAvoidingStage stage = new IdPDisplayNameDuplicateAvoidingStage();
        stage.setId("test");
        stage.setRegistrationAuthorityDisplayNames(makeRegMap());
        stage.initialize();
        
        stage.execute(items);

        // UK item should be unchanged
        assertXMLEqual(originalUKItem.unwrap(), items.get(0).unwrap());

        // DK item has changed
        Assert.assertEquals(extractDiscoveryNameElements(originalDKItem.unwrap()).get(1).getTextContent(),
                "Royal Academy of Music");
        Assert.assertEquals(extractDiscoveryNameElements(items.get(1).unwrap()).get(1).getTextContent(),
                "[DK] Royal Academy of Music");
        assertXMLEqual(readXMLData("ram-dk-out.xml"), items.get(1).unwrap());

        // Look for INFO status item metadata
        final List<InfoStatus> infos = items.get(1).getItemMetadata().get(InfoStatus.class);
        Assert.assertEquals(infos.size(), 1);
        Assert.assertEquals(infos.get(0).getStatusMessage(),
                "discovery name changed to '[DK] Royal Academy of Music'");

        stage.destroy();
    }

    @Test
    public void royalAcademyNoClash() throws Exception {
        final List<Item<Element>> items = readDOMItems(new String[] { "ram-uk.xml", "ram-dk-noclash.xml" });
        final Item<Element> originalUKItem = items.get(0).copy();
        final Item<Element> originalDKItem = items.get(1).copy();

        final IdPDisplayNameDuplicateAvoidingStage stage = new IdPDisplayNameDuplicateAvoidingStage();
        stage.setId("test");
        stage.setRegistrationAuthorityDisplayNames(makeRegMap());
        stage.initialize();
        
        stage.execute(items);

        // UK item should be unchanged
        assertXMLEqual(originalUKItem.unwrap(), items.get(0).unwrap());

        // DK item is also unchanged
        Assert.assertEquals(extractDiscoveryNameElements(originalDKItem.unwrap()).get(1).getTextContent(),
                "Royal Academy of Music Aarhus/Aalborg (RAMA)");
        Assert.assertEquals(extractDiscoveryNameElements(items.get(1).unwrap()).get(1).getTextContent(),
                "Royal Academy of Music Aarhus/Aalborg (RAMA)");
        assertXMLEqual(originalDKItem.unwrap(), items.get(1).unwrap());

        stage.destroy();
    }
    
    @Test
    public void defaultRegistrarHandle() throws Exception {
        final List<Item<Element>> items = readDOMItems(new String[] { "ram-uk.xml", "ram-dk-clash.xml" });
        final Item<Element> originalUKItem = items.get(0).copy();
        final Item<Element> originalDKItem = items.get(1).copy();

        final IdPDisplayNameDuplicateAvoidingStage stage = new IdPDisplayNameDuplicateAvoidingStage();
        stage.setId("test");
        // Don't provide a registrar code mapping
        // stage.setRegistrationAuthorityDisplayNames(makeRegMap());
        stage.initialize();
        
        stage.execute(items);

        // UK item should be unchanged
        assertXMLEqual(originalUKItem.unwrap(), items.get(0).unwrap());

        // DK item has changed
        Assert.assertEquals(extractDiscoveryNameElements(originalDKItem.unwrap()).get(1).getTextContent(),
                "Royal Academy of Music");
        Assert.assertEquals(extractDiscoveryNameElements(items.get(1).unwrap()).get(1).getTextContent(),
                "[??] Royal Academy of Music");

        stage.destroy();
    }

    @Test
    public void setDefaultRegistrarHandle() throws Exception {
        final List<Item<Element>> items = readDOMItems(new String[] { "ram-uk.xml", "ram-dk-clash.xml" });
        final Item<Element> originalUKItem = items.get(0).copy();
        final Item<Element> originalDKItem = items.get(1).copy();

        final IdPDisplayNameDuplicateAvoidingStage stage = new IdPDisplayNameDuplicateAvoidingStage();
        stage.setId("test");
        // Don't provide a registrar code mapping
        // stage.setRegistrationAuthorityDisplayNames(makeRegMap());
        stage.setDefaultRegistrationAuthorityDisplayName("not-UK");
        stage.initialize();
        
        stage.execute(items);

        // UK item should be unchanged
        assertXMLEqual(originalUKItem.unwrap(), items.get(0).unwrap());

        // DK item has changed
        Assert.assertEquals(extractDiscoveryNameElements(originalDKItem.unwrap()).get(1).getTextContent(),
                "Royal Academy of Music");
        Assert.assertEquals(extractDiscoveryNameElements(items.get(1).unwrap()).get(1).getTextContent(),
                "[not-UK] Royal Academy of Music");

        stage.destroy();
    }

    @Test
    public void setNameFormat() throws Exception {
        final List<Item<Element>> items = readDOMItems(new String[] { "ram-uk.xml", "ram-dk-clash.xml" });
        final Item<Element> originalUKItem = items.get(0).copy();
        final Item<Element> originalDKItem = items.get(1).copy();

        final IdPDisplayNameDuplicateAvoidingStage stage = new IdPDisplayNameDuplicateAvoidingStage();
        stage.setId("test");
        stage.setRegistrationAuthorityDisplayNames(makeRegMap());
        stage.setNameFormat("{0} (from {1})");
        stage.initialize();
        
        stage.execute(items);

        // UK item should be unchanged
        assertXMLEqual(originalUKItem.unwrap(), items.get(0).unwrap());

        // DK item has changed
        Assert.assertEquals(extractDiscoveryNameElements(originalDKItem.unwrap()).get(1).getTextContent(),
                "Royal Academy of Music");
        Assert.assertEquals(extractDiscoveryNameElements(items.get(1).unwrap()).get(1).getTextContent(),
                "Royal Academy of Music (from DK)");

        stage.destroy();
    }

    @Test
    public void royalAcademyClashOtherFoot() throws Exception {
        final List<Item<Element>> items = readDOMItems(new String[] { "ram-uk.xml", "ram-dk-clash.xml" });
        final Item<Element> originalUKItem = items.get(0).copy();
        final Item<Element> originalDKItem = items.get(1).copy();

        final IdPDisplayNameDuplicateAvoidingStage stage = new IdPDisplayNameDuplicateAvoidingStage();
        stage.setId("test");
        stage.setRegistrationAuthority("https://www.wayf.dk");
        stage.setRegistrationAuthorityDisplayNames(makeRegMap());
        stage.initialize();
        
        stage.execute(items);

        // DK item should be unchanged
        assertXMLEqual(originalDKItem.unwrap(), items.get(1).unwrap());

        // UK item has changed
        Assert.assertEquals(extractDiscoveryNameElements(originalUKItem.unwrap()).get(0).getTextContent(),
                "Royal Academy of Music");
        Assert.assertEquals(extractDiscoveryNameElements(items.get(0).unwrap()).get(0).getTextContent(),
                "[UK] Royal Academy of Music");

        stage.destroy();
    }

}

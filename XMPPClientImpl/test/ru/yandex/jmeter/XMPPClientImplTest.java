package ru.yandex.jmeter;

import java.io.InputStream;
import java.io.OutputStream;
import kg.apc.emulators.SocketEmulatorInputStream;
import kg.apc.emulators.SocketEmulatorOutputStream;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author undera
 */
public class XMPPClientImplTest {

    public XMPPClientImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of write method, of class XMPPClientImpl.
     */
    @Test
    public void testWrite_OutputStream_InputStream() {
        System.out.println("write");
        OutputStream os = null;
        InputStream is = null;
        XMPPClientImpl instance = new XMPPClientImpl();
        try {
            instance.write(os, is);
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     * Test of write method, of class XMPPClientImpl.
     */
    @Test
    public void testWrite_OutputStream_String() {
        System.out.println("write");
        SocketEmulatorOutputStream os = new SocketEmulatorOutputStream();
        String s = "test";
        XMPPClientImpl instance = new XMPPClientImpl();
        instance.write(os, s);
        String exp = "test";
        assertEquals(exp, os.getWrittenBytesAsString());
    }

    @Test
    public void testWrite_OutputStream_Empty() {
        System.out.println("write_empty");
        SocketEmulatorOutputStream os = new SocketEmulatorOutputStream();
        String s = "";
        XMPPClientImpl instance = new XMPPClientImpl();
        instance.write(os, s);
        String exp = "";
        assertEquals(exp, os.getWrittenBytesAsString());
    }

    /**
     * Test of read method, of class XMPPClientImpl.
     */
    @Test
    public void testRead() {
        System.out.println("read");
        String str = "<test></test><test></test>";
        SocketEmulatorInputStream is = new SocketEmulatorInputStream(str.getBytes());
        XMPPClientImpl instance = new XMPPClientImpl();
        String expResult = "<test></test><test></test>";
        String result = instance.read(is);
        assertEquals(expResult, result);
    }

    @Test
    public void testRead_res() {
        System.out.println("read");
        String str = "<?xml version='1.0'?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' id='1873395557' from='ya.ru' version='1.0' xml:lang='en'><stream:features><starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/><compression xmlns='http://jabber.org/features/compress'><method>zlib</method></compression><mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'><mechanism>PLAIN</mechanism></mechanisms><ver xmlns='urn:xmpp:features:rosterver'/></stream:features>";
        SocketEmulatorInputStream is = new SocketEmulatorInputStream(str.getBytes());
        XMPPClientImpl instance = new XMPPClientImpl();
        String expResult = "</stream:features>";
        String result = instance.read(is);
        assertTrue(result.endsWith(expResult));
    }

    @Test
    public void testRead_differentTags() {
        System.out.println("read");
        String str = "<stream:features><compression xmlns='http://jabber.org/features/compress'><method>zlib</method></compression><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/><session xmlns='urn:ietf:params:xml:ns:xmpp-session'/><ver xmlns='urn:xmpp:features:rosterver'/></stream:features><iq id='bind_1' type='result'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><jid>stress-test-1@ya.ru/dhcp-218-110-wifi6152455</jid></bind></iq>";
        SocketEmulatorInputStream is = new SocketEmulatorInputStream(str.getBytes());
        XMPPClientImpl instance = new XMPPClientImpl();
        String result = instance.read(is);
        assertEquals(str, result);
    }

    @Test
    public void testRead_just_long() {
        System.out.println("read");
        String str = "<iq type='result' xmlns='jabber:client' id='aad49296350a'><session xmlns='urn:ietf:params:xml:ns:xmpp-session'/></iq><iq from='ya.ru' to='stress-test-1000@ya.ru/dhcp-218-110-wifi4929635' id='ask_version' type='get'><query xmlns='jabber:iq:version'/></iq><iq from='ya.ru' to='stress-test-1000@ya.ru/dhcp-218-110-wifi4929635' id='ping_0' type='get'><ping xmlns='urn:xmpp:ping'/></iq><iq from='stress-test-1000@ya.ru' to='stress-test-1000@ya.ru/dhcp-218-110-wifi4929635' id='aad49296351a' type='result'><query xmlns='jabber:iq:roster'><item subscription='both' name='Stress test user #8878' jid='stress-test-8878@ya.ru'/><item subscription='both' name='Stress test user #8757' jid='stress-test-8757@ya.ru'/><item subscription='both' name='Stress test user #7479' jid='stress-test-7479@ya.ru'/><item subscription='both' name='Stress test user #619' jid='stress-test-619@ya.ru'/><item subscription='both' name='Stress test user #5985' jid='stress-test-5985@ya.ru'/><item subscription='both' name='Stress test user #5971' jid='stress-test-5971@ya.ru'/><item subscription='both' name='Stress test user #5178' jid='stress-test-5178@ya.ru'/><item subscription='both' name='Stress test user #4569' jid='stress-test-4569@ya.ru'/><item subscription='both' name='Stress test user #39821' jid='stress-test-39821@ya.ru'/><item subscription='both' name='Stress test user #39417' jid='stress-test-39417@ya.ru'/><item subscription='both' name='Stress test user #3940' jid='stress-test-3940@ya.ru'/><item subscription='both' name='Stress test user #38350' jid='stress-test-38350@ya.ru'/><item subscription='both' name='Stress test user #37120' jid='stress-test-37120@ya.ru'/><item subscription='both' name='Stress test user #37009' jid='stress-test-37009@ya.ru'/><item subscription='both' name='Stress test user #36625' jid='stress-test-36625@ya.ru'/><item subscription='both' name='Stress test user #36505' jid='stress-test-36505@ya.ru'/><item subscription='both' name='Stress test user #36260' jid='stress-test-36260@ya.ru'/><item subscription='both' name='Stress test user #35215' jid='stress-test-35215@ya.ru'/><item subscription='both' name='Stress test user #34821' jid='stress-test-34821@ya.ru'/><item subscription='both' name='Stress test user #34308' jid='stress-test-34308@ya.ru'/><item subscription='both' name='Stress test user #33936' jid='stress-test-33936@ya.ru'/><item subscription='both' name='Stress test user #33220' jid='stress-test-33220@ya.ru'/><item subscription='both' name='Stress test user #32355' jid='stress-test-32355@ya.ru'/><item subscription='both' name='Stress test user #31070' jid='stress-test-31070@ya.ru'/><item subscription='both' name='Stress test user #30991' jid='stress-test-30991@ya.ru'/><item subscription='both' name='Stress test user #30576' jid='stress-test-30576@ya.ru'/><item subscription='both' name='Stress test user #28899' jid='stress-test-28899@ya.ru'/><item subscription='both' name='Stress test user #28297' jid='stress-test-28297@ya.ru'/><item subscription='both' name='Stress test user #28100' jid='stress-test-28100@ya.ru'/><item subscription='both' name='Stress test user #27797' jid='stress-test-27797@ya.ru'/><item subscription='both' name='Stress test user #27581' jid='stress-test-27581@ya.ru'/><item subscription='both' name='Stress test user #26600' jid='stress-test-26600@ya.ru'/><item subscription='both' name='Stress test user #26002' jid='stress-test-26002@ya.ru'/><item subscription='both' name='Stress test user #25382' jid='stress-test-25382@ya.ru'/><item subscription='both' name='Stress test user #222' jid='stress-test-222@ya.ru'/><item subscription='both' name='Stress test user #2102' jid='stress-test-2102@ya.ru'/><item subscription='both' name='Stress test user #20924' jid='stress-test-20924@ya.ru'/><item subscription='both' name='Stress test user #20422' jid='stress-test-20422@ya.ru'/><item subscription='both' name='Stress test user #19427' jid='stress-test-19427@ya.ru'/><item subscription='both' name='Stress test user #17723' jid='stress-test-17723@ya.ru'/><item subscription='both' name='Stress test user #1662' jid='stress-test-1662@ya.ru'/><item subscription='both' name='Stress test user #16219' jid='stress-test-16219@ya.ru'/><item subscription='both' name='Stress test user #16028' jid='stress-test-16028@ya.ru'/><item subscription='both' name='Stress test user #14077' jid='stress-test-14077@ya.ru'/><item subscription='both' name='Stress test user #13292' jid='stress-test-13292@ya.ru'/><item subscription='both' name='Stress test user #13025' jid='stress-test-13025@ya.ru'/><item subscription='both' name='Stress test user #11992' jid='stress-test-11992@ya.ru'/><item subscription='both' name='Stress test user #1122' jid='stress-test-1122@ya.ru'/><item subscription='both' name='Stress test user #10646' jid='stress-test-10646@ya.ru'/><item subscription='both' name='Stress test user #10442' jid='stress-test-10442@ya.ru'/><item subscription='both' name='Facebook transport' jid='fb.ya.ru'/></query></iq>";
        SocketEmulatorInputStream is = new SocketEmulatorInputStream(str.getBytes());
        XMPPClientImpl instance = new XMPPClientImpl();
        String result = instance.read(is);
        assertEquals(str, result);
    }

    @Test
    public void testRead_res1() {
        System.out.println("read");
        String str = "<?xml version='1.0'?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' id='1873395557' from='ya.ru' version='1.0' xml:lang='en'><stream:features><starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/><compression xmlns='http://jabber.org/features/compress'><method>zlib</method></compression><mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'><mechanism>PLAIN</mechanism></mechanisms><ver xmlns='urn:xmpp:features:rosterver'/>";
        SocketEmulatorInputStream is = new SocketEmulatorInputStream(str.getBytes());
        XMPPClientImpl instance = new XMPPClientImpl();
        try {
            instance.read(is);
            fail();
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void testReadWriteChained() {
        XMPPClientImpl instance = new XMPPClientImpl();

        String req = "<iq xmlns='jabber:client' type='get' id='aad49296351a' ><query xmlns='jabber:iq:roster'/></iq>";
        SocketEmulatorOutputStream os = new SocketEmulatorOutputStream();
        instance.write(os, req);
        String resp = "<iq from='stress-test-1000@ya.ru' to='stress-test-1000@ya.ru/dhcp-218-110-wifi4929635' id='aad49296351a' type='result'><query xmlns='jabber:iq:roster'><item subscription='both' name='Stress test user #8878' jid='stress-test-8878@ya.ru'/></iq>";
        SocketEmulatorInputStream is = new SocketEmulatorInputStream(resp.getBytes());
        String result = instance.read(is);
    }

    @Test
    public void testReadWriteChained_notfound() {
        XMPPClientImpl instance = new XMPPClientImpl();

        String req = "<iq xmlns='jabber:client' type='get' id='aad49296351a' ><query xmlns='jabber:iq:roster'/></iq>";
        SocketEmulatorOutputStream os = new SocketEmulatorOutputStream();
        instance.write(os, req);
        // just wrong id attribute
        String resp = "<iq from='stress-test-1000@ya.ru' to='stress-test-1000@ya.ru/dhcp-218-110-wifi4929635' id='aad49296351B' type='result'><query xmlns='jabber:iq:roster'><item subscription='both' name='Stress test user #8878' jid='stress-test-8878@ya.ru'/></iq>";
        SocketEmulatorInputStream is = new SocketEmulatorInputStream(resp.getBytes());
        try {
            instance.read(is);
            fail();
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void testReadWrite_skipread() {
        XMPPClientImpl instance = new XMPPClientImpl();

        String req = "<iq dontread='true'></iq><!--dontread-->";
        SocketEmulatorOutputStream os = new SocketEmulatorOutputStream();
        instance.write(os, req);
        String resp = "<iq from='stress-test-1000@ya.ru' to='stress-test-1000@ya.ru/dhcp-218-110-wifi4929635' id='aad49296351a' type='result'><query xmlns='jabber:iq:roster'><item subscription='both' name='Stress test user #8878' jid='stress-test-8878@ya.ru'/></iq>";
        SocketEmulatorInputStream is = new SocketEmulatorInputStream(resp.getBytes());
        String result = instance.read(is);
        assertEquals("", result);
    }

    /**
     * Test of setupTest method, of class XMPPClientImpl.
     */
    @Test
    public void testSetupTest() {
        System.out.println("setupTest");
        XMPPClientImpl instance = new XMPPClientImpl();
        instance.setupTest();
    }

    /**
     * Test of teardownTest method, of class XMPPClientImpl.
     */
    @Test
    public void testTeardownTest() {
        System.out.println("teardownTest");
        XMPPClientImpl instance = new XMPPClientImpl();
        instance.teardownTest();
    }

    /**
     * Test of getEolByte method, of class XMPPClientImpl.
     */
    @Test
    public void testGetEolByte() {
        System.out.println("getEolByte");
        XMPPClientImpl instance = new XMPPClientImpl();
        byte expResult = 10;
        byte result = instance.getEolByte();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEolByte method, of class XMPPClientImpl.
     */
    @Test
    public void testSetEolByte() {
        System.out.println("setEolByte");
        int i = 0;
        XMPPClientImpl instance = new XMPPClientImpl();
        instance.setEolByte(i);
    }
}

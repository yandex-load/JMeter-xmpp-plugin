package ru.yandex.jmeter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import org.apache.jmeter.protocol.tcp.sampler.TCPClient;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class XMPPClientImpl implements TCPClient {

    private static final Logger log = LoggingManager.getLoggerForClass();
    private static final String XML_HEADER = "?xml";
    private static final String ID = "id=";
    private static final String IQ = "<iq";
    private static final String LT = "<";
    private static final String GT = ">";
    private static final String CloseGT = LT + "/";
    private static final String MESSAGE = "<message";
    private static final String SPACE = " ";
    private static final String STREAMFEATURES = "stream:features";
    private static final String streamTag = "stream:stream";
    private static final String streamEND = '/' + streamTag + '>';
    private String expectString;
    private boolean readResponse = true;
    private static final String DONT_READ_RESPONSE = "<!--dontread-->";
    private static final String IGNORE_TIMEOUT = "<!--ignore_timeout-->";
    private boolean ignoreTimeout;

    public XMPPClientImpl() {
    }

    @Override
    public void write(OutputStream os, InputStream is) {
        throw new UnsupportedOperationException("Not used");
    }

    @Override
    public void write(OutputStream os, String s) {
        if (s.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("No data to, just proceed to reading");
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Write data: " + s);
        }

        expectString = getStreamID(s);
        if (log.isDebugEnabled()) {
            log.debug("Expecting ID: " + expectString);
        }

        readResponse = !s.contains(DONT_READ_RESPONSE);
        ignoreTimeout = s.contains(IGNORE_TIMEOUT);

        try {
            os.write(s.getBytes());
            os.flush();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to send data: " + s, ex);
        }
    }

    private String getStartTag(final String str) throws RuntimeException {
        int gt = str.indexOf(GT);
        if (gt < 0) {
            throw new RuntimeException("Can't find first tag, it's strange: " + str);
        }
        String startTag;
        int space = str.indexOf(SPACE);
        if (space > 0 && space < gt) {
            startTag = str.substring(1, space);
        } else {
            startTag = str.substring(1, gt);
        }
        return startTag;
    }

    private String getStreamID(String strWrite) {
        if (strWrite.contains(MESSAGE) || strWrite.contains(IQ)) {
            int start_indexId = strWrite.indexOf(ID);
            if (start_indexId >= 0) {
                int end_indexId = strWrite.indexOf(SPACE, start_indexId);
                return strWrite.substring(start_indexId + 4, end_indexId - 1);
            }
        }
        return null;
    }

    @Override
    public String read(InputStream is) {
        if (!readResponse) {
            if (log.isDebugEnabled()) {
                log.debug("Skip reading data");
            }
            return "";
        } else if (log.isDebugEnabled()) {
            log.debug("Reading data");
        }

        byte[] buffer = new byte[4096];
        ByteArrayOutputStream w = new ByteArrayOutputStream();
        StringBuilder stringBuffer = new StringBuilder();
        int x;
        int retries = 0;
        String res = "";
        try {
            do {
                if (retries++ > 1000) {
                    throw new RuntimeException("Retries more than 1000, aborting read");
                }

                if (log.isDebugEnabled()) {
                    log.debug("Reading from stream");
                }

                // read from the network
                try {
                    x = is.read(buffer);
                } catch (SocketTimeoutException e) {
                    if (!ignoreTimeout) {
                        throw e;
                    } else {
                        log.warn("Skipped socket timeout", e);
                    }
                    return e.getMessage();
                }

                if (x > 0) {
                    w.write(buffer, 0, x);
                    stringBuffer.append(w.toString());
                    res += w.toString();
                    w.reset();
                }

                // analyze next data chunk
                while (stringBuffer.length() > 0 && processNextChunk(stringBuffer)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Remaining [" + stringBuffer.length() + "]: " + stringBuffer.toString());
                    }
                }

            } while (stringBuffer.length() > 0);
        } catch (Exception e) {
            log.error("Error reading data", e);
            throw new RuntimeException("Error reading data", e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Res is: " + stringBuffer);
        }
        // do we need to close byte array (or flush it?)
        return res;
    }

    /**
     *
     * @param buffer
     * @return true if chunk was processed, false otherwise (not complete chunk)
     */
    private boolean processNextChunk(StringBuilder buffer) {
        final String str = buffer.toString();
        if (log.isDebugEnabled()) {
            log.debug("Processing chunk: " + str);
        }

        if (str.equals(streamEND)) {
            if (log.isDebugEnabled()) {
                log.debug("Stream end reached: " + str);
                return false;
            }
        }

        String startTag = getStartTag(str);

        if (expectString != null) {
            if (log.isDebugEnabled()) {
                log.debug("Expecting Request ID: " + expectString);
            }
            if (!str.contains(expectString)) {
                return false;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Request ID found: " + expectString + ", clearing expected request ID");
                }
                expectString = null;
            }
        }

        return isChunkProcessed(startTag, buffer, str);
    }

    private boolean isChunkProcessed(String startTag, StringBuilder buffer, final String str) {
        if (startTag.equals(XML_HEADER)) {
            if (log.isDebugEnabled()) {
                log.debug("Skipping xml header");
            }
            buffer.delete(0, str.indexOf(GT) + 1);
            expectString = STREAMFEATURES;
            return true;
        } else if (startTag.equals(streamTag)) {
            if (log.isDebugEnabled()) {
                log.debug("Skipping stream tag: " + str.substring(0, str.indexOf(GT) + 1));
            }
            buffer.delete(0, str.indexOf(GT) + 1);
            return true;
        } else {
            final String endTag = CloseGT + startTag + GT;
            if (str.contains(endTag)) {
                if (log.isDebugEnabled()) {
                    log.debug("Finished processing chunk: " + str);
                }
                buffer.delete(0, str.indexOf(endTag) + endTag.length());
                return true;
            }
        }
        return false;
    }

    @Override
    public void setupTest() {
    }

    @Override
    public void teardownTest() {
    }

    @Override
    public byte getEolByte() {
        return '\n';
    }

    @Override
    public void setEolByte(int i) {
    }

   
    public String getCharset() {
        return Charset.defaultCharset().name();
    }
}

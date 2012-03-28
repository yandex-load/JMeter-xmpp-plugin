package ru.yandex.jmeter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import org.apache.jmeter.protocol.tcp.sampler.TCPClient;
public class XMPPClientImpl implements TCPClient {
private static final String XML_HEADER="?xml";
private static final String ID="id=";
private static final String IQ="<iq";
private static final String LT="<";
private static final String GT=">";
private static final String CloseGT=LT+"/";
private static final String MESSAGE="<message";
private static final String SPACE=" ";
private static final String STREAMFEATURES="stream:features";
private static final String streamTag="stream:stream";
private static final String streamEND='/'+streamTag+'>';
private String expectString;
private boolean readResponse=true;
private static final String DONT_READ_RESPONSE="<!--dontread-->";
private static final String IGNORE_TIMEOUT="<!--ignore_timeout-->";
private boolean ignoreTimeout;
public XMPPClientImpl() {
}
public void write(OutputStream os,InputStream is) {
 throw new UnsupportedOperationException("Not used");
}
public void write(OutputStream os,String s) {
 if (s.isEmpty())
  return;

 expectString=getStreamID(s);
 readResponse=!s.contains(DONT_READ_RESPONSE);
 ignoreTimeout=s.contains(IGNORE_TIMEOUT);

 try {
  os.write(s.getBytes());
  os.flush();
 } catch (IOException ex) {
  throw new RuntimeException("Failed to send data: "+s,ex);
 }
}
private String getStartTag(final String str) throws RuntimeException {
 int gt=str.indexOf(GT);
 if (gt<0)
  throw new RuntimeException("Can't find first tag, it's strange: "+str);
 String startTag;
 int space=str.indexOf(SPACE);
 if (space>0&&space<gt)
  startTag=str.substring(1,space);
 else
  startTag=str.substring(1,gt);
 return startTag;
}
private String getStreamID(String strWrite) {
 if (strWrite.contains(MESSAGE)||strWrite.contains(IQ)) {
  int start_indexId=strWrite.indexOf(ID);
  if (start_indexId>=0) {
   int end_indexId=strWrite.indexOf(SPACE,start_indexId);
   return strWrite.substring(start_indexId+4,end_indexId-1);
  }
 }
 return null;
}
public String read(InputStream is) {
 if (!readResponse)
  return "";

 byte[] buffer=new byte[4096];
 ByteArrayOutputStream w=new ByteArrayOutputStream();
 StringBuilder stringBuffer=new StringBuilder();
 int x;
 int retries=0;
 String res="";
 try {
  do {
   if (retries++>1000)
    throw new RuntimeException("Retries more than 1000, aborting read");

   try {
    x=is.read(buffer);
   } catch (SocketTimeoutException e) {
    if (!ignoreTimeout)
     throw e;
    return e.getMessage();
   }

   if (x>0) {
    w.write(buffer,0,x);
    stringBuffer.append(w.toString());
    res+=w.toString();
    w.reset();
   }

   while (stringBuffer.length()>0&&processNextChunk(stringBuffer)) {
   }

  } while (stringBuffer.length()>0);
 } catch (Exception e) {
  throw new RuntimeException("Error reading data",e);
 }

 return res;
}
private boolean processNextChunk(StringBuilder buffer) {
 final String str=buffer.toString();
 if (str.equals(streamEND))
  return false;

 String startTag=getStartTag(str);

 if (expectString!=null)
  if (!str.contains(expectString))
   return false;
  else
   expectString=null;

 return isChunkProcessed(startTag,buffer,str);
}
private boolean isChunkProcessed(String startTag,StringBuilder buffer,final String str) {
 if (startTag.equals(XML_HEADER)) {
  buffer.delete(0,str.indexOf(GT)+1);
  expectString=STREAMFEATURES;
  return true;
 } else if (startTag.equals(streamTag)) {
  buffer.delete(0,str.indexOf(GT)+1);
  return true;
 } else {
  final String endTag=CloseGT+startTag+GT;
  if (str.contains(endTag)) {
   buffer.delete(0,str.indexOf(endTag)+endTag.length());
   return true;
  }
 }
 return false;
}
public void setupTest() {
}
public void teardownTest() {
}
public byte getEolByte() {
 return '\n';
}
public void setEolByte(int i) {
}
public String getCharset() {
 return Charset.defaultCharset().name();
}
}

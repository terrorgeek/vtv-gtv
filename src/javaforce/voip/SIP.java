package javaforce.voip;

import javaforce.JF;
import javaforce.JFLog;
import javaforce.MD5;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/** Base class for SIP communications.  Opens the UDP port and passes any received packets thru the SIPInterface.<br>
Known derivates : SIPClient, SIPServer.*/

public abstract class SIP {
  private DatagramSocket sock;
  private Worker worker;
  private SIPInterface iface;
  private boolean active = true;
  private String rinstance;
  private String tupleid;
  private Random r = new Random();
  private boolean server;
  protected static String useragent = "JavaForce/" + JF.getVersion();

  /** Opens the UDP port and sets the SIPInterface callback. */

  protected boolean init(int port, SIPInterface iface, boolean server) throws Exception {
    rinstance = null;
    this.iface = iface;
    this.server = server;
    sock = new DatagramSocket(port);
    worker = new Worker();
    worker.start();
    return true;
  }

  /** Closes the UDP port and frees and resources. */

  protected void uninit() {
    if (sock == null) return;
    active = false;
    sock.close();
    try {worker.join();} catch (Exception e) {}
    sock = null;
    worker = null;
  }

  /** Sends a packet out on the UDP port. */

  protected boolean send(String remote, int remoteport, String datastr) {
    byte data[] = datastr.getBytes();
    try {
      sock.send(new DatagramPacket(data, data.length, InetAddress.getByName(remote), remoteport));
      return true;
    } catch (Exception e) {
    }
    return false;
  }

  /** Splits a To: or From: field in a SIP message into parts. */

  public static String[] split(String x) {
    //x = "display name" <sip:user@host ;...   >  ;...
    //return:    [0]          [1]  [2] [flgs1][:][flgs2]
    ArrayList<String> parts = new ArrayList<String>();
    int i1, i2;
    String x1, x2;
    i1 = x.indexOf('<');
    if (i1 == -1) return null;
    if (i1 == 0) {
      parts.add("Unknown Name");
    } else {
      parts.add(x.substring(0, i1).trim().replaceAll("\"", ""));
    }
    i1++;
    i2 = x.substring(i1).indexOf('>');
    if (i2 == -1) return null;
    x1 = x.substring(i1, i1+i2);
    x2 = x.substring(i1+i2+1).trim();
    i1 = x1.indexOf(':');
    if (i1 == -1) return null;
    x1 = x1.substring(i1+1);  //remove sip:
    i1 = x1.indexOf('@');
    if (i1 == -1) {
      parts.add("");  //no user
    } else {
      parts.add(x1.substring(0, i1).trim());  //userid
      x1 = x1.substring(i1+1).trim();
    }
    if ((x1.length() > 0) && (x1.charAt(0) == ';')) x1 = x1.substring(1);
    do {
      i1 = x1.indexOf(';');
      if (i1 == -1) {
        x1 = x1.trim();
        if (x1.length() > 0) {
          parts.add(x1);
        }
        break;
      }
      parts.add(x1.substring(0, i1).trim());
      x1 = x1.substring(i1+1).trim();
    } while (true);
    if (parts.size() == 2) parts.add("");  //no host ???
    parts.add(":");  //this seperates fields outside of <>
    if ((x2.length() > 0) && (x2.charAt(0) == ';')) x2 = x2.substring(1);
    do {
      i1 = x2.indexOf(';');
      if (i1 == -1) {
        x2 = x2.trim();
        if (x2.length() > 0) {
          parts.add(x2);
        }
        break;
      }
      parts.add(x2.substring(0, i1).trim());
      x2 = x2.substring(i1+1).trim();
    } while (true);
    String ret[] = new String[parts.size()];
    for(int a=0;a<parts.size();a++) {
      ret[a] = parts.get(a);
    }
    return ret;
  }

  /** Joins a To: or From: field after it was split into parts. */

  public static String join(String x[]) {
    //x = "display name" <sip:user@host ;...  > ;...
    //return:    [0]          [1]  [2]  [...][:][...]
    StringBuffer buf = new StringBuffer();
    buf.append('\"');
    buf.append(x[0]);
    buf.append('\"');
    buf.append("<sip:");
    buf.append(x[1]);
    buf.append('@');
    buf.append(x[2]);
    int i = 3;
    for(;(i < x.length) && (!x[i].equals(":"));i++) {
      buf.append(';');
      buf.append(x[i]);
    }
    i++;  //skip ':' seperator
    buf.append('>');
    for(;i<x.length;i++) {
      buf.append(';');
      buf.append(x[i]);
    }
    return buf.toString();
  }

  /** Returns a flag in a To: From: field. */

  public static String getFlag2(String fields[], String flg) {
    flg += "=";
    int i;
    for(i=0;i<fields.length;i++) {
      if (fields[i].equals(":")) break;
    }
    if (i==fields.length) return "";
    i++;
    for(i=0;i<fields.length;i++) {
      if (fields[i].startsWith(flg)) return fields[i].substring(flg.length());
    }
    return "";  //do not return null
  }

  /** Sets/adds a flag in a To: From: field. */

  public static String[] setFlag2(String fields[], String flg, String value) {
    flg += "=";
    boolean seperator = false;
    for(int i=3;i<fields.length;i++) {
      if (!seperator) {
        if (fields[i].equals(":")) seperator = true;
        continue;
      }
      if (fields[i].startsWith(flg)) {
        fields[i] = flg + value;
        return fields;
      }
    }
    //need to add an element to fields and append "flg=value"
    String newfields[] = new String[fields.length + 1];
    for(int j=0;j<fields.length;j++) newfields[j] = fields[j];
    newfields[fields.length] = flg + value;
    return newfields;
  }

  /** Returns a random SIP branch id. */

  protected String getbranch() {
    return String.format("z123456-y12345-%x%x-1--d12345-", r.nextInt(), r.nextInt());
  }

  /** Determines if a SIP message is on hold. */

  protected boolean ishold(String msg[]) {
    //does msg contain "a=sendonly"?
    for(int a=0;a<msg.length;a++) {
      if (msg[a].equalsIgnoreCase("a=sendonly")) return true;
    }
    return false;
  }

  /** Returns the Via: list in a SIP message as an array. */

  protected String[] getvialist(String msg[]) {
    ArrayList<String> vialist = new ArrayList<String>();
    for(int a=0;a<msg.length;a++) {
      if (msg[a].regionMatches(true, 0, "Via:", 0, 4)) {vialist.add(msg[a]); continue;}
      if (msg[a].regionMatches(true, 0, "v:", 0, 2)) {vialist.add(msg[a]); continue;}
    }
    return vialist.toArray(new String[0]);
  }

  /** Returns a random generated rinstance id. */

  protected String getrinstance() {
    if (rinstance != null) return rinstance;
    rinstance = String.format("%x%x", r.nextInt(), r.nextInt());
    return rinstance;
  }

  /** Returns a random generated tuple id. */

  protected String gettupleid() {
    if (tupleid != null) return tupleid;
    tupleid = String.format("%08x", r.nextInt());
    return tupleid;
  }

  /** Returns the URI part of a SIP message. */

  protected String geturi(String msg[]) {
    //cmd uri SIP/2.0\r\n
    int idx1 = msg[0].indexOf(' ');
    if (idx1 == -1) return null;
    int idx2 = msg[0].substring(idx1+1).indexOf(' ');
    if (idx2 == -1) return null;
    return msg[0].substring(idx1+1).substring(0, idx2);
  }

  /** Returns a random generated tag for the To: or From: parts of a SIP message. This function is used by replacetag() so it must resemble a To: or From: field. */

  public static String generatetag() {
    Random r = new Random();
    return String.format("null<sip:null@null>;tag=%x%x", r.nextInt(), r.nextInt());
  }

  /** Replaces the 'tag' field from 'newfield' into 'fields'. */

  public static String[] replacetag(String fields[], String newfield) {
    //x = "display name" <sip:user@host;tag=...>;tag=...
    //           [0]          [1]  [2]  [...] [:][...]
    if (newfield == null) return fields;
    String newfields[] = split(newfield);
    int oldtagidx = -1;
    boolean seperator = false;
    for(int i=3;i<fields.length;i++) {
      if (!seperator) {
        if (fields[i].equals(":")) seperator = true;
        continue;
      }
      if (fields[i].startsWith("tag=")) {
        oldtagidx = i;
        break;
      }
    }
    seperator = false;
    for(int i=3;i<newfields.length;i++) {
      if (!seperator) {
        if (newfields[i].equals(":")) seperator = true;
        continue;
      }
      if (newfields[i].startsWith("tag=")) {
        if (oldtagidx != -1) {
          fields[oldtagidx] = newfields[i];
          return fields;
        } else {
          //need to add an element to fields and append newfields[i]
          String retfields[] = new String[fields.length + 1];
          for(int j=0;j<fields.length;j++) retfields[j] = fields[j];
          retfields[fields.length] = newfields[i];
          return retfields;
        }
      }
    }
    return fields;
  }

  /** Removes the 'tag' field from 'fields'. */

  public static String[] removetag(String fields[]) {
    boolean seperator = false;
    for(int i=3;i<fields.length;i++) {
      if (!seperator) {
        if (fields[i].equals(":")) seperator = true;
        continue;
      }
      if (fields[i].startsWith("tag=")) {
        //remove fields[i]
        String newfields[] = new String[fields.length-1];
        for(int j=0;j<i;j++) newfields[j] = fields[j];
        for(int j=i+1;j<fields.length;j++) newfields[j-1] = fields[j];
        return newfields;
      }
    }
    return fields;  //no tag found
  }

  /** Returns a random callid for a SIP message (a unique id for each call, not to be confused with caller id). */

  protected String getcallid() {
    return String.format("%x%x", r.nextInt(), System.currentTimeMillis());
  }

  /** Returns current time in seconds. */

  protected long getNow() {
    return System.currentTimeMillis() / 1000;
  }

  /** Returns a random nonce variable used in SIP authorization. */

  protected String getnonce() {
    return String.format("%x%x%x%x", r.nextInt(), r.nextInt(), System.currentTimeMillis(), r.nextInt());
  }

  /** Returns string name of codec based on payload id (except dynamic ids 96-127). */

  private String getCodecName(int id) {
    switch (id) {
      case 0: return "PCMU";
      case 8: return "PCMA";
      case 18: return "G729";
      case 34: return "H263";
    }
    return "?";
  }

  /** Returns an array of codecs in a SIP/SDP packet. */

  protected Codec[] getCodecs(String msg[]) {
    //id 96-127 = dynamic
    int id;
    ArrayList<Codec> lst = new ArrayList<Codec>();
    //m=audio port RTP/AVP 18 0 101
    String ma = getHeader("m=audio ", msg);
    if (ma != null) {
      String tags[] = ma.split(" ");
      int cnt = tags.length - 2;
      if (cnt > 0) {
        for(int a=2;a<tags.length;a++) {
          if (tags[a].length() == 0) continue;
          id = Integer.valueOf(tags[a]);
          if (id < 96) lst.add(new Codec(getCodecName(id), id));
        }
      }
    }
    //m=video port RTP/AVP 34 99 125
    String mv = getHeader("m=video ", msg);
    if (mv != null) {
      String tags[] = mv.split(" ");
      int cnt = tags.length - 2;
      if (cnt > 0) {
        for(int a=2;a<tags.length;a++) {
          if (tags[a].length() == 0) continue;
          id = Integer.valueOf(tags[a]);
          if (id < 96) lst.add(new Codec(getCodecName(id), id));
        }
      }
    }
    //process dynamic ids (a=rtpmap:## name/bitrate)
    String dyn[] = getHeaders("a=", msg);
    for(int a=0;a<dyn.length;a++) {
      if (dyn[a].startsWith("rtpmap:")) {
        int idx = dyn[a].indexOf(" ");
        if (idx == -1) continue;
        id = Integer.valueOf(dyn[a].substring(7, idx));
        if (id < 96) continue;  //already done static ids
        if (dyn[a].indexOf("H264") != -1) lst.add(new Codec("H264", id));
        if (dyn[a].indexOf("telephone-event") != -1) lst.add(new Codec("telephone-event", id));
      }
    }
    return lst.toArray(new Codec[0]);
  }

  /** Determines if codecs[] contains codec. */

  public static boolean hasCodec(Codec codecs[], Codec codec) {
    for(int a=0;a<codecs.length;a++) {
      if (codecs[a].name.equals(codec.name)) return true;
    }
    return false;
  }

  /** Adds a codec to a list of codecs. */

  public static Codec[] addCodec(Codec codecs[], Codec codec) {
    Codec newCodecs[] = new Codec[codecs.length + 1];
    for(int a=0;a<codecs.length;a++) newCodecs[a] = codecs[a];
    newCodecs[codecs.length] = codec;
    return newCodecs;
  }

  /** Removes a codec from a list of codecs. */

  public static Codec[] delCodec(Codec codecs[], Codec codec) {
    if (!hasCodec(codecs, codec)) return codecs;
    Codec newCodecs[] = new Codec[codecs.length - 1];
    int pos = 0;
    for(int a=0;a<codecs.length;a++) {
      if (codecs[a].name.equals(codec.name)) continue;
      newCodecs[pos++] = codecs[a];
    }
    return newCodecs;
  }

  /** Returns a codec from a list of codecs. Comparison is done by name only. The returned codec 'id' may be different than provided codec. */

  public static Codec getCodec(Codec codecs[], Codec codec) {
    for(int a=0;a<codecs.length;a++) {
      if (codecs[a].name.equals(codec.name)) return codecs[a];
    }
    return null;
  }

  /** Returns the requested operation of a SIP message. (INVITE, BYE, etc.) */

  protected String getRequest(String msg[]) {
    int idx = msg[0].indexOf(" ");
    if (idx == -1) return null;
    return msg[0].substring(0, idx);
  }

  /** Returns the response number from a SIP reply message. (100, 200, 401, etc.) */

  protected int getResponseType(String msg[]) {
    if (msg[0].length() < 11) return -1;  //bad msg
    if (!msg[0].regionMatches(true, 0, "SIP/2.0 ", 0, 8)) return -1;  //not a response
    //SIP/2.0 ### ...
    return Integer.valueOf(msg[0].substring(8, 11));
  }

  /** Returns a specific header (field) from a SIP message. */

  public static String getHeader(String header, String msg[]) {
    int sl = header.length();
    for(int a=0;a<msg.length;a++) {
      if (msg[a].length() < sl) continue;
      if (msg[a].substring(0, sl).equalsIgnoreCase(header)) {
        return msg[a].substring(sl).trim().replaceAll("\"", "");
      }
    }
    return null;
  }

  /** Returns a set of specific headers (fields) from a SIP message. */

  public static String[] getHeaders(String header, String msg[]) {
    ArrayList<String> lst = new ArrayList<String>();
    int sl = header.length();
    for(int a=0;a<msg.length;a++) {
      if (msg[a].length() < sl) continue;
      if (msg[a].substring(0, sl).equalsIgnoreCase(header)) {
        lst.add(msg[a].substring(sl).trim().replaceAll("\"", ""));
      }
    }
    return lst.toArray(new String[0]);
  }

  /** Returns the cseq of a SIP message. */

  protected int getcseq(String msg[]) {
    String cseqstr = getHeader("CSeq:", msg);
    if (cseqstr == null) return -1;
    String parts[] = cseqstr.split(" ");
    return Integer.valueOf(parts[0]);
  }

  /** Returns the command at the end of the cseq header in a SIP message. */

  protected String getcseqcmd(String msg[]) {
    String cseqstr = getHeader("CSeq:", msg);
    if (cseqstr == null) return null;
    String parts[] = cseqstr.split(" ");
    if (parts.length < 2) return null;
    return parts[1];
  }

  /** Generates a response to a SIP authorization challenge. */

  protected String getResponse(String user, String pass, String realm, String cmd, String uri, String nonce, String qop, String nc, String cnonce) {
    if (pass.equals(".")) return iface.getResponse(realm, cmd, uri, nonce, qop, nc, cnonce);
    MD5 md5 = new MD5();
    String H1 = user + ":" + realm + ":" + pass;
    md5.init();
    md5.add(H1.getBytes(),0,H1.length());
    H1 = new String(md5.byte2char(md5.done()));
    String H2 = cmd + ":" + uri;
    md5.init();
    md5.add(H2.getBytes(),0,H2.length());
    H2 = new String(md5.byte2char(md5.done()));
    String H3 = H1 + ":" + nonce + ":";
    if ((qop != null) && (qop.length() > 0)) {
      H3 += nc + ":" + cnonce + ":" + qop + ":";
    }
    H3 += H2;
    md5.init();
    md5.add(H3.getBytes(),0,H3.length());
    H3 = new String(md5.byte2char(md5.done()));
    return H3;
  }

  /** Generates a complete header response to a SIP authorization challenge. */

  protected String getAuthResponse(String request, String user, String pass, String remote, String cmd, String header) {
    //request = ' Digest algorithm=MD5, realm="asterisk", nonce="value", etc.'
    if (!request.regionMatches(true, 0, "Digest ", 0, 7)) {
      JFLog.log("err:no digest"); return null;}
    String tags[] = request.substring(7).replaceAll(" ","").split(",");  //NOTE:if qop="auth-int,auth" this split will fail (qop="auth,auth-int" will be okay)
    String auth, nonce = null, qop = null, nc = null, cnonce = null, stale = null;
    String realm = null;
    auth = getHeader("algorithm=", tags);
    if (auth != null) {
      if (!auth.equalsIgnoreCase("MD5")) {
        JFLog.log("err:only MD5 auth supported"); return null;}  //unsupported auth type
    }
    realm = getHeader("realm=", tags);
    nonce = getHeader("nonce=", tags);
    qop = getHeader("qop=", tags);  //auth or auth-int
    stale = getHeader("stale=", tags);  //true|false ???
    if (nonce == null) {
      JFLog.log("err:no nonce"); return null;}  //no nonce found
    if (realm == null) {
      JFLog.log("err:no realm"); return null;}  //no realm found
    if (qop != null) {
      String qops[] = qop.split(",");  //server could provide multiple options
      qop = null;
      for(int a=0;a<qops.length;a++) {
        if (qops[a].trim().equals("auth")) {
          qop = "auth";
          break;
        }
      }
      if (qop != null) {
        //generate cnonce and nc
        nc = "00000001";  //never use a nonce twice
        cnonce = getnonce();
      }
    }
    String response = getResponse(user, pass, realm, cmd, "sip:" + remote, nonce, qop, nc, cnonce);
    StringBuffer ret = new StringBuffer();
    ret.append(header);
    ret.append(" Digest username=\"" + user + "\", realm=\"" + realm + "\", uri=\"sip:" + remote + "\", nonce=\"" + nonce + "\"");
    ret.append(", response=\"" + response + "\"");
    if (qop != null) {
      ret.append(", qop=\"" + qop + "\"");
    }
    if (nc != null) ret.append(", nc=\"" + nc + "\"");
    if (cnonce != null) ret.append(", cnonce=\"" + cnonce + "\"");
    ret.append(", algorithm=MD5\r\n");
    return ret.toString();
  }

  /** Returns the remote RTP host in a SIP/SDP packet. */

  protected String getremotertphost(String msg[]) {
    String c = getHeader("c=", msg);
    if (c == null) {
      String from = getHeader("From:", msg);
      if (from == null) {
        return null;
      }
      int indexStart = from.indexOf("@");
      int indexEnd = from.lastIndexOf(":");
      if (indexStart < 0 || indexEnd < 0) {
        return null;
      }
      return from.substring(indexStart + 1, indexEnd);
    }
    int idx = c.indexOf("IP4 ");
    if (idx == -1) return null;
    return c.substring(idx+4);
  }

  /** Returns the remote RTP port in a SIP/SDP packet. */

  protected int getremotertpport(String msg[]) {
    // m=audio PORT RTP/AVP ...
    String m = getHeader("m=audio ", msg);
    if (m == null) return -1;
    int idx = m.indexOf(' ');
    if (idx == -1) return -1;
    return Integer.valueOf(m.substring(0, idx));
  }

  /** Returns the remote Video RTP port in a SIP/SDP packet. */

  protected int getremoteVrtpport(String msg[]) {
    // m=video PORT RTP/AVP ...
    String m = getHeader("m=video ", msg);
    if (m == null) return -1;
    int idx = m.indexOf(' ');
    if (idx == -1) return -1;
    return Integer.valueOf(m.substring(0, idx));
  }

  /** Returns the 'o' counts in a SIP/SDP packet. idx can be 1 or 2. */

  protected int geto(String msg[], int idx) {
    //o=blah o1 o2 ...
    String o = getHeader("o=", msg);
    if (o == null) return 0;
    String os[] = o.split(" ");
    return Integer.valueOf(os[idx]);
  }

  /** Returns "expires" field from SIP headers. */

  public int getexpires(String msg[]) {
    //check Expires field
    String expires = getHeader("Expires:", msg);
    if (expires != null) return JF.atoi(expires);
    //check Contact field
    String contact = getHeader("Contact:", msg);
    if (contact == null) contact = getHeader("c:", msg);
    if (contact == null) return -1;
    String tags[] = split(contact);
    expires = getHeader("expires=", tags);
    if (expires == null) return -1;
    return JF.atoi(expires);
  }

  public abstract String getlocalhost();

  /** Builds SDP packet. */

  public void buildsdp(CallDetails cd, CallDetails.SideDetails cdsd) {
    //build SDP content
    StringBuffer content = new StringBuffer();
    Codec h264, rfc2833;  //dyn codecs
    h264 = getCodec(cdsd.codecs, RTP.CODEC_H264);
    rfc2833 = getCodec(cdsd.codecs, RTP.CODEC_RFC2833);
    if (rfc2833 == null) rfc2833 = RTP.CODEC_RFC2833;  //always send RFC2833
    boolean g711u = /*hasCodec(cdsd.codecs, RTP.CODEC_G711u);*/ true;
    boolean g729a = hasCodec(cdsd.codecs, RTP.CODEC_G729a);
    boolean h263 = hasCodec(cdsd.codecs, RTP.CODEC_H263);
    content.append("v=0\r\n");
    content.append("o=- " + cdsd.o1 + " " + cdsd.o2 + " IN IP4 " + getlocalhost() + "\r\n");
    content.append("s=" + useragent + "\r\n");
    content.append("c=IN IP4 " + (cd.holding ? "0.0.0.0" : getlocalhost()) + "\r\n");
    content.append("t=0 0\r\n");
    content.append("m=audio " + cdsd.rtp_port_audio + " RTP/AVP");
    if (g711u) content.append(" 0");
    if (g729a) content.append(" 18");
    if (rfc2833 != null) content.append(" " + rfc2833.id);
    content.append("\r\n");
    if (g711u) content.append("a=rtpmap:0 PCMU/8000\r\n");
    if (g729a) {
      content.append("a=rtpmap:18 G729/8000\r\n");
      content.append("a=fmtp:18 annexb=no\r\n");
      content.append("a=silenceSupp:off - - - -\r\n");
    }
    if (rfc2833 != null) {
      content.append("a=rtpmap:" + rfc2833.id + " telephone-event/8000\r\n");
      content.append("a=fmtp:" + rfc2833.id + " 0-15\r\n");
    }
    content.append("a=ptime:20\r\n");
    if (cd.holding)
      content.append("a=sendonly\r\n");
    else if (cd.onhold)
      content.append("a=recvonly\r\n");
    else
      content.append("a=sendrecv\r\n");
    //BUG : What if holding && onhold?
    if (h263 || (h264 != null)) {
      content.append("m=video " + cdsd.rtp_port_video + " RTP/AVP");
      if (h263) content.append(" 34");
      if (h264 != null) content.append(" " + h264.id);
      content.append("\r\n");
      if (h263) content.append("a=rtpmap:34 H263/90000\r\n");
      if (h264 != null) content.append("a=rtpmap:" + h264.id + " H264/90000\r\n");
      content.append("a=sendrecv\r\n");
    }
    cd.sdp = content.toString();
  }

  private static HashMap<String, String> dnsCache = new HashMap<String, String>();

  /** Resolve hostname to IP address.  Keeps a cache to improve performance. */
  public static String resolve(String host) {
    //uses a small DNS cache
    //TODO : age and delete old entries (SIP servers should always have static IPs so this is not critical)
    String ip = dnsCache.get(host);
    if (ip != null) return ip;
    try {
      ip = InetAddress.getByName(host).getHostAddress();
    } catch (Exception e) {
      JFLog.log(e);
      return null;
    }
    JFLog.log("dns:" + host + "=" + ip);
    dnsCache.put(host, ip);
    return ip;
  }

  private final int mtu = 1460;  //max size of packet

  /** This thread handles reading incoming SIP packets and dispatches them thru SIPInterface. */

  private class Worker extends Thread {
    public void run() {
      while (active) {
        try {
          byte data[] = new byte[mtu];
          DatagramPacket pack = new DatagramPacket(data, mtu);
          sock.receive(pack);
          int len = pack.getLength();
          if (len <= 4) continue;  //keep alive
          String msg[] = new String(data, 0, len).replaceAll("\r", "").split("\n", -1);
          String remote = pack.getAddress().getHostAddress();
          if (server) {
            WorkerPacket wp = new WorkerPacket(msg, remote, pack.getPort());
            wp.start();
          } else {
            iface.packet(msg, remote, pack.getPort());
          }
        } catch (SocketException e) {
          if (active) {
            JFLog.log(e);
          }
        } catch (Exception e) {
          JFLog.log(e);
        }
      }
    }
  }

  /** This thread dispatches SIP packets in a seperate thread for server mode. */

  private class WorkerPacket extends Thread {
    String x1[];
    String x2;
    int x3;
    public WorkerPacket(String x1[], String x2, int x3) {
      this.x1 = x1;
      this.x2 = x2;
      this.x3 = x3;
    }
    public void run() {
      iface.packet(x1,x2,x3);
    }
  }
}

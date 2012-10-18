package javaforce.voip;

import javaforce.JFLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/** Handles sending/receiving RTP packets. */

public class RTP {
  private static int nextlocalrtpport = 32768;
  private static int rtpmin = 32768;
  private static int rtpmax = 65536;
  protected DatagramSocket sock1, sock2;
  private Worker worker1, worker2;  //inbound Workers
  private int localrtpport;
  protected volatile boolean active = false;
  private boolean used;
  protected String remoteip;
  protected int remoteport;
  private volatile boolean hold = false;
  private final int bufcnt = 12;
  private short bufs[][] = new short[bufcnt][0];
  private boolean bufFull[] = new boolean[bufcnt];
  private int bufHead = 0;
  private int bufTail = 0;
  private DTMF dtmf = new DTMF();
  private Object lockBuffers = new Object();
  protected Object lockHostPort = new Object();
  private short silence[] = new short[160];
  private RTPInterface iface;
  private int mtu = 1500;  //max size of packet
  private RTPChannel rtpChannel;
  private boolean rawMode;
//  private Codec codecs[];
  private int rfc2833_id = -1;
  private int h264_id = -1;

  public Object rtmp;  //used by RTMP2SIPServer
  public Object userobj;  //free to use

  public static boolean debug = false;  //set to true and recompile to get a lot of output

  public final static Codec CODEC_UNKNOWN = new Codec("?", -1);
  public final static Codec CODEC_G711u   = new Codec("PCMU", 0);  //g711u
//  public final static Codec CODEC_G711a   = new Codec("PCMA", 8);  //g711a : currently not supported
  public final static Codec CODEC_G729a   = new Codec("G729", 18);
  public final static Codec CODEC_H263    = new Codec("H263", 34);
  public final static Codec CODEC_RFC2833 = new Codec("telephone-event", 101);  //dynamic : usually 100 or 101
  public final static Codec CODEC_H264    = new Codec("H264", 125);  //dynamic : usually 99 or 125

  public Coder coder_g711u, coder_g729a;
  public Coder coder;  //selected audio encoder

  /** Returns a packet of decoded samples. */

  public boolean getSamples(short data[]) {
    synchronized (lockBuffers) {
      if (debug) {
        int cnt = 0;
        for(int a=0;a<bufcnt;a++) {if (bufFull[a]) cnt++;}
        JFLog.log("RTP.getSamples() : cnt = " + cnt);
      }
      if (!bufFull[bufTail]) {
        if (debug) JFLog.log("err:RTP.getSamples():no data (g729a silence?)");
        return false;
      }
      System.arraycopy(bufs[bufTail], 0, data, 0, 160);
      bufFull[bufTail++] = false;
      if (bufTail == bufcnt) bufTail = 0;
    }
    return true;
  }

  private void setSamples(short data[]) {
    if (data == null) {if (debug) JFLog.log("err:RTP.setSamples:null");return;}
    if (data.length != 160) {if (debug) JFLog.log("err:RTP.setSamples:wrong size");return;}
    if (debug) JFLog.log("note:RTP.setSamples:ok");
    boolean full = false;
    synchronized (lockBuffers) {
      if (bufFull[bufHead]) full = true;
      bufs[bufHead] = data;
      bufFull[bufHead] = true;
      bufHead++;
      if (bufHead == bufcnt) bufHead = 0;
      if (full) {
        if (debug) JFLog.log("note:RTP.setSamples: Buffers were full! Discarded one!");
        bufTail++;
        if (bufTail == bufcnt) bufTail = 0;
      }
    }
  }

  public static synchronized int getnextlocalrtpport() {
    int ret = nextlocalrtpport;
    nextlocalrtpport += 2;
    if (nextlocalrtpport+1 > rtpmax) nextlocalrtpport = rtpmin;
    return ret;
  }

  public int getlocalrtpport() {
    return localrtpport;
  }

  public boolean init(RTPInterface iface) {
    this.iface = iface;
    do {
      try {
        localrtpport = getnextlocalrtpport();
        sock1 = new DatagramSocket(localrtpport);
        sock2 = new DatagramSocket(localrtpport+1);
      } catch (Exception e2) {
        continue;
      }
      break;
    } while (true);
    return true;
  }

  public boolean init(RTPInterface iface, DatagramSocket sock1, DatagramSocket sock2) {
    this.iface = iface;
    this.sock1 = sock1;
    this.sock2 = sock2;
    return true;
  }

  /** Sets maximum packet size.  Must call before start(). (default = 1500) */
  public void setMTU(int mtu) {
    if (active) return;
    this.mtu = mtu;
  }

  /** Starts RTP session. */

  public synchronized boolean start(String remote, int remoteport, Codec codecs[], boolean video) {
//    this.codecs = codecs;
    coder = null;
    coder_g711u = new g711(this);
    coder = coder_g711u;
    if ((codecs != null) && (codecs.length > 0)) {
      Codec codec_rfc2833 = SIP.getCodec(codecs, CODEC_RFC2833);
      if (codec_rfc2833 != null) rfc2833_id = codec_rfc2833.id;
      Codec codec_h264 = SIP.getCodec(codecs, CODEC_H264);
      if (codec_h264 != null) h264_id = codec_h264.id;
      coder_g711u = new g711(this);
      coder_g729a = new g729a(this);
      JFLog.log("RTP.start() : localhost:" + localrtpport + " remote=" + remote + ":" + remoteport + ":rfc2833_id=" + rfc2833_id);
      if (!video) {
        if (SIP.hasCodec(codecs, CODEC_G711u)) {
          coder = coder_g711u;
          JFLog.log("codec = g711u");
          try { sock1.setSendBufferSize(12 + 160); } catch (Exception e) {}
        } else if (SIP.hasCodec(codecs, CODEC_G729a)) {
          JFLog.log("codec = g729a");
          coder = coder_g729a;
          try { sock1.setSendBufferSize(12 + 20); } catch (Exception e) {}
        } else {
          JFLog.log("RTP.start() : Warning : no compatible audio codec selected");
        }
      } else {
        if (SIP.hasCodec(codecs, CODEC_H263)) {
          JFLog.log("codec = H.263");
        } else if (SIP.hasCodec(codecs, CODEC_H264)) {
          JFLog.log("codec = H.264");
        } else {
          JFLog.log("RTP.start() : Warning : no compatible video codec selected");
        }
      }
    }
    //TODO : send something on localrtpport+1 (RTCP)
    remoteip = SIP.resolve(remote);
    this.remoteport = remoteport;
    if (active) return true;
    active = true;
    worker1 = new Worker(sock1, false);
    worker1.start();
    worker2 = new Worker(sock2, true);
    worker2.start();
    return true;
  }

  /** Sets raw mode<br>
    Packets are not decoded, they are passed directly thru RTPInterface.rtpPacket()<br>
    This is used by PBX to relay RTP packets between call originator and terminator.<br>
  */

  public void setRawMode(boolean state) {
    this.rawMode = state;
  }

  /** Returns current raw mode */

  public boolean getRawMode() {
    return rawMode;
  }

  /** Changes the remotehost/port for this RTP session.  Could occur in a SIP reINVITE. */

  public boolean change(String remote, int remoteport) {
    synchronized (lockHostPort) {
      try {
        remoteip = SIP.resolve(remote);
      } catch (Exception e) {
        return false;
      }
      this.remoteport = remoteport;
    }
    return true;
  }

  /** Changes codecs.  Could occur in a SIP reINVITE. */

  public boolean change(Codec codecs[]) {
    if (SIP.hasCodec(codecs, CODEC_G711u)) {
//      try { sock1.setSendBufferSize(12 + 160); } catch (Exception e) {}  //crashes on reINVITE?
      coder = coder_g711u;
    } else if (SIP.hasCodec(codecs, CODEC_G729a)) {
//      try { sock1.setSendBufferSize(12 + 20); } catch (Exception e) {}  //crashes on reINVITE?
      coder = coder_g729a;
    }
    return true;
  }

  /** Stops RTP session and frees resources. */

  public void stop() {
    if (!active) return;
    active = false;
    closeSockets();
    freeWorkers();
  }

  private void freeWorkers() {
    if (worker1 != null) {
      try { worker1.join(); } catch (Exception e1) {}
      worker1 = null;
    }
    if (worker2 != null) {
      try { worker2.join(); } catch (Exception e2) {}
      worker2 = null;
    }
  }

  /** Frees resources. */

  public void uninit() {
    stop();
    closeSockets();
    freeWorkers();
  }

  private void closeSockets() {
    if (sock1 != null) {
      try {
        sock1.close();
      } catch (Exception e) { }
      sock1 = null;
    }
    if (sock2 != null) {
      try {
        sock2.close();
      } catch (Exception e) { }
      sock2 = null;
    }
  }

  /** Places RTP session in hold state. */

  public void hold(boolean state) {hold = state;}

  /** Returns if RTP session is on hold. */

  public boolean getHold() {return hold;}

  /** Create a new RTP channel with a random ssrc id. */

  public RTPChannel createChannel() {
    return new RTPChannel(this, -1);
  }

  /** Create a new RTP channel with a specified ssrc id. */

  public RTPChannel createChannel(int ssrc) {
    return new RTPChannel(this, ssrc);
  }

  /** Returns default RTP channel. */

  public synchronized RTPChannel getDefaultChannel() {
    if (rtpChannel == null) rtpChannel = createChannel();
    return rtpChannel;
  }

  /** Sets global RTP port range to use (should be set before during init). */

  public static void setPortRange(int min, int max) {
    rtpmin = min;
    rtpmax = max;
    nextlocalrtpport = min;
  }

  /** Reads inbound packets for RTP session. */

  private class Worker extends Thread {
    private DatagramSocket sock;
    private boolean rtcp;
    private char dtmfChar;
    private boolean dtmfSent = false;
    public Worker(DatagramSocket sock,boolean rtcp) {
      this.sock = sock;
      this.rtcp = rtcp;
    }
    public void run() {
      byte data[] = new byte[mtu];
      while (active) {
        try {
          DatagramPacket pack = new DatagramPacket(data, mtu);
          sock.receive(pack);
          int len = pack.getLength();
          if (len < 12) continue;
          String remote = pack.getAddress().getHostAddress();
          if (!rtcp) {
            if (remoteport == -1) {
              remoteport = pack.getPort();  //NATing
              JFLog.log("RTP : NAT port = " + remoteport);
            }
            //TODO : validate remote.substring(idx+1), pack.getPort() are from trusted source
            if (rawMode) {
              iface.rtpPacket(RTP.this, false, data, 0, len);
              continue;
            }
            int id = data[1] & 0x7f;  //payload id
            if (id < 96) {
              switch (id) {
                case 0:
                  dtmfSent = false;  //just in case end of dtmf was not received
                  setSamples(coder_g711u.decode(data, 0, len));
                  iface.rtpSamples(RTP.this);
                  break;
                case 18:
                  dtmfSent = false;  //just in case end of dtmf was not received
                  setSamples(coder_g729a.decode(data, 0, len));
                  iface.rtpSamples(RTP.this);
                  break;
                case 34:
                  iface.rtpH263(RTP.this, data, 0, len);
                  break;
              }
            } else {
              if (id == rfc2833_id) {
                dtmfChar = ' ';
                if ((data[12] >= 0) && (data[12] <= 9)) dtmfChar = (char)('0' + data[12]);
                if (data[12] == 10) dtmfChar = '*';
                if (data[12] == 11) dtmfChar = '#';
                if (data[13] < 0) {   //0x80 == end of dtmf
                  dtmfSent = false;
                  dtmfChar = ' ';
                }
                if (dtmfChar == ' ') {
                  setSamples(silence);
                } else {
                  setSamples(dtmf.getSamples(dtmfChar));
                  if (!dtmfSent) {
                    iface.rtpDigit(RTP.this, dtmfChar);
                    dtmfSent = true;
                  }
                }
              } else if (id == h264_id) {
                iface.rtpH264(RTP.this, data, 0, len);
              }
            }
          } else {
            if (rawMode) {
              iface.rtpPacket(RTP.this, true, data, 0, len);
            }
            //TODO : RTCP ???
            break;
          }
        } catch (SocketException e) {
          if (active) {
            JFLog.log(e);
          }
          active = false;
        } catch (Exception e) {
          JFLog.log(e);
          active = false;
        }
      }
    }
  }
}

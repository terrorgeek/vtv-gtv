package javaforce.voip;

/*
struct rtp_header {
  byte version;  //usually 0x80
  byte type;  //0x12 (18)
  short seqnum;
  int timestamp;
  int syncsrcid;
};
*/

import javaforce.JFLog;
import javaforce.codec.g729a.Decoder;
import javaforce.codec.g729a.Encoder;

/** Encodes/decodes g729a packets. */

public class g729a implements Coder {
  private Encoder encoder = new Encoder();
  private Decoder decoder = new Decoder();
  private RTP rtp;

  public g729a(RTP rtp) {
    this.rtp = rtp;
  }

  //len should be # bytes not samples
  public byte[] encode(short samples[], int off, int len) {
    RTPChannel rtpChannel = rtp.getDefaultChannel();
    byte[] encoded = new byte[len / 80 * 10 + 12];
    rtpChannel.buildHeader(encoded, 18, rtpChannel.getseqnum(), rtpChannel.gettimestamp(20), rtpChannel.getssrc());
    encoder.encode(encoded, 12, samples, off, len / 80);
    return encoded;
  }

  private int getuint32(byte[] data, int offset) {
    int ret;
    ret  = (int)data[offset+3] & 0xff;
    ret += ((int)data[offset+2] & 0xff) << 8;
    ret += ((int)data[offset+1] & 0xff) << 16;
    ret += ((int)data[offset+0] & 0xff) << 24;
    return ret;
  }

  private int decode_timestamp;

  public short[] decode(byte encoded[], int off, int len) {
    if (len < 12) {
      if (RTP.debug) JFLog.log("G729a:packet too small");
      return null;
    }
    int decode_timestamp = getuint32(encoded, 4);
    if (this.decode_timestamp == 0) {
      this.decode_timestamp = decode_timestamp;
    } else {
      if (RTP.debug) JFLog.log("G729a:timestamp = " + decode_timestamp + ":" + ((this.decode_timestamp + 160 == decode_timestamp) ? "ok" : "lost packet"));
      this.decode_timestamp = decode_timestamp;
    }
    short[] samples = new short[(len - 12) / 10 * 80];
    decoder.decode(samples, 0, encoded, 12, (len - 12) / 10);
    return samples;
  }
}

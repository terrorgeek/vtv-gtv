package javaforce.voip;

/*
struct rtp_header {
  byte version;  //usually 0x80
  byte type;  //0x00
  short seqnum;
  int timestamp;
  int syncsrcid;
};
*/

import javaforce.JFLog;

/** Encodes/decodes g711 packets. */

public class g711 implements Coder {
  private RTP rtp;

  public g711(RTP rtp) {
    this.rtp = rtp;
  }

  private static short ulaw_lut[];  //signed
  private static short etab[]={0,132,396,924,1980,4092,8316,16764};
  private static int exp_lut[] = {
    0,0,1,1,2,2,2,2,3,3,3,3,3,3,3,3,
    4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,
    5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,
    5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,
    6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
    6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
    6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
    6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
  };

  public static void init() {
    short mu,e,f,y;
    ulaw_lut = new short[256];
    for (short i = 0;i < 256;i++) {
      mu = (short)(255-i);
      e = (short)((mu & 0x70)/16);
      f = (short)(mu & 0x0f);
      y = (short)(f * (1 << (e + 3)));
      y += etab[e];
      if ((mu & 0x80) == 0x80) y = (short)-y;
      ulaw_lut[i] = y;
    }
  }

  private static final short BIAS = 0x84;
  private static final short CLIP = 32635;  //not MAX short

  //len should be # bytes not samples
  public byte[] encode(short samples[], int off, int len) {
    byte encoded[] = new byte[len + 12];
    int sign, exponent, mantissa;
    byte ulawbyte;
    short sample;
    RTPChannel rtpChannel = rtp.getDefaultChannel();

    rtpChannel.buildHeader(encoded, 0, rtpChannel.getseqnum(), rtpChannel.gettimestamp(20), rtpChannel.getssrc());

    for(int i = 0;i < len;i++) {
      /* Get the sample into sign-magnitude. */
      sample = samples[off+i];
      sign = (sample >> 8) & 0x80;          /* set aside the sign */
      if (sign != 0) sample = (short)-sample;      /* get magnitude */
      if (sample > CLIP) sample = CLIP;     /* clip the magnitude */
      /* Convert from 16 bit linear to ulaw. */
      sample += BIAS;
      exponent = exp_lut[(sample >> 7) & 0xff];
      mantissa = ((sample >> (exponent + 3)) & 0x0f);
      ulawbyte = (byte)(~(sign | (exponent << 4) | mantissa));
      if (ulawbyte == 0) ulawbyte = 0x02;   /* optional CCITT trap */
      encoded[12+i] = ulawbyte;
    }
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
      if (RTP.debug) JFLog.log("G711:packet too small");
      return null;
    }
    int decode_timestamp = getuint32(encoded, 4);
    if (this.decode_timestamp == 0) {
      this.decode_timestamp = decode_timestamp;
    } else {
      if (RTP.debug) JFLog.log("G711:timestamp = " + decode_timestamp + ":" + ((this.decode_timestamp + 160 == decode_timestamp) ? "ok" : "lost packet"));
      this.decode_timestamp = decode_timestamp;
    }
    short dest16[] = new short[len - 12];
    //skip first 12 bytes (RTP header)
    for(int i = 12;i < len;i++) {
      dest16[i-12] = ulaw_lut[encoded[off+i] & 0xff];
    }
    return dest16;
  }
}

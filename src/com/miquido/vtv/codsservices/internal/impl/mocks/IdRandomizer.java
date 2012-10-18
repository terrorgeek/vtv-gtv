package com.miquido.vtv.codsservices.internal.impl.mocks;

import com.miquido.vtv.bo.Id;

import java.util.Random;

/**
  938690c3-dd06-0282-ec57-50193b78f3be
 */
public class IdRandomizer {

    Random random = new Random();

    public Id getNewId() {
        StringBuilder sb = new StringBuilder();
        randomizeBytesHex(sb, 4);
        sb.append("-");
        randomizeBytesHex(sb, 2);
        sb.append("-");
        randomizeBytesHex(sb, 2);
        sb.append("-");
        randomizeBytesHex(sb, 2);
        sb.append("-");
        randomizeBytesHex(sb, 6);

        return Id.valueOf(sb.toString());
    }

    protected String randomizeByteHex() {
        return String.format("%02x", random.nextInt(256));
    }

    protected void randomizeBytesHex(StringBuilder sb, int n) {
        for (int i = 0; i < n; i++) {
            sb.append(randomizeByteHex());
        }
    }

}

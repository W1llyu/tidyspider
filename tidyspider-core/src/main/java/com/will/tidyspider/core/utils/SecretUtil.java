package com.will.tidyspider.core.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by will on 02/01/2018.
 */
public abstract class SecretUtil {
    public static String md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String base64Decode(String str, String charCode) {
        try {
            return new String(new BASE64Decoder().decodeBuffer(str), charCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String base64Encode(byte[] bstr) {
        return new BASE64Encoder().encode(bstr);
    }
}

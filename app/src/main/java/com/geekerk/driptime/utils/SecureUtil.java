package com.geekerk.driptime.utils;

import android.util.Base64;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA1摘要，Base64加密解密
 * Created by s21v on 2016/6/6.
 */
public class SecureUtil {
    public static String encodeBase64(String input) {
        byte[] input_sha = SHA1(input);
        if (input_sha != null) {
            return new String(Base64.encode(input_sha, Base64.DEFAULT), Charset.forName("utf-8"));
        } else
            return null;
    }

    public static String decodeBase64(String input) {
        if (input != null) {
            return new String(Base64.decode(input, Base64.DEFAULT), Charset.forName("utf-8"));
        } else
            return null;
    }

    public static byte[] SHA1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(input.getBytes());
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}

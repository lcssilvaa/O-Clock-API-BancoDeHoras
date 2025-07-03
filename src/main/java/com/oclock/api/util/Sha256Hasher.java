package com.oclock.api.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class Sha256Hasher {

    public static String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    public static boolean verify(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null) {
            return false;
        }
        String hashedInputPassword = hash(plainTextPassword);
        return hashedInputPassword.equals(storedHash);
    }
}
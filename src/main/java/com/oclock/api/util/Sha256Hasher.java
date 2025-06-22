package com.oclock.api.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class Sha256Hasher {

    /**
     * Gera o hash SHA-256 de uma string.
     *
     * @param text O texto (senha em texto puro) a ser hashed.
     * @return O hash SHA-256 em formato hexadecimal.
     * @throws RuntimeException se o algoritmo SHA-256 não for encontrado (altamente improvável).
     */
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

    /**
     * Verifica se uma senha em texto puro corresponde a um hash SHA-256 armazenado.
     *
     * @param plainTextPassword A senha em texto puro fornecida (ex: no login).
     * @param storedHash O hash SHA-256 armazenado no banco de dados.
     * @return true se o hash da senha em texto puro corresponder ao hash armazenado, false caso contrário.
     */
    public static boolean verify(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null) {
            return false;
        }
        String hashedInputPassword = hash(plainTextPassword);
        return hashedInputPassword.equals(storedHash);
    }
}
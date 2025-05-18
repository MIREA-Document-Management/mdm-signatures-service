package ru.mdm.signatures.util;

import ru.mdm.signatures.exception.ServerException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private static SecretKey getSecretKey(String secretKey) {
        return new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
    }

    public static String encrypt(String plainText, String secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(secretKey));
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new ServerException("Failed to encrypt", e);
        }
    }

    public static String decrypt(String base64CipherText, String secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secretKey));
            byte[] decoded = Base64.getDecoder().decode(base64CipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new ServerException("Failed to decrypt", e);
        }
    }
}

package org.waterwood.waterfunservice.utils;

import org.waterwood.waterfunservice.entity.User.EncryptionDataKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

public class EncryptUtil {
    public static SecretKey getKEKFromEnv() {
        String kekBase64 = System.getenv("WATERFUN_KEK");
        if (kekBase64 == null) throw new RuntimeException("KEK System Environment Variable is not set");
        byte[] kekBytes = Base64.getDecoder().decode(kekBase64);
        return new SecretKeySpec(kekBytes, "AES");
    }

    public static List<EncryptionDataKey> generateAndEncryptDEKs(int count) throws Exception {
        SecretKey kek = getKEKFromEnv();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, kek);

        List<EncryptionDataKey> dekList = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            String dekId = "dek-" + java.util.UUID.randomUUID().toString();
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // Use 256-bit AES keys
            SecretKey dek = keyGenerator.generateKey();

            byte[] encryptedDek = cipher.doFinal(dek.getEncoded());
            EncryptionDataKey dekKey = new EncryptionDataKey();
            dekKey.setId(dekId);
            dekKey.setEncryptedKey(Base64.getEncoder().encodeToString(encryptedDek));
            dekKey.setAlgorithm("AES");
            dekKey.setKeyLength(256);
            dekKey.setCreatedAt(Instant.now());
            dekKey.setDescription("Auto Generated DEK with index: " + i);

            dekList.add(dekKey);
        }
        return dekList;
    }

    public static SecretKey decryptDEK(EncryptionDataKey dekKey) throws Exception {
        SecretKey kek = getKEKFromEnv();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, kek);

        byte[] encryptedDekBytes = Base64.getDecoder().decode(dekKey.getEncryptedKey());
        byte[] decryptedDekBytes = cipher.doFinal(encryptedDekBytes);

        return new SecretKeySpec(decryptedDekBytes, "AES");
    }

    /**
     * Decrypts a field that was encrypted with a DEK
     * @param encryptedFieldBase64 base64 encoded encrypted field
     * @param dekKey DEK key used to decrypt the field
     * @return decrypted field as a String
     * @throws Exception Encryption errors
     */
    public static String decryptField(String encryptedFieldBase64, EncryptionDataKey dekKey) throws Exception {
        SecretKey dek = decryptDEK(dekKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, dek);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedFieldBase64);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

    /**
     * Return base64 encoded encrypted field
     * @param field raw field to encrypt
     * @param dekKey DEK key used to encrypt the field
     * @return base64 encoded encrypted field
     * @throws Exception Encryption errors
     */
    public static String encryptField(String field, EncryptionDataKey dekKey) throws Exception {
        SecretKey dek = decryptDEK(dekKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, dek);

        byte[] fieldBytes = field.getBytes();
        byte[] encryptedBytes = cipher.doFinal(fieldBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}

package org.waterwood.waterfunservice.utils.security;

import org.waterwood.waterfunservice.entity.security.EncryptionDataKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

public class KeyEncryptionHelper {
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    
    public static SecretKey getKEKFromEnv() throws Exception{
        String kekBase64 = System.getenv("WATERFUN_KEK");
        if (kekBase64 == null) {
            throw new RuntimeException("KEK System Environment Variable is not set");
        }
        byte[] kekBytes = Base64.getDecoder().decode(kekBase64);
        return new SecretKeySpec(kekBytes, "AES");
    }

    public static List<EncryptionDataKey> generateAndEncryptDEKs(int count) throws Exception {
        SecretKey kek = getKEKFromEnv();
        List<EncryptionDataKey> dekList = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            // Generate DEK
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // Use 256-bit AES keys
            SecretKey dek = keyGenerator.generateKey();

            // Random iv
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            // Encrypt Data
            Cipher cipher = Cipher.getInstance(AES_GCM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, dek, parameterSpec);
            byte[] encryptedDek = cipher.doFinal(dek.getEncoded());
            // Combined IV and Encrypted data
            byte[] combined = new byte[iv.length + encryptedDek.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedDek, 0, combined, iv.length, encryptedDek.length);
            // Create DEK record
            EncryptionDataKey dekKey = new EncryptionDataKey();
            String dekId = "dek-" + java.util.UUID.randomUUID();
            dekKey.setId(dekId);
            dekKey.setEncryptedKey(Base64.getEncoder().encodeToString(combined));
            dekKey.setAlgorithm("AES");
            dekKey.setKeyLength(256);
            dekKey.setCreatedAt(Instant.now());
            dekKey.setDescription("Auto Generated DEK #" + i + 1);

            dekList.add(dekKey);
        }
        return dekList;
    }

    public static SecretKey decryptDEK(EncryptionDataKey dekKey) throws Exception {
        SecretKey kek = getKEKFromEnv();
        // Decrypt Base64
        byte[] combined = Base64.getDecoder().decode(dekKey.getEncryptedKey());

        // Divide into IV,Encrypted Data
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedDek = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(combined, GCM_IV_LENGTH, encryptedDek, 0, encryptedDek.length);

        // Decrypt
        Cipher cipher = Cipher.getInstance(AES_GCM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, kek, spec);
        byte[] decryptedDekBytes = cipher.doFinal(encryptedDek);

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

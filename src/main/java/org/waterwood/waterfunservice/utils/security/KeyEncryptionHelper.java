package org.waterwood.waterfunservice.utils.security;

import org.waterwood.waterfunservice.entity.security.EncryptionDataKey;
import org.waterwood.waterfunservice.entity.security.KeyStatus;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

/**
 * A Class to help data with symmetric key encryption and decryption
 * must set up kek env in your system
 * @since 1.0
 * @author Danburen
 * @version 1.0
 */
public class KeyEncryptionHelper {
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    
    public static SecretKey getKEKFromEnv() throws Exception{
        String kekBase64 = System.getenv("WATERFUN_KEK");
        if (kekBase64 == null) {
            throw new RuntimeException("KEK System Environment Variable is not set.");
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
            // Encrypt DEK with KEK
            byte[] combined = encryptWithGCM(dek.getEncoded(),kek);
            // Create DEK record
            EncryptionDataKey dekKey = new EncryptionDataKey();
            String dekId = "dek-" + java.util.UUID.randomUUID();
            dekKey.setId(dekId);
            dekKey.setEncryptedKey(Base64.getEncoder().encodeToString(combined));
            dekKey.setAlgorithm("AES");
            dekKey.setKeyLength(256);
            dekKey.setCreatedAt(Instant.now());
            dekKey.setKeyStatus(KeyStatus.PENDING_ACTIVATION);
            dekKey.setDescription("Auto Generated DEK #" + i + 1);

            dekList.add(dekKey);
        }
        return dekList;
    }

    public static SecretKey decryptDEK(EncryptionDataKey dekKey) throws Exception {
        SecretKey kek = getKEKFromEnv();
        // Decrypt Base64
        byte[] combined = Base64.getDecoder().decode(dekKey.getEncryptedKey());
        byte[] decryptedDekBytes = decryptWithGCM(combined,kek);
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
        byte[] combined = Base64.getDecoder().decode(encryptedFieldBase64);
        return new String(decryptWithGCM(combined,dek), StandardCharsets.UTF_8);
    }

    /**
     * Return base64 encoded encrypted field
     * @param field raw field to encrypt
     * @param dekKey DEK key used to encrypt the field
     * @return base64 encoded encrypted field
     * @throws Exception Encryption errors
     */
    public static String encryptField(String field, EncryptionDataKey dekKey) throws Exception {
        SecretKey dek =  decryptDEK(dekKey);
        return Base64.getEncoder().encodeToString(encryptWithGCM(field.getBytes(),dek));
    }

    /**
     * Use GCM_AES Type encryption cipher decrypt data
     * @param combined the combined encrypted data with init vector
     * @param key the encryption key for decryption
     * @return combined encrypted data.
     * @throws Exception exception
     */
    private static byte[] decryptWithGCM(byte[] combined, SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedData = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(combined, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

        Cipher cipher = Cipher.getInstance(AES_GCM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(encryptedData);
    }

    /**
     * Use GCM_AES Type encryption cipher encrypt data to encrypted data
     * @param original original of data
     * @param key key for encryption
     * @return combined encrypted data with init vector
     * @throws Exception exception
     */
    private static byte[] encryptWithGCM(byte[] original, SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_GCM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encryptedData = cipher.doFinal(original);
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        return combined;
    }
}

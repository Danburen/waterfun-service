package org.waterwood.waterfunservice.utils.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.waterwood.waterfunservice.utils.DataAdapter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class HashUtil {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private static final SecureRandom secureRandom = new SecureRandom();

    private static final ThreadLocal<MessageDigest> MD5_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

    private static final ThreadLocal<MessageDigest> SHA256_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

    private static final ThreadLocal<Mac> SHA256_HMAC = ThreadLocal.withInitial(() -> {
        try {
            return Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

    public static BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return bCryptPasswordEncoder;
    }

    public static String calculateHmac(String data, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            SHA256_HMAC.get().init(secretKey);
            byte[] hmacBytes = SHA256_HMAC.get().doFinal(data.getBytes(StandardCharsets.UTF_8));
            return DataAdapter.bytesToHex(hmacBytes);
        }catch (InvalidKeyException e){
            throw new RuntimeException(e);
        }
    }

    public static String getRandomSalt(int length) {
        byte[] saltBytes = new byte[length];
        secureRandom.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static String hashWithSalt(String original,String salt){
        byte[] hash = SHA256_DIGEST.get().digest(salt.concat(original).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String hashWithRandomSalt(String original){
        byte[] hash = SHA256_DIGEST.get()
                .digest(getRandomSalt(32)
                        .concat(original)
                        .getBytes(StandardCharsets.UTF_8)
                );
        return Base64.getEncoder().encodeToString(hash);
    }

    public static MessageDigest getSHA256Digest() {
        return SHA256_DIGEST.get();
    }

    public static MessageDigest getMD5Digest() {
        return MD5_DIGEST.get();
    }

    public static Mac getSHA256Hmac() {
        return SHA256_HMAC.get();
    }
}

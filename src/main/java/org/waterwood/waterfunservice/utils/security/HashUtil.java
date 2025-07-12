package org.waterwood.waterfunservice.utils.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.waterwood.waterfunservice.utils.DataAdapter;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return bCryptPasswordEncoder;
    }

    public static String calculateHmac(String data, String key) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            hmac.init(secretKey);
            byte[] hmacBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return DataAdapter.bytesToHex(hmacBytes);
        }catch (NoSuchAlgorithmException | InvalidKeyException e){
            throw new RuntimeException(e);
        }
    }
}

package org.waterwood.waterfunservice.utils.generator.impl;

import org.waterwood.waterfunservice.utils.generator.UidGenerator;
import org.waterwood.waterfunservice.utils.security.HashUtil;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class HashedTimeNumericUidGenerator implements UidGenerator {
    private static final long EPOCH = 1735689600000L; // GMT: 2025-01-01 00:00:00
    private static final int LENGTH = 16;
    private static final SecureRandom random = new SecureRandom();
    @Override
    public String generateUid(){
        return generateUid(LENGTH);
    }

    @Override
    public String generateUid(int length) {
        long timestamp = System.currentTimeMillis() - EPOCH;
        String input = timestamp + "-" + random.nextLong();

        byte[] hash = HashUtil.getSHA256Digest().digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        // Turn hashed bytes into a numeric string
        for (int i = 0; i < Math.min(length, hash.length); i++) {
            result.append(Math.abs(hash[i]) % 10);
        }
        // Use timestamp as fallback
        while (result.length() < length) {
            result.append(Math.abs(timestamp % 10));
            timestamp /= 10;
        }
        return result.substring(0, length);
    }
}

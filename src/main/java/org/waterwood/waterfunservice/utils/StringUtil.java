package org.waterwood.waterfunservice.utils;

import java.util.Arrays;
import java.util.Objects;

public class StringUtil {
    public static String[] noNullStringArray(String... strings) {
        return Arrays.stream(strings)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}

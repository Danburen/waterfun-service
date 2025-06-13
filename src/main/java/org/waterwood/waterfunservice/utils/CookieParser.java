package org.waterwood.waterfunservice.utils;

import jakarta.servlet.http.Cookie;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CookieParser {
    /**
     * Get target cookie value by key from a cookies array.
     * @param cookies cookies array
     * @param key target cookie key
     * @return the value of the target cookie, or null if not found
     */
    public @Nullable static String getCookieValue(Cookie[] cookies, String key) {
        return Arrays.stream(cookies)
                .filter(c->key.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}

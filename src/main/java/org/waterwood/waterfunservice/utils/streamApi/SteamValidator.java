package org.waterwood.waterfunservice.utils.streamApi;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class SteamValidator<T>{
    private boolean valid = true;
    private T result;

    private SteamValidator() {}

    public static <T> SteamValidator<T> start() {
        return new SteamValidator<>();
    }

    public SteamValidator<T> check(BooleanSupplier condition, Supplier<T> failResultSupplier) {
        if (valid && !condition.getAsBoolean()) {
            valid = false;
            result = failResultSupplier.get();
        }
        return this;
    }

    public T orElse(Supplier<T> successResultSupplier) {
        return valid ? successResultSupplier.get() : result;
    }
}

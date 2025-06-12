package org.waterwood.waterfunservice.utils.validator;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Validator<T>{
    private boolean valid = true;
    private T result;

    private Validator() {}

    public static <T> Validator<T> start() {
        return new Validator<>();
    }

    public Validator<T> check(BooleanSupplier condition, Supplier<T> failResultSupplier) {
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

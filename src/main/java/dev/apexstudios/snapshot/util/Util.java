package dev.apexstudios.snapshot.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Util {
    static <T> T make(Supplier<T> factory, Consumer<T> modifier) {
        var obj = factory.get();
        modifier.accept(obj);
        return obj;
    }
}

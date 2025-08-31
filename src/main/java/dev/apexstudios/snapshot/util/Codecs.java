package dev.apexstudios.snapshot.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.function.Function;

public interface Codecs {
    Codec<String> NON_BLANK_STRING = Codec.STRING.validate(str -> str.isBlank() ? DataResult.error(() -> "Expected non-blank string") : DataResult.success(str));
    Codec<Optional<String>> OPTIONAL_STRING = optional(NON_BLANK_STRING);

    static <T, I> Codec<T> forLookup(Codec<I> idCodec, Function<T, I> toId, Function<I, T> byId) {
        return idCodec.xmap(byId, toId);
    }

    static <T extends Enum<T>> Codec<T> forEnum(Class<T> enumType) {
        return forLookup(NON_BLANK_STRING, Enum::name, id -> {
            for(var val : enumType.getEnumConstants()) {
                if(id.equalsIgnoreCase(val.name()))
                    return val;
            }

            throw new IllegalArgumentException("Unknown enum constant: '" + id + "'");
        });
    }

    static <R> Codec<Optional<R>> optional(Codec<R> codec) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<Optional<R>, T>> decode(DynamicOps<T> ops, T input) {
                return isEmptyMap(ops, input) ? DataResult.success(Pair.of(Optional.empty(), input)) : codec.decode(ops, input).map(val -> val.mapFirst(Optional::of));
            }

            @Override
            public <T> DataResult<T> encode(Optional<R> input, DynamicOps<T> ops, T prefix) {
                return input.map(val -> codec.encode(val, ops, prefix)).orElseGet(() -> DataResult.success(ops.emptyMap()));
            }
        };
    }

    static <T> boolean isEmptyMap(DynamicOps<T> ops, T val) {
        var optional = ops.getMap(val).result();
        return optional.isPresent() && optional.get().entries().findAny().isEmpty();
    }

    static <T, R> R encode(DynamicOps<R> ops, Codec<T> codec, T val) {
        return codec.encodeStart(ops, val).getOrThrow();
    }
}

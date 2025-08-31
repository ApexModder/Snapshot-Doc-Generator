package dev.apexstudios.snapshot.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.apexstudios.snapshot.util.Codecs;
import java.util.Optional;

public record SnowMan(
        Optional<String> forgecraft,
        Optional<String> neoforge
) {
    public static final SnowMan EMPTY = new SnowMan(Optional.empty(), Optional.empty());

    public static final Codec<SnowMan> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.optionalField("forgecraft", Codecs.NON_BLANK_STRING, false).forGetter(SnowMan::forgecraft),
            Codec.optionalField("neoforge", Codecs.NON_BLANK_STRING, false).forGetter(SnowMan::neoforge)
    ).apply(builder, SnowMan::new));

    public Optional<String> get(boolean forgecraft) {
        return forgecraft ? forgecraft() : neoforge;
    }
}

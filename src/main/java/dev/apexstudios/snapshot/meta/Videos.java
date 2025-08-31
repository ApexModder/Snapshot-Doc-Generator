package dev.apexstudios.snapshot.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.apexstudios.snapshot.util.Codecs;
import java.util.Optional;

public record Videos(
        Optional<String> main,
        Optional<String> pack
) {
    public static final Videos EMPTY = new Videos(Optional.empty(), Optional.empty());

    public static final Codec<Videos> FULL_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.optionalField("main", Codecs.NON_BLANK_STRING, false).forGetter(Videos::main),
            Codec.optionalField("pack", Codecs.NON_BLANK_STRING, false).forGetter(Videos::pack)
    ).apply(builder, Videos::new));

    public static final Codec<Videos> SIMPLE_CODEC = Codecs.OPTIONAL_STRING.xmap(main -> new Videos(main, Optional.empty()), Videos::main);
    public static final Codec<Videos> CODEC = Codec.withAlternative(FULL_CODEC, SIMPLE_CODEC);
}

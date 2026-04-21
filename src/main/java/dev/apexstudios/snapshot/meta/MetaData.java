package dev.apexstudios.snapshot.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.apexstudios.snapshot.util.Codecs;
import java.util.Optional;

public record MetaData(
        Optional<String> articleOverride,
        Optional<String> drop,
        Optional<String> primer,
        SnowMan snowman,
        Videos videos
) {
    public static final Codec<MetaData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.optionalField("article_override", Codecs.NON_BLANK_STRING, false).forGetter(MetaData::articleOverride),
            Codec.optionalField("drop", Codecs.NON_BLANK_STRING, false).forGetter(MetaData::drop),
            Codec.optionalField("primer", Codecs.NON_BLANK_STRING, false).forGetter(MetaData::primer),
            SnowMan.CODEC.optionalFieldOf("snowman", SnowMan.EMPTY).forGetter(MetaData::snowman),
            Videos.CODEC.optionalFieldOf("videos", Videos.EMPTY).forGetter(MetaData::videos)
    ).apply(builder, MetaData::new));
}

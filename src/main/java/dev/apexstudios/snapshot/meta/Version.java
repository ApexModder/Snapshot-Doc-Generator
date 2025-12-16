package dev.apexstudios.snapshot.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.apexstudios.snapshot.util.AppContext;
import dev.apexstudios.snapshot.util.Codecs;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public record Version(
        String displayName,
        String id,
        String article,
        String changelog,
        Optional<String> primer,
        Optional<String> notion,
        SnowMan snowman,
        Videos videos,
        ReleaseType type,
        Optional<String> next,
        Optional<String> previous
) {
    public static final Codec<Version> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codecs.NON_BLANK_STRING.fieldOf("display_name").forGetter(Version::displayName),
            Codecs.NON_BLANK_STRING.fieldOf("id").forGetter(Version::id),
            Codecs.NON_BLANK_STRING.fieldOf("article").forGetter(Version::article),
            Codecs.NON_BLANK_STRING.fieldOf("changelog").forGetter(Version::changelog),
            Codec.optionalField("primer", Codecs.NON_BLANK_STRING, false).forGetter(Version::primer),
            Codec.optionalField("notion", Codecs.NON_BLANK_STRING, false).forGetter(Version::notion),
            SnowMan.CODEC.optionalFieldOf("snowman", SnowMan.EMPTY).forGetter(Version::snowman),
            Videos.CODEC.optionalFieldOf("videos", Videos.EMPTY).forGetter(Version::videos),
            ReleaseType.CODEC.fieldOf("type").forGetter(Version::type),
            Codec.optionalField("next", Codecs.NON_BLANK_STRING, false).forGetter(Version::next),
            Codec.optionalField("previous", Codecs.NON_BLANK_STRING, false).forGetter(Version::previous)
    ).apply(builder, Version::new));

    public Optional<Version> next(AppContext context) {
        return next.flatMap(context::findVersion);
    }

    public Optional<Version> previous(AppContext context) {
        return previous.flatMap(context::findVersion);
    }

    public Optional<String> primer(AppContext context) {
        return findTopMostProperty(context, this, Version::primer);
    }

    public String shortHand() {
        return type.shortHand(id);
    }

    public String cleanId() {
        return cleanId(id);
    }

    public static String cleanId(String id) {
        return StringUtils.replaceChars(id, '.', '-');
    }

    private static <T> Optional<T> findTopMostProperty(AppContext context, Version version, Function<Version, Optional<T>> mapper) {
        for(var next = version; next != null; next = next.next(context).orElse(null)) {
            var property = mapper.apply(next);

            if(property.isPresent())
                return property;
        }

        return Optional.empty();
    }
}

package dev.apexstudios.snapshot.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.apexstudios.snapshot.util.AppContext;
import dev.apexstudios.snapshot.util.Codecs;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public record Version(
        Optional<String> displayName,
        String id,
        Optional<String> article,
        Optional<String> changelog,
        Optional<String> primer,
        Optional<String> notion,
        SnowMan snowman,
        Videos videos,
        ReleaseType type,
        Optional<String> next,
        Optional<String> previous
) {
    public static final Codec<Version> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.optionalField("display_name", Codecs.NON_BLANK_STRING, false).forGetter(Version::displayName),
            Codecs.NON_BLANK_STRING.fieldOf("id").forGetter(Version::id),
            Codec.optionalField("article", Codecs.NON_BLANK_STRING, false).forGetter(Version::article),
            Codec.optionalField("changelog", Codecs.NON_BLANK_STRING, false).forGetter(Version::changelog),
            Codec.optionalField("primer", Codecs.NON_BLANK_STRING, false).forGetter(Version::primer),
            Codec.optionalField("notion", Codecs.NON_BLANK_STRING, false).forGetter(Version::notion),
            SnowMan.CODEC.optionalFieldOf("snowman", SnowMan.EMPTY).forGetter(Version::snowman),
            Videos.CODEC.optionalFieldOf("videos", Videos.EMPTY).forGetter(Version::videos),
            ReleaseType.CODEC.fieldOf("type").forGetter(Version::type),
            Codec.optionalField("next", Codecs.NON_BLANK_STRING, false).forGetter(Version::next),
            Codec.optionalField("previous", Codecs.NON_BLANK_STRING, false).forGetter(Version::previous)
    ).apply(builder, Version::new));

    @Override
    public Optional<String> article() {
        return article.or(() -> Optional.of(type.articleUrl(cleanId())));
    }

    @Override
    public Optional<String> changelog() {
        return changelog.or(() -> Optional.of(type.changelogUrl(id)));
    }

    public Optional<Version> next(AppContext context) {
        return next.flatMap(context::findVersion);
    }

    public Optional<Version> previous(AppContext context) {
        return previous.flatMap(context::findVersion);
    }

    public Optional<String> primer(AppContext context) {
        return findTopMostProperty(context, this, Version::primer);
    }

    public String headerName() {
        var prefix = type == ReleaseType.SNAPSHOT ? "Snapshot " : "";
        return prefix + displayName.orElse(id);
    }

    public String shortHand() {
        return type.shortHand(id);
    }

    public String cleanId() {
        return cleanId(id);
    }

    public static Version empty(String id, ReleaseType releaseType) {
        return new Version(
                Optional.empty(),
                id,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                SnowMan.EMPTY,
                Videos.EMPTY,
                releaseType,
                Optional.empty(),
                Optional.empty()
        );
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

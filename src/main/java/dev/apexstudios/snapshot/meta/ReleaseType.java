package dev.apexstudios.snapshot.meta;

import com.mojang.serialization.Codec;
import dev.apexstudios.snapshot.util.Codecs;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.StringUtils;

public enum ReleaseType {
    RELEASE("https://www.minecraft.net/en-us/article/minecraft-java-edition-{id}"),
    RELEASE_CANDIDATE("https://www.minecraft.net/en-us/article/minecraft-{id}", id -> StringUtils.replace(id, "release-candidate-", "rc")),
    PRE_RELEASE("https://www.minecraft.net/en-us/article/minecraft-{id}", id -> StringUtils.replace(id, "pre-release-", "pre")),
    SNAPSHOT("https://www.minecraft.net/en-us/article/minecraft-snapshot-{id}");

    public static final Codec<ReleaseType> CODEC = Codecs.forEnum(ReleaseType.class);

    private final String articleUrl;
    private final UnaryOperator<String> shortHand;

    ReleaseType(String articleUrl, UnaryOperator<String> shortHand) {
        this.articleUrl = articleUrl;
        this.shortHand = shortHand;
    }

    ReleaseType(String articleUrl) {
        this(articleUrl, UnaryOperator.identity());
    }

    public String articleUrl(String cleanId) {
        return StringUtils.replace(articleUrl, "{id}", cleanId);
    }

    public String changelogUrl(String id) {
        var shortHand = shortHand(id);
        return "https://misode.github.io/versions/?id=" + shortHand;
    }

    public String shortHand(String id) {
        return shortHand.apply(id);
    }
}

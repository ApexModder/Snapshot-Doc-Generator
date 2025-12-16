package dev.apexstudios.snapshot.meta;

import com.mojang.serialization.Codec;
import dev.apexstudios.snapshot.util.Codecs;
import java.util.function.UnaryOperator;
import org.apache.commons.lang3.StringUtils;

public enum ReleaseType {
    RELEASE(UnaryOperator.identity()),
    RELEASE_CANDIDATE(id -> StringUtils.replace(id, "release-candidate-", "rc")),
    PRE_RELEASE(id -> StringUtils.replace(id, "pre-release-", "pre")),
    SNAPSHOT(UnaryOperator.identity());

    public static final Codec<ReleaseType> CODEC = Codecs.forEnum(ReleaseType.class);

    private final UnaryOperator<String> shortHand;

    ReleaseType(UnaryOperator<String> shortHand) {
        this.shortHand = shortHand;
    }

    public String shortHand(String id) {
        return shortHand.apply(id);
    }
}

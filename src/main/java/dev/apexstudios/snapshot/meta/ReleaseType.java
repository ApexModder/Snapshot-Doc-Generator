package dev.apexstudios.snapshot.meta;

import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public final class ReleaseType {
    public static final ReleaseType RELEASE = ReleaseType.builder()
            .article(id -> "minecraft-java-edition-" + id)
            .build("release");

    public static final ReleaseType RELEASE_CANDIDATE = ReleaseType.builder()
            .article(id -> "minecraft-" + id)
            .shortHand(id -> StringUtils.replace(id, "release-candidate-", "rc"))
            .displayName(id -> extractVersionAndBuild(id, "Release Candidate"))
            .build("release-candidate");

    public static final ReleaseType PRE_RELEASE = ReleaseType.builder()
            .article(id -> "minecraft-" + id)
            .shortHand(id -> StringUtils.replace(id, "pre-release-", "pre"))
            .displayName(id -> extractVersionAndBuild(id, "Pre Release"))
            .build("pre-release");

    public static final ReleaseType SNAPSHOT = ReleaseType.builder()
            .article(id -> "minecraft-" + id)
            .displayName(id -> "Snapshot " + id)
            .build("snapshot");

    public static final ReleaseType SNAPSHOT_LEGACY = ReleaseType.builder()
            .article(id -> "minecraft-snapshot-" + id)
            .displayName(id -> "Snapshot " + id)
            .build("snapshot-legacy");

    public static final ReleaseType APRIL_FOOLS = ReleaseType.builder().build("april-fools");

    private static final Pattern RELEASE_FORMAT = Pattern.compile("\\d+\\.\\d+(?:\\.\\d+)?");
    private static final Pattern RELEASE_CANDIDATE_FORMAT = Pattern.compile(RELEASE_FORMAT.pattern() + "(?:-release-candidate-|-rc-?)\\d+");
    private static final Pattern PRE_RELEASE_FORMAT = Pattern.compile(RELEASE_FORMAT.pattern() + "(?:-pre-release-|-pre-?)\\d+");
    private static final Pattern SNAPSHOT_FORMAT = Pattern.compile(RELEASE_FORMAT.pattern() + "(-snapshot-)\\d+");
    private static final Pattern OLD_SNAPSHOT_FORMAT = Pattern.compile("(\\d{2})w\\d{2}[a-z]");

    private final String serializedName;
    private final UnaryOperator<String> shortHand;
    private final UnaryOperator<String> displayName;
    private final @Nullable UnaryOperator<String> article;

    private ReleaseType(String serializedName, Builder builder) {
        this.serializedName = serializedName;

        shortHand = builder.shortHand;
        displayName = builder.displayName;
        article = builder.article;
    }

    public String shortHand(Version version) {
        return shortHand.apply(version.id());
    }

    public String displayName(Version version) {
        return displayName.apply(version.id());
    }

    public String article(Version version) {
        // https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1-2
        // https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-5

        // https://www.minecraft.net/en-us/article/minecraft-26-2-snapshot-4
        // https://www.minecraft.net/en-us/article/minecraft-snapshot-25w05a

        // https://www.minecraft.net/en-us/article/minecraft-26-1-2-release-candidate-1
        // https://www.minecraft.net/en-us/article/minecraft-1-21-5-release-candidate-1

        // https://www.minecraft.net/en-us/article/minecraft-26-1-pre-release-3
        // https://www.minecraft.net/en-us/article/minecraft-1-21-5-pre-release-1

        // https://www.minecraft.net/en-us/article/the-herdcraft-update

        return "https://www.minecraft.net/en-us/article/" + Objects.requireNonNull(article).apply(version.cleanId());
    }

    public String changelog(Version version) {
        return "https://misode.github.io/versions/?id=" + version.shortHand();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj instanceof ReleaseType other) {
            return serializedName.equals(other.serializedName);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return serializedName.hashCode();
    }

    @Override
    public String toString() {
        return serializedName;
    }

    private static String extractVersionAndBuild(String id, String human) {
        var version = id.substring(0, id.indexOf('-'));
        var build = id.substring(id.lastIndexOf('-'));
        return version + " - " + human + " " + build;
    }

    public static ReleaseType from(String id) {
        var oldMatcher = OLD_SNAPSHOT_FORMAT.matcher(id);

        if(oldMatcher.matches()) {
            var year = Integer.parseInt(oldMatcher.group(1));

            if(year > 25) {
                return APRIL_FOOLS;
            }

            return SNAPSHOT_LEGACY;
        }

        if(SNAPSHOT_FORMAT.matcher(id).matches()) {
            return SNAPSHOT;
        }

        if(PRE_RELEASE_FORMAT.matcher(id).matches()) {
            return PRE_RELEASE;
        }

        if(RELEASE_CANDIDATE_FORMAT.matcher(id).matches()) {
            return RELEASE_CANDIDATE;
        }

        if(RELEASE_FORMAT.matcher(id).matches()) {
            return RELEASE;
        }

        return APRIL_FOOLS;
    }

    private static Builder builder() {
        return new Builder();
    }

    private static final class Builder {
        private UnaryOperator<String> shortHand = UnaryOperator.identity();
        private UnaryOperator<String> displayName = UnaryOperator.identity();
        private @Nullable UnaryOperator<String> article = null;

        private Builder() {
        }

        public Builder shortHand(UnaryOperator<String> shortHand) {
            this.shortHand = shortHand;
            return this;
        }

        public Builder displayName(UnaryOperator<String> displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder article(UnaryOperator<String> article) {
            this.article = article;
            return this;
        }

        private ReleaseType build(String serializedName) {
            return new ReleaseType(serializedName, this);
        }
    }
}

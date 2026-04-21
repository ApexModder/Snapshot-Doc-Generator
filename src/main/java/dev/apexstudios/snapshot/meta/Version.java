package dev.apexstudios.snapshot.meta;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public final class Version {
    private final String id;
    private final ReleaseType releaseType;
    private final MetaData metadata;

    public Version(String id, MetaData metadata) {
        this.id = id;
        this.metadata = metadata;

        releaseType = ReleaseType.from(id);
    }

    public String id() {
        return id;
    }

    public ReleaseType type() {
        return releaseType;
    }

    public MetaData metadata() {
        return metadata;
    }

    public String article() {
        return metadata.articleOverride().orElseGet(() -> releaseType.article(this));
    }

    public String changelog() {
        return releaseType.changelog(this);
    }

    public Optional<String> snowman(boolean forgecraft) {
        return metadata.snowman().get(forgecraft);
    }

    public Optional<String> video(boolean main) {
        return metadata.videos().get(main);
    }

    public String shortHand() {
        return releaseType.shortHand(this);
    }

    public String cleanId() {
        return StringUtils.replaceChars(id, '.', '-');
    }

    public String displayName() {
        var displayName = releaseType.displayName(this);
        return metadata.drop().map(suffix -> displayName + " - " + suffix).orElse(displayName);
    }
}

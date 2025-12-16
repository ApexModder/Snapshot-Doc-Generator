package dev.apexstudios.snapshot.util;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.apexstudios.snapshot.Main;
import dev.apexstudios.snapshot.meta.ReleaseType;
import dev.apexstudios.snapshot.meta.Version;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public final class AppContext {
    private final Path outputDir;
    private final Map<String, Version> versions;

    private AppContext(
            Path outputDir,
            List<Version> versions
    ) {
        this.outputDir = outputDir;
        this.versions = versions.stream().collect(Collectors.toUnmodifiableMap(Version::id, Function.identity()));
    }

    public Path outputDir() {
        return outputDir;
    }

    public Collection<Version> versions() {
        return versions.values();
    }

    public Collection<Version> versions(@Nullable ReleaseType filter) {
        return filter == null ? versions() : versions().stream().filter(version -> version.type() == filter).toList();
    }

    public Optional<Version> findVersion(String id) {
        return Optional.ofNullable(versions.get(id));
    }

    public static AppContext of(
            Path outputDir,
            @Nullable File versionsJson
    ) {
        var versions = parseVersions(versionsJson);
        Main.LOGGER.info("Found {} versions", versions.size());

        return Util.make(() -> new AppContext(
                outputDir,
                versions
        ), context -> {
            versions.forEach(AppContext::validateVersion);
            versions.forEach(version -> validateVersionChain(context, version));
        });
    }

    private static List<Version> parseVersions(@Nullable File versionsJson) {
        try {
            if(versionsJson != null) {
                Main.LOGGER.info("Parsing versions from file: '{}'", versionsJson.getPath());

                try(var reader = new FileInputStream(versionsJson)) {
                    return parseVersions(reader);
                }
            } else {
                Main.LOGGER.info("Parsing versions from jar");
                return parseVersions(Main.class.getResourceAsStream("/versions.json"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Version> parseVersions(InputStream stream) throws IOException {
        var singleList = Version.CODEC.flatComapMap(Collections::singletonList, versions -> versions.size() == 1 ? DataResult.success(versions.getFirst()) : DataResult.error(() -> "Expected list of size 1!"));
        var listCodec = Codec.withAlternative(Version.CODEC.listOf(), singleList);

        try(var reader = new InputStreamReader(stream)) {
            var json = new Gson().fromJson(reader, JsonElement.class);
            return listCodec.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
        }
    }

    private static void validateVersion(Version version) {
        if(version.next().isEmpty() && version.previous().isEmpty())
            throw new IllegalStateException("Version: '" + version.id() + "' must have either 'next' or 'previous' property");
    }

    private static void validateVersionChain(AppContext context, Version version) {
        version.next().ifPresent(nextID -> validateVersionChain(context, version, Util.make(Sets::newHashSet, set -> set.add(version.id())), nextID, "next", Version::next));
        version.previous().ifPresent(previousID -> validateVersionChain(context, version, Util.make(Sets::newHashSet, set -> set.add(version.id())), previousID, "previous", Version::previous));

        if(version.next().isPresent() && version.primer(context).isEmpty()) {
            throw new IllegalStateException("Could not determine primer for verion '" + version.id() + "'");
        }
    }

    private static void validateVersionChain(AppContext context, Version current, Set<String> chain, String otherID, String key, Function<Version, Optional<String>> itr) {
        if(!chain.add(otherID))
            throw new IllegalStateException("Version '" + current.id() + "' has circular '" + key + "' property");

        var other = context.findVersion(otherID);

        if(other.isEmpty())
            throw new IllegalStateException("Version '" + current.id() + "' specified '" + key + "' as '" + otherID + "' but no version could be found");

        var itrVer = other.get();
        itr.apply(itrVer).ifPresent(itrID -> validateVersionChain(context, itrVer, chain, itrID, key, itr));
    }
}

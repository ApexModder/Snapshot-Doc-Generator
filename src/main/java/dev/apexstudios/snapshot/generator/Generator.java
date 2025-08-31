package dev.apexstudios.snapshot.generator;

import dev.apexstudios.snapshot.Main;
import dev.apexstudios.snapshot.meta.Version;
import dev.apexstudios.snapshot.util.AppContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class Generator {
    private final String id;
    private final String ext;
    private final IGenerator generator;

    private Generator(String id, String ext, IGenerator generator) {
        this.id = id;
        this.ext = ext;
        this.generator = generator;
    }

    public void generate(AppContext context, Version version) {
        try {
            Main.LOGGER.info("Running generator: '{}'", id);
            var outputDir = context.outputDir().resolve(version.shortHand());
            var outputFile = outputDir.resolve(id + ext);
            var output = generator.generate(context, version);

            Files.createDirectories(outputDir);
            Files.deleteIfExists(outputFile);
            Files.writeString(outputFile, output, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final List<Generator> GENERATORS = List.of(
            new Generator("generic", ".md", new GenericGenerator()),
            new Generator("json", ".json", new JsonGenerator()),
            new Generator("neoforge", ".md", new DiscordGenerator(false)),
            new Generator("forgecraft", ".md", new DiscordGenerator(true))
    );

    public static Generator valueOf(String id) {
        for(var generator : GENERATORS) {
            if(generator.id.equalsIgnoreCase(id))
                return generator;
        }

        throw new IllegalStateException("Unknown Generator: '" + id + "'");
    }
}

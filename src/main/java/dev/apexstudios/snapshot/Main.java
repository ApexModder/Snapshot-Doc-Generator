package dev.apexstudios.snapshot;

import com.google.common.collect.Sets;
import dev.apexstudios.snapshot.generator.Generator;
import dev.apexstudios.snapshot.meta.Version;
import dev.apexstudios.snapshot.util.AppContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.util.PathConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class Main {
    public static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        var parser = new OptionParser();
        var helpSpec = parser.acceptsAll(List.of("help", "h", "?"), "Shows the help menu").forHelp();
        var versionsJsonSpec = parser.accepts("versions", "Custom versions.json file").withRequiredArg().ofType(File.class).withValuesSeparatedBy(',');
        var listSpec = parser.acceptsAll(List.of("list", "l"), "List all known versions");
        var versionSpec = parser.acceptsAll(List.of("version", "v"), "Version id to process").withRequiredArg();
        var outputDirSpec = parser.acceptsAll(List.of("out", "o"), "Directory to store generated files").withRequiredArg().withValuesConvertedBy(new PathConverter()).defaultsTo(Path.of("out"));
        var freshRunSpec = parser.acceptsAll(List.of("fresh", "f"), "Clean out output directory before running");
        var generatorSpec = parser.acceptsAll(List.of("generator", "g"), "Generator type to run").withRequiredArg().defaultsTo("generic").withValuesSeparatedBy(',');
        var allSpec = parser.accepts("all", "Generate all known versions");

        var options = parser.parse(args);

        if(!options.hasOptions() || options.has(helpSpec)) {
            parser.printHelpOn(System.out);
            return;
        }

        var context = AppContext.of(
                options.valueOf(outputDirSpec),
                options.valueOf(versionsJsonSpec)
        );

        if(options.has(listSpec)) {
            listVersions(context);
            return;
        }

        var unknownVersions = Sets.<String>newHashSet();
        var toProcess = Sets.<Version>newLinkedHashSet();

        gatherVersionIds(
                context,
                options.valuesOf(versionSpec),
                unknownVersions,
                toProcess,
                options.has(allSpec)
        );

        if(!unknownVersions.isEmpty()) {
            LOGGER.error("Failed to process {} unknown versions", unknownVersions.size());
            LOGGER.error(String.join(", ", unknownVersions));
            return;
        }

        if(options.has(freshRunSpec) && Files.exists(context.outputDir())) {
            LOGGER.info("Cleaning output directory: '{}'", context.outputDir());
            safeDeleteDirectory(context.outputDir());
        }

        var generators = options.valuesOf(generatorSpec).stream().map(Generator::valueOf).collect(Collectors.toSet());

        toProcess.forEach(version -> {
            LOGGER.info("Processing version: '{}'", version.id());
            generators.forEach(generator -> generator.generate(context, version));
        });
    }

    private static void listVersions(AppContext context) {
        LOGGER.info(context.versions().stream().map(Version::id).collect(Collectors.joining(", ")));
    }

    private static void gatherVersionIds(AppContext context, List<String> versionIds, Set<String> unknownVersions, Set<Version> toProcess, boolean all) {
        versionIds.forEach(id -> context
                .findVersion(id)
                .ifPresentOrElse(toProcess::add, () -> unknownVersions.add(id))
        );

        if(all) {
            toProcess.addAll(context.versions());
        }
    }

    private static void safeDeleteDirectory(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }

        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if(Files.isHidden(dir)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(!Files.isHidden(file)) {
                    Files.delete(file);
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, @Nullable IOException exc) throws IOException {
                if(exc != null) {
                    throw exc;
                }

                if(!dir.equals(root) && !Files.isHidden(dir)) {
                    Files.delete(dir);
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }
}

package dev.apexstudios.snapshot;

import com.google.common.collect.Sets;
import dev.apexstudios.snapshot.generator.Generator;
import dev.apexstudios.snapshot.meta.Version;
import dev.apexstudios.snapshot.util.AppContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.util.PathConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    public static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        var parser = new OptionParser();
        var helpSpec = parser.acceptsAll(List.of("help", "h", "?"), "Shows the help menu").forHelp();
        var versionsJsonSpec = parser.accepts("versions", "Custom versions.json file").withRequiredArg().ofType(File.class).withValuesSeparatedBy(',');
        var listSpec = parser.acceptsAll(List.of("list", "l"), "List all known versions");
        var versionSpec = parser.acceptsAll(List.of("version", "v"), "Version id to process").withRequiredArg();
        var outputDirSpec = parser.acceptsAll(List.of("out", "o"), "Directory to store generated files").withRequiredArg().withValuesConvertedBy(new PathConverter()).defaultsTo(Path.of("out"));
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

        if(all)
            toProcess.addAll(context.versions());
    }
}

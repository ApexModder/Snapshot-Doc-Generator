package dev.apexstudios.snapshot.generator;

import dev.apexstudios.snapshot.meta.Version;
import dev.apexstudios.snapshot.util.AppContext;

final class DiscordGenerator extends MarkdownGenerator {
    private final boolean forgeCraft;

    DiscordGenerator(boolean forgeCraft) {
        this.forgeCraft = forgeCraft;
    }

    @Override
    protected void generate(AppContext context, Version version, StringBuilder output) {
        heading(output, 1);
        output.append(version.displayName());
        newLine(output);

        newLine(output);

        listItem(output);
        link(output, "Primer", version.primer(context), false);
        newLine(output);

        listItem(output);
        link(output, "Article", version.article(), true);
        newLine(output);

        listItem(output);
        link(output, "Changelog", version.changelog(), false);
        newLine(output);

        listItem(output);
        link(output, "Notion", version.notion(), false);
        newLine(output);

        listItem(output);
        link(output, "SnowMan", version.snowman().get(forgeCraft), false);
        newLine(output);

        if(forgeCraft) {
            subHeading(output);
            italic(output, "Ping Mikey for access");
            newLine(output);
        }

        newLine(output);

        bold(output, "SlicedLimes Videos");
        output.append(':');
        newLine(output);

        listItem(output);
        link(output, "Main", version.videos().main(), true);
        newLine(output);

        listItem(output);
        link(output, "Pack", version.videos().pack(), true);

        if(!forgeCraft) {
            newLine(output);
            newLine(output);
            spoiler(output, "<@&1067092163520909374>"); // NeoForge: '@Snapshot Alarm'
        }
    }
}

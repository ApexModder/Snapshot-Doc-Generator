package dev.apexstudios.snapshot.generator;

import com.google.common.collect.Maps;
import dev.apexstudios.snapshot.meta.Version;
import dev.apexstudios.snapshot.util.AppContext;
import dev.apexstudios.snapshot.util.Util;

final class GenericGenerator extends MarkdownGenerator {
    @Override
    public void generate(AppContext context, Version version, StringBuilder output) {
        heading(output, 1);
        output.append(version.displayName());
        newLine(output);

        newLine(output);
        table(output, Util.make(Maps::newLinkedHashMap, table -> {
            table.put("ID", version.id());
            table.put("Release Type", version.type().toString());
        }));
        newLine(output);

        newLine(output);
        heading(output, 3);
        output.append("Links");
        newLine(output);

        listItem(output);
        link(output, "Primer", version.metadata().primer(), false);
        newLine(output);

        listItem(output);
        link(output, "Article", version.article(), false);
        newLine(output);

        listItem(output);
        link(output, "Changelog", version.changelog(), false);
        newLine(output);

        newLine(output);
        heading(output, 3);
        output.append("SnowMan");
        newLine(output);

        listItem(output);
        link(output, "NeoForge", version.snowman(false), false);
        newLine(output);

        listItem(output);
        link(output, "ForgeCraft", version.snowman(true), false);
        newLine(output);

        newLine(output);
        heading(output, 3);
        output.append("Update Videos");
        newLine(output);

        listItem(output);
        link(output, "Main", version.video(true), false);
        newLine(output);

        listItem(output);
        link(output, "Resource/Data Pack", version.video(false), false);
    }
}

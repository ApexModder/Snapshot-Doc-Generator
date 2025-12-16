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
            table.put("Next Version", version.next().orElse("N/A"));
            table.put("Previous Version", version.previous().orElse("N/A"));
        }));
        newLine(output);

        newLine(output);
        heading(output, 3);
        output.append("Links");
        newLine(output);

        listItem(output);
        link(output, "Primer", version.primer(context), false);
        newLine(output);

        listItem(output);
        link(output, "Article", version.article(), false);
        newLine(output);

        listItem(output);
        link(output, "Changelog", version.changelog(), false);
        newLine(output);

        listItem(output);
        link(output, "Notion", version.notion(), false);
        newLine(output);

        newLine(output);
        heading(output, 3);
        output.append("SnowMan");
        newLine(output);

        listItem(output);
        link(output, "NeoForge", version.snowman().neoforge(), false);
        newLine(output);

        listItem(output);
        link(output, "ForgeCraft", version.snowman().forgecraft(), false);
        newLine(output);

        newLine(output);
        heading(output, 3);
        output.append("Update Videos");
        newLine(output);

        listItem(output);
        link(output, "Main", version.videos().main(), false);
        newLine(output);

        listItem(output);
        link(output, "Resource/Data Pack", version.videos().pack(), false);
    }

    /*
    List.of(
                version.header(),
                "",
                "| ID | Release Type | Next Version | Previous Version |",
                "|:---:|:---:|:---:|:---:|",
                "| " + version.id() + " | " + version.type() + " | " + version.next().orElse("N/A") + " | " + version.previous().orElse("N/A") + " |",
                "",
                "### Links",
                "- " + link("Primer", version.primer(), false),
                "- " + link("Article", version.article(), false),
                "- " + link("Changelog", version.changelog(), false),
                "- " + link(" Notion", version.notion(), false),
                "",
                "### Update Videos",
                "- " + link("Main", version.videos().main(), false),
                "- " + link("Resource/Data Pack", version.videos().pack(), false),
                "",
                "### Snowman",
                "- " + link("NeoForge", version.snowman().neoforge(), false),
                "- " + link("ForgeCraft", version.snowman().forgecraft(), false)
        )
     */
}

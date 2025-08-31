package dev.apexstudios.snapshot.generator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

public abstract class MarkdownGenerator extends StringGenerator {
    protected static void newLine(StringBuilder output) {
        output.append('\n');
    }

    protected static void heading(StringBuilder output, int num) {
        output.append(Strings.repeat("#", num)).append(' ');
    }

    protected static void subHeading(StringBuilder output) {
        output.append("-# ");
    }

    protected static void listItem(StringBuilder output) {
        output.append("- ");
    }

    protected static void link(StringBuilder output, String key, Optional<String> url, boolean embed) {
        link(output, key, url.orElse("N/A"), embed);
    }

    protected static void link(StringBuilder output, String key, String url, boolean embed) {
        bold(output, key);
        output.append(": ");

        var shouldEmbed = url.equalsIgnoreCase("N/A") || embed;
        link(output, url, !shouldEmbed);
    }

    protected static void link(StringBuilder output, String url, boolean embed) {
        if(embed)
            wrap(output, '<', '>', url);
        else
            output.append(url);
    }

    protected static void table(StringBuilder output, Map<String, String> table) {
        var formatted = Lists.<Triple<String, String, String>>newLinkedList();

        for(var entry : table.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var len = Math.max(key.length(), value.length()) + 2;

            formatted.add(Triple.of(
                    ' ' + StringUtils.center(key, len) + ' ',
                    ':' + StringUtils.repeat('-', len) + ':',
                    ' ' + StringUtils.center(value, len) + ' '
            ));
        }

        var keyOutput = new StringBuilder("|");
        var separatorOutput = new StringBuilder("|");
        var valueOutput = new StringBuilder("|");

        for(var elem : formatted) {
            var key = elem.getLeft();
            var separator = elem.getMiddle();
            var value = elem.getRight();

            keyOutput.append(key).append('|');
            separatorOutput.append(separator).append('|');
            valueOutput.append(value).append('|');
        }

        output.append(keyOutput);
        newLine(output);
        output.append(separatorOutput);
        newLine(output);
        output.append(valueOutput);
    }

    protected static void wrap(StringBuilder output, String wrapper, String text) {
        wrap(output, wrapper, wrapper, text);
    }

    protected static void wrap(StringBuilder output, String prefix, String suffix, String text) {
        output.append(prefix).append(text).append(suffix);
    }

    protected static void wrap(StringBuilder output, char wrapper, String text) {
        wrap(output, wrapper, wrapper, text);
    }

    protected static void wrap(StringBuilder output, char prefix, char suffix, String text) {
        output.append(prefix).append(text).append(suffix);
    }

    protected static void bold(StringBuilder output, String text) {
        wrap(output, "**", text);
    }

    protected static void italic(StringBuilder output, String text) {
        wrap(output, '_', text);
    }

    protected static void spoiler(StringBuilder output, String text) {
        wrap(output, "||", text);
    }
}

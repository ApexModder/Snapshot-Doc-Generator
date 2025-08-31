package dev.apexstudios.snapshot.generator;

import dev.apexstudios.snapshot.meta.Version;
import dev.apexstudios.snapshot.util.AppContext;
import java.io.IOException;

public abstract class StringGenerator implements IGenerator {
    @Override
    public String generate(AppContext context,Version version) throws IOException {
        var output = new StringBuilder();
        generate(context, version, output);
        return output.toString();
    }

    protected abstract void generate(AppContext context, Version version, StringBuilder output);
}

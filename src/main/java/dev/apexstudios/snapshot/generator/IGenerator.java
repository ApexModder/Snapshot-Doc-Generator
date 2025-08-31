package dev.apexstudios.snapshot.generator;

import dev.apexstudios.snapshot.meta.Version;
import dev.apexstudios.snapshot.util.AppContext;
import java.io.IOException;

public interface IGenerator {
    String generate(AppContext context, Version version) throws IOException;
}

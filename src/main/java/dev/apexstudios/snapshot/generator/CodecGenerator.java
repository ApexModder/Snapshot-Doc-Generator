package dev.apexstudios.snapshot.generator;

import com.mojang.serialization.DynamicOps;
import dev.apexstudios.snapshot.meta.Version;
import dev.apexstudios.snapshot.util.AppContext;
import dev.apexstudios.snapshot.util.Codecs;
import java.io.IOException;

public abstract class CodecGenerator<T> implements IGenerator {
    @Override
    public String generate(AppContext context, Version version) throws IOException {
        return generate(context, Codecs.encode(dynamicOps(), Version.CODEC, version));
    }

    protected abstract DynamicOps<T> dynamicOps();

    protected abstract String generate(AppContext context, T encoded) throws IOException;
}

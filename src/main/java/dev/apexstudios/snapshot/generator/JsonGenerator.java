package dev.apexstudios.snapshot.generator;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.apexstudios.snapshot.util.AppContext;
import dev.apexstudios.snapshot.util.Jsons;
import java.io.IOException;

final class JsonGenerator extends CodecGenerator<JsonElement> {
    @Override
    protected DynamicOps<JsonElement> dynamicOps() {
        return JsonOps.INSTANCE;
    }

    @Override
    protected String generate(AppContext context, JsonElement json) throws IOException {
        return Jsons.write(json, writer -> writer.setIndent("    "), Jsons.KEY_COMPARATOR);
    }
}

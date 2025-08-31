package dev.apexstudios.snapshot.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import org.jetbrains.annotations.Nullable;

public interface Jsons {
    ToIntFunction<String> FIXED_ORDER_FIELDS = Util.make(Object2IntOpenHashMap::new, map -> {
        map.put("display_name", 0);
        map.put("id", 1);
        map.put("article", 2);
        map.put("changelog", 3);
        map.put("primer", 4);
        map.put("snowman", 5);
        map.put("videos", 6);
        map.put("type", 7);
        map.put("next", 8);
        map.put("previous", 9);

        map.put("forgecraft", 0);
        map.put("neoforge", 1);

        map.put("main", 0);
        map.put("pack", 1);

        map.defaultReturnValue(10);
    });

    Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS);

    static <T> JsonElement encode(Codec<T> codec, T val) {
        return Codecs.encode(JsonOps.INSTANCE, codec, val);
    }

    static void write(JsonWriter writer, @Nullable JsonElement json, @Nullable Comparator<String> sorter) throws IOException {
        if(json == null || json.isJsonNull())
            writer.nullValue();
        else if(json.isJsonPrimitive()) {
            var primitive = json.getAsJsonPrimitive();

            if(primitive.isNumber())
                writer.value(primitive.getAsNumber());
            else if(primitive.isBoolean())
                writer.value(primitive.getAsBoolean());
            else
                writer.value(primitive.getAsString());
        } else if(json.isJsonArray()) {
            writer.beginArray();

            for(var child : json.getAsJsonArray()) {
                write(writer, child, sorter);
            }

            writer.endArray();
        } else if(json.isJsonObject()) {
            writer.beginObject();

            for(var entry : sort(json.getAsJsonObject().entrySet(), sorter)) {
                writer.name(entry.getKey());
                write(writer, entry.getValue(), sorter);
            }

            writer.endObject();
        } else
            throw new IllegalArgumentException("Couldn't write: " + json.getClass());
    }

    static String write(@Nullable JsonElement json, Consumer<JsonWriter> settings, @Nullable Comparator<String> sorter) throws IOException {
        var str = new StringWriter();

        try(var writer = new JsonWriter(str)) {
            settings.accept(writer);
            write(writer, json, sorter);
        }

        return str.toString();
    }

    static <T> String write(Codec<T> codec, T val, Consumer<JsonWriter> settings, @Nullable Comparator<String> sorter) throws IOException {
        return write(encode(codec, val), settings, sorter);
    }

    private static Collection<Map.Entry<String, JsonElement>> sort(Collection<Map.Entry<String, JsonElement>> entries, @Nullable Comparator<String> sorter) {
        if(sorter == null)
            return entries;

        var result = Lists.newArrayList(entries);
        result.sort(Map.Entry.comparingByKey(sorter));
        return result;
    }
}

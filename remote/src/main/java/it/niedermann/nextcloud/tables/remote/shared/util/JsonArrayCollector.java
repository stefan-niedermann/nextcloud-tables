package it.niedermann.nextcloud.tables.remote.shared.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JsonArrayCollector implements Collector<JsonElement, JsonArray, JsonElement> {

    @Override
    public Supplier<JsonArray> supplier() {
        return JsonArray::new;
    }

    @Override
    public BiConsumer<JsonArray, JsonElement> accumulator() {
        return JsonArray::add;
    }

    @Override
    public BinaryOperator<JsonArray> combiner() {
        return ((jsonArray, jsonArray2) -> {
            final var result = new JsonArray();
            result.addAll(jsonArray);
            result.addAll(jsonArray2);
            return result;
        });
    }

    @Override
    public Function<JsonArray, JsonElement> finisher() {
        return result -> result;
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
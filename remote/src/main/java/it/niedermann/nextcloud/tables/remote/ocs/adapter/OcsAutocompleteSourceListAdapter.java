package it.niedermann.nextcloud.tables.remote.ocs.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;
import it.niedermann.nextcloud.tables.remote.shared.util.JsonArrayCollector;

public class OcsAutocompleteSourceListAdapter implements JsonSerializer<List<OcsAutocompleteResult.OcsAutocompleteSource>> {

    @Override
    public JsonElement serialize(List<OcsAutocompleteResult.OcsAutocompleteSource> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }

        return src
                .stream()
                .map(context::serialize)
                .collect(new JsonArrayCollector());
    }
}
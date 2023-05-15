package it.niedermann.nextcloud.tables.remote.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class ValueTypeAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String read(JsonReader in) throws IOException {
        final var token = in.peek();
        //noinspection SwitchStatementWithTooFewBranches
        switch (token) {
            case BEGIN_ARRAY: {
                in.beginArray();
                final Collection<String> arrayValues = new LinkedList<>();
                while (in.hasNext()) {
                    arrayValues.add(in.nextString());
                }
                final var serializedArray = "[" + String.join(",", arrayValues) + "]";
                in.endArray();
                return serializedArray;
            }
            default:
                return in.nextString();
        }
    }
}

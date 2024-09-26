package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class SelectionDefault implements Externalizable {

    private JsonElement selectionDefault;

    public SelectionDefault() {

    }

    public SelectionDefault(@Nullable JsonElement value) {
        selectionDefault = value;
    }

    @NonNull
    public JsonElement getValue() {
        return selectionDefault == null ? JsonNull.INSTANCE : selectionDefault;
    }

    @NonNull
    @Override
    public String toString() {
        return selectionDefault == null ? "" : selectionDefault.toString();
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeUTF(selectionDefault.toString());
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException {
        selectionDefault = JsonParser.parseString(objectInput.readUTF());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectionDefault that = (SelectionDefault) o;
        return Objects.equals(selectionDefault, that.selectionDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(selectionDefault);
    }
}

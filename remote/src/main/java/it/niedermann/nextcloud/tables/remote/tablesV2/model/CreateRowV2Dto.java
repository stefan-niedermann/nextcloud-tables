package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.io.Serializable;

public record CreateRowV2Dto(
        @NonNull JsonElement data
) implements Serializable {
}

package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public record FilterConstraints(
        @NonNull String term
) implements Serializable {
}
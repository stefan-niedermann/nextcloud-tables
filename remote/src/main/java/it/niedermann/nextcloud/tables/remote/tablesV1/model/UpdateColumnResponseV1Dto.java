package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import it.niedermann.nextcloud.tables.remote.shared.model.RemoteDto;

public record UpdateColumnResponseV1Dto(
        @SerializedName("id")
        Long remoteId
) implements Serializable, RemoteDto {
}

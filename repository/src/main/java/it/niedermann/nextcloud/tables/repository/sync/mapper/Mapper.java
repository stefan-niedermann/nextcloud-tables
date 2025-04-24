package it.niedermann.nextcloud.tables.repository.sync.mapper;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface Mapper<A, B> {

    @NonNull
    A toDto(@NonNull B entity);

    @NonNull
    B toEntity(@NonNull A dto);

    @NonNull
    default List<A> toDtoList(@NonNull Collection<B> entities) {
        return entities
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }

    @NonNull
    default List<B> toEntityList(@NonNull Collection<A> dtos) {
        return dtos
                .stream()
                .filter(Objects::nonNull)
                .map(this::toEntity)
                .toList();
    }
}

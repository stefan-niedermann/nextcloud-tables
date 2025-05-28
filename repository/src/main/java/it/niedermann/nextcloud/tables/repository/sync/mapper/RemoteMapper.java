package it.niedermann.nextcloud.tables.repository.sync.mapper;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface RemoteMapper<DtoType, EntityType> {

    DtoType toDto(EntityType entity);

    EntityType toEntity(DtoType dto);

    @NonNull
    default List<DtoType> toDtoList(@NonNull Collection<EntityType> entities) {
        return entities
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }

    @NonNull
    default List<EntityType> toEntityList(@NonNull Collection<DtoType> dtos) {
        return dtos
                .stream()
                .filter(Objects::nonNull)
                .map(this::toEntity)
                .toList();
    }
}

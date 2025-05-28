package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EUserGroupTypeV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.UserGroupV2Dto;

public class UserGroupV2MapperTest {

    private UserGroupV2Mapper mapper;

    @Before
    public void setup() {
        this.mapper = UserGroupV2Mapper.INSTANCE;
    }

    @Test
    public void toDto() {

        final var entity = new UserGroup();
        entity.setRemoteId("foo");
        entity.setType(EUserGroupType.USER);
        entity.setDisplayName("John Doe");

        final var dto = mapper.toDto(entity);

        assertEquals("foo", dto.remoteId());
        assertEquals("John Doe", dto.key());
        assertEquals(EUserGroupTypeV2Dto.USER, dto.type());
    }

    @Test
    public void toEntity() {

        final var dto = new UserGroupV2Dto("foo", "John Doe", EUserGroupTypeV2Dto.USER);

        final var entity = mapper.toEntity(dto);

        assertEquals("foo", entity.getRemoteId());
        assertEquals("John Doe", entity.getDisplayName());
        assertEquals(EUserGroupType.USER, entity.getType());
    }
}
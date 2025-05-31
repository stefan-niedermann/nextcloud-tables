package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;

import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EUserGroupTypeV2Dto;

public class EUserGroupTypeV2MapperTest {

    private EUserGroupTypeV2Mapper mapper;

    @Before
    public void setup() {
        this.mapper = EUserGroupTypeV2Mapper.INSTANCE;
    }

    @Test
    public void toDto() {
        assertEquals(EUserGroupTypeV2Dto.USER, mapper.toDto(EUserGroupType.USER));
        assertEquals(EUserGroupTypeV2Dto.GROUP, mapper.toDto(EUserGroupType.GROUP));
        assertEquals(EUserGroupTypeV2Dto.TEAMS, mapper.toDto(EUserGroupType.TEAM));
        assertThrows(Exception.class, () -> mapper.toDto(EUserGroupType.UNKNOWN));
        assertThrows(Exception.class, () -> mapper.toDto(null));
    }

    @Test
    public void toEntity() {
        assertEquals(EUserGroupType.USER, mapper.toEntity(EUserGroupTypeV2Dto.USER));
        assertEquals(EUserGroupType.GROUP, mapper.toEntity(EUserGroupTypeV2Dto.GROUP));
        assertEquals(EUserGroupType.TEAM, mapper.toEntity(EUserGroupTypeV2Dto.TEAMS));
        assertEquals(EUserGroupType.UNKNOWN, mapper.toEntity(null));
    }
}
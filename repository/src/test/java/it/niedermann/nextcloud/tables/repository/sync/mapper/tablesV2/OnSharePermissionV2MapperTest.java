package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import it.niedermann.nextcloud.tables.database.entity.OnSharePermission;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.OnSharePermissionV2Dto;

public class OnSharePermissionV2MapperTest {

    private OnSharePermissionV2Mapper mapper;

    @Before
    public void setup() {
        this.mapper = OnSharePermissionV2Mapper.INSTANCE;
    }

    @Test
    public void toDto_allUnset() {

        final var entity = new OnSharePermission();

        final var dto = mapper.toDto(entity);

        assertEquals(false, dto.read());
        assertEquals(false, dto.create());
        assertEquals(false, dto.update());
        assertEquals(false, dto.delete());
        assertEquals(false, dto.manage());
    }

    @Test
    public void toDto_allTrue() {

        final var entity = new OnSharePermission();

        entity.setRead(true);
        entity.setCreate(true);
        entity.setUpdate(true);
        entity.setDelete(true);
        entity.setManage(true);

        final var dto = mapper.toDto(entity);

        assertEquals(true, dto.read());
        assertEquals(true, dto.create());
        assertEquals(true, dto.update());
        assertEquals(true, dto.delete());
        assertEquals(true, dto.manage());
    }

    @Test
    public void toDto_allFalse() {

        final var entity = new OnSharePermission();

        entity.setRead(false);
        entity.setCreate(false);
        entity.setUpdate(false);
        entity.setDelete(false);
        entity.setManage(false);

        final var dto = mapper.toDto(entity);

        assertEquals(false, dto.read());
        assertEquals(false, dto.create());
        assertEquals(false, dto.update());
        assertEquals(false, dto.delete());
        assertEquals(false, dto.manage());
    }

    @Test
    public void toEntity_allNull() {

        final var dto = new OnSharePermissionV2Dto(null, null, null, null, null);

        final var entity = mapper.toEntity(dto);

        assertFalse(entity.isRead());
        assertFalse(entity.isCreate());
        assertFalse(entity.isUpdate());
        assertFalse(entity.isDelete());
        assertFalse(entity.isManage());
    }

    @Test
    public void toEntity_allTrue() {

        final var dto = new OnSharePermissionV2Dto(true, true, true, true, true);

        final var entity = mapper.toEntity(dto);

        assertTrue(entity.isRead());
        assertTrue(entity.isCreate());
        assertTrue(entity.isUpdate());
        assertTrue(entity.isDelete());
        assertTrue(entity.isManage());
    }

    @Test
    public void toEntity_allFalse() {

        final var dto = new OnSharePermissionV2Dto(false, false, false, false, false);

        final var entity = mapper.toEntity(dto);

        assertFalse(entity.isRead());
        assertFalse(entity.isCreate());
        assertFalse(entity.isUpdate());
        assertFalse(entity.isDelete());
        assertFalse(entity.isManage());
    }
}
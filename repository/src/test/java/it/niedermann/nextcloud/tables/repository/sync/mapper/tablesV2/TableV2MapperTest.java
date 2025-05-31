package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import it.niedermann.nextcloud.tables.database.entity.OnSharePermission;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.OnSharePermissionV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.TableV2Dto;

public class TableV2MapperTest {

    private TableV2Mapper mapper;

    @Before
    public void setup() {
        this.mapper = TableV2Mapper.INSTANCE;
    }

    @Test
    public void toDto() {

        final var onSharePermissionEntity = new OnSharePermission();
        onSharePermissionEntity.setRead(true);
        onSharePermissionEntity.setCreate(true);
        onSharePermissionEntity.setUpdate(true);
        onSharePermissionEntity.setDelete(true);
        onSharePermissionEntity.setManage(true);

        final var entity = new Table();
        entity.setRemoteId(4711L);
        entity.setTitle("foo");
        entity.setDescription("bar");
        entity.setEmoji("\uD83D\uDCCB");
        entity.setOwnership("john");
        entity.setOwnerDisplayName("John Doe");
        entity.setCreatedBy("john");
        entity.setCreatedAt(Instant.parse("2020-02-20T10:15:30.345Z"));
        entity.setLastEditBy("john");
        entity.setLastEditAt(Instant.parse("1999-09-09T11:19:29.999Z"));
        entity.setFavorite(true);
        entity.setArchived(true);
        entity.setShared(true);
        entity.setOnSharePermission(onSharePermissionEntity);

        final var dto = mapper.toDto(entity);

        assertEquals(Long.valueOf(4711), dto.remoteId());
        assertEquals("foo", dto.title());
        assertEquals("bar", dto.description());
        assertEquals("\uD83D\uDCCB", dto.emoji());
        assertEquals("john", dto.ownership());
        assertEquals("John Doe", dto.ownerDisplayName());
        assertEquals("john", dto.createdBy());
        assertEquals(Instant.parse("2020-02-20T10:15:30.345Z"), dto.createdAt());
        assertEquals("john", dto.lastEditBy());
        assertEquals(Instant.parse("1999-09-09T11:19:29.999Z"), dto.lastEditAt());
        assertEquals(true, dto.favorite());
        assertEquals(true, dto.archived());
        assertEquals(true, dto.isShared());
        assertNotNull(dto.onSharePermissions());
        assertEquals(true, dto.onSharePermissions().read());
        assertEquals(true, dto.onSharePermissions().create());
        assertEquals(true, dto.onSharePermissions().update());
        assertEquals(true, dto.onSharePermissions().delete());
        assertEquals(true, dto.onSharePermissions().manage());
    }

    @Test
    public void toEntity() {

        final var onSharePermissionV2Dto = new OnSharePermissionV2Dto(true, true, true, true, true);

        final var dto = new TableV2Dto(
                4711L,
                "foo",
                "\uD83D\uDCCB",
                "bar",
                "john",
                "John Doe",
                "john",
                Instant.parse("2020-02-20T10:15:30.345Z"),
                "john",
                Instant.parse("1999-09-09T11:19:29.999Z"),
                true,
                true,
                true,
                onSharePermissionV2Dto
        );

        final var entity = mapper.toEntity(dto);

        assertEquals("foo", entity.getTitle());
        assertEquals("bar", entity.getDescription());
        assertEquals("\uD83D\uDCCB", entity.getEmoji());
        assertEquals("john", entity.getOwnership());
        assertEquals("John Doe", entity.getOwnerDisplayName());
        assertEquals("john", entity.getCreatedBy());
        assertEquals(Instant.parse("2020-02-20T10:15:30.345Z"), entity.getCreatedAt());
        assertEquals("john", entity.getLastEditBy());
        assertEquals(Instant.parse("1999-09-09T11:19:29.999Z"), entity.getLastEditAt());
        assertTrue(entity.isFavorite());
        assertTrue(entity.isArchived());
        assertTrue(entity.isShared());
        assertNotNull(entity.getOnSharePermission());
        assertTrue(entity.getOnSharePermission().isRead());
        assertTrue(entity.getOnSharePermission().isCreate());
        assertTrue(entity.getOnSharePermission().isUpdate());
        assertTrue(entity.getOnSharePermission().isDelete());
        assertTrue(entity.getOnSharePermission().isManage());
    }

    @Test
    public void toEntity_shouldHandleNullableOnSharePermissionV2Dto() {

        final var dto = new TableV2Dto(
                4711L,
                "foo",
                "\uD83D\uDCCB",
                "bar",
                "john",
                "John Doe",
                "john",
                Instant.parse("2020-02-20T10:15:30.345Z"),
                "john",
                Instant.parse("1999-09-09T11:19:29.999Z"),
                true,
                true,
                true,
                null
        );

        final var entity = mapper.toEntity(dto);

        assertNotNull(entity.getOnSharePermission());
        assertFalse(entity.getOnSharePermission().isRead());
        assertFalse(entity.getOnSharePermission().isCreate());
        assertFalse(entity.getOnSharePermission().isUpdate());
        assertFalse(entity.getOnSharePermission().isDelete());
        assertFalse(entity.getOnSharePermission().isManage());
    }
}
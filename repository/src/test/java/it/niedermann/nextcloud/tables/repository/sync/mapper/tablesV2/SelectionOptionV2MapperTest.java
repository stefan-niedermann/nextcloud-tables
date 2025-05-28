package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.SelectionOptionV2Dto;

public class SelectionOptionV2MapperTest {

    private SelectionOptionV2Mapper mapper;

    @Before
    public void setup() {
        this.mapper = SelectionOptionV2Mapper.INSTANCE;
    }

    @Test
    public void toDto() {

        final var entity = new SelectionOption();
        entity.setRemoteId(4711L);
        entity.setLabel("foo");

        final var dto = mapper.toDto(entity);

        assertEquals(Long.valueOf(4711L), dto.remoteId());
        assertEquals("foo", dto.label());
    }

    @Test
    public void toEntity() {

        final var dto = new SelectionOptionV2Dto(4711L, "foo");

        final var entity = mapper.toEntity(dto);

        assertEquals(Long.valueOf(4711L), entity.getRemoteId());
        assertEquals("foo", entity.getLabel());
    }
}